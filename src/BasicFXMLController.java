import java.time.LocalDate;
import java.util.*;
import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.scene.text.Text;


public class BasicFXMLController {
    public Label notReadyLabel;
    // 2d array used to determine question frequency. Indicies are used to represent the multiplication questions
    // e.g. multiplicationArray[3][6] represents the question 3x6
    private List<List<LocalDate>> multiplicationDateArray = new ArrayList<List<LocalDate>>();

    // questionRow and questionColumn represent the indicies of the multiplicationArray. Questions are presented one row at a time
    // e.g. row 2 is 2x1, 2x2, ..., 2x11, 2x12
    private Integer questionsRow = 1;
    private Integer maxTimesTable = 2;

    private JsonNode congratsMessages;
    private int currentQuestionsComplete = 0;
    List<List<Question>> questions = generateQuestions();

    public Button nextButton;
    @FXML
    public GridPane questionsGrid = new GridPane();
    private boolean inRandomSection = false;
    private int numRandomQuestions;

    @FXML
    private Label label;

    public void initialize() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        congratsMessages = objectMapper.readTree(getClass().getResource("congratulations.json"));
        displayQuestionsRow(questions.get(questionsRow));
    }

    //TODO should only be done when json file with questions doesn't already exist
    public List<List<Question>> generateQuestions() {
        List<List<Question>> questions = new ArrayList<>();
        for (int i = 0; i <= maxTimesTable; i++) {
            List<Question> row = new ArrayList<>();
            for (int j = 0; j <= maxTimesTable; j++) {
                row.add(new Question(i,j));
            }
            questions.add(row);
        }
        return questions;
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
                                questionsGrid.getChildren().remove(textField);
                                Label answerText = new Label();
                                answerText.setText(currentQuestion.answer + "");
                                Label congratsText = new Label();
                                congratsText.setText(getCongratsMsg());
                                questionsGrid.add(answerText, 2, finalI);
                                questionsGrid.add(congratsText, 3, finalI);
                                currentQuestionsComplete++;
                            } else {
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
                questionsGrid.add(currentQuestion.label, 0, i);
                questionsGrid.add(equalsLabel, 1, i);
                questionsGrid.add(textField, 2, i);
            }
    }
//                            Integer currentMultiplier = multiplicationMultiplierArray.get(questionRow).get(questionColumn);
//                            multiplicationDateArray.get(questionRow).set(questionColumn, LocalDate.now().plusDays(currentMultiplier));
//                            Integer newVal;
//                            newVal = currentMultiplier * 2;
//                            multiplicationMultiplierArray.get(questionRow).set(questionColumn, newVal);

    public void initializeArray() {
        for (int i = 0; i < 13; i++) {
            List<LocalDate> row = new ArrayList<LocalDate>();
            for (int j = 0; j < 13; j++) {
                row.add(LocalDate.now());
            }
            multiplicationDateArray.add(row);
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
            // win
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