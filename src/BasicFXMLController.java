import java.io.File;
import java.time.LocalDate;
import java.util.*;
import java.io.IOException;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


public class BasicFXMLController {
    public Label notReadyLabel;
    private JsonNode congratsMessages;
    public ObjectMapper mapper = new ObjectMapper();

    private Integer questionsRow = 1;
    private Integer maxTimesTable = 3;
    private int currentQuestionsComplete = 0;

    List<List<Question>> questions = loadQuestions();

    public Button nextButton;
    @FXML
    public GridPane questionsGrid = new GridPane();
    private boolean inRandomSection = false;
    private int numRandomQuestions;

    @FXML
    private Label label;

    public void initialize() throws IOException {
        congratsMessages = mapper.readTree(getClass().getResource("congratulations.json"));
        displayQuestionsRow(questions.get(questionsRow));
    }

    public List<List<Question>> loadQuestions() {
        mapper.registerModule(new JavaTimeModule());
        List<List<Question>> questionsList = new ArrayList<>();
            try {
                File questionsJsonFile = new File("questions.json");
                if (questionsJsonFile.exists()) {
                    questionsList = mapper.readValue(questionsJsonFile, new TypeReference<List<List<Question>>>() {
                    });
                }
                else{
                    questionsList = new ArrayList<>();
                    for (int i = 0; i <= maxTimesTable; i++) {
                        List<Question> row = new ArrayList<>();
                        for (int j = 0; j <= maxTimesTable; j++) {
                            Question question = new Question(i, j);
                            question.dueDate = LocalDate.now();
                            row.add(question);
                        }
                        questionsList.add(row);
                    }
                    mapper.writerWithDefaultPrettyPrinter().writeValue(new File("questions.json"), questionsList);
                }
            } catch (IOException err) {
                err.printStackTrace();
        }
        return questionsList;
    }

    public List<Question> generateRandomQuestions() {
        List<Question> questionsList = new ArrayList<>();
        for (int i = 0; i <= maxTimesTable; i++) {
            for (int j = 0; j <= maxTimesTable; j++) {
                Question question = questions.get(i).get(j);
                if (question.dueDate.isBefore(LocalDate.now()) || question.dueDate.isEqual(LocalDate.now())) {
                    questionsList.add(question);
                }
            }
        }
        Collections.shuffle(questionsList);
        numRandomQuestions = questionsList.size();
        return questionsList;
    }

    public void displayQuestionsRow(List<Question> questions) {
            for (int i = 0; i < questions.size(); i++) {
                Label errorText = new Label();
                Question currentQuestion = questions.get(i);
                int finalI = i;
                TextField textField = new TextField();
                textField.setOnKeyPressed(keyEvent -> {
                    questionsGrid.getChildren().remove(errorText);
                    if (keyEvent.getCode() == KeyCode.ENTER) {
                        try {
                            if (Integer.parseInt(textField.getText()) == currentQuestion.answer) {
                                if (inRandomSection) {
                                    currentQuestion.dueDate = currentQuestion.dueDate.plusDays(currentQuestion.daysToAdd);
                                    if (currentQuestion.dateLastFailed == null || !currentQuestion.dateLastFailed.equals(LocalDate.now())) {
                                        currentQuestion.daysToAdd = currentQuestion.daysToAdd * 2;
                                    }
                                }
                                questionsGrid.getChildren().remove(textField);
                                Label answerText = new Label();
                                answerText.setText(currentQuestion.answer + "");
                                Label congratsText = new Label();
                                congratsText.setText(getCongratsMsg());
                                questionsGrid.add(answerText, 2, finalI);
                                questionsGrid.add(congratsText, 3, finalI);
                                currentQuestionsComplete++;
                            } else {
                                currentQuestion.dateLastFailed = LocalDate.now();
                                currentQuestion.daysToAdd = 1;
                                errorText.setText("Try again!");
                                questionsGrid.add(errorText, 3, finalI);
                            }
                        } catch (NumberFormatException e) {
                            errorText.setText("Enter a number please!");
                            questionsGrid.add(errorText, 3, finalI);
                        }
                    }
                });
                Label equalsLabel = new Label(" = ");
                questionsGrid.add(new Label(currentQuestion.labelText), 0, i);
                questionsGrid.add(equalsLabel, 1, i);
                questionsGrid.add(textField, 2, i);
            }
    }

    public String getCongratsMsg() {
        int numOfMessages = congratsMessages.size();
        int msgId = (int) Math.floor((Math.random()*numOfMessages));
        return congratsMessages.get(Integer.toString(msgId)).asText();
    }

    @FXML
    public void handleNextButtonPress(ActionEvent actionEvent) {
        notReadyLabel.setVisible(false);
        if (inRandomSection && currentQuestionsComplete == numRandomQuestions) {
            try {
                mapper.writerWithDefaultPrettyPrinter().writeValue(new File("questions.json"), questions);
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("WIN");
        }
        else if (currentQuestionsComplete == maxTimesTable+1) {
            questionsGrid.getChildren().clear();
            questionsRow++;
            if (questionsRow > maxTimesTable) {
                inRandomSection = true;
                displayQuestionsRow(generateRandomQuestions());
            }
            else {
                displayQuestionsRow(questions.get(questionsRow));
            }
            currentQuestionsComplete = 0;
        }
        else {
            notReadyLabel.setVisible(true);
        }
    }
}