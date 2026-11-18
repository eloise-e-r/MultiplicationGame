import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


public class BasicFXMLController {
    // 2d array used to determine question frequency. Indicies are used to represent the multiplication questions
    // e.g. multiplicationArray[3][6] represents the question 3x6
    private List<List<Integer>> multiplicationArray = new ArrayList<List<Integer>>();

    // questionRow and questionColumn represent the indicies of the multiplicationArray. Questions are presented one row at a time
    // e.g. row 2 is 2x1, 2x2, ..., 2x11, 2x12
    private Integer questionRow = 1;
    private Integer questionColumn = 1;

    private Integer maxTimesTable = 4;

    private JsonNode congratsMessages;

    @FXML
    public GridPane questions;

    @FXML
    private Label label;

    public void initialize() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        congratsMessages = objectMapper.readTree(getClass().getResource("congratulations.json"));
        generateQuestions();
    }

    @FXML
    private void handleButtonAction(ActionEvent event) {
        label.setText("Hello World!");
    }

    public void generateQuestions() {
        for (int i = 1; i <= maxTimesTable; i++) {
            Label question = new Label();
            question.setText(questionRow.toString() + " x " + i + " = ");
            questions.add(question, 0,i-1);
        }

        TextField answer = new TextField();
        Label errorText = new Label();
        questions.add(answer, 1, questionColumn -1);
        answer.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                Integer correctAnswer = questionRow * questionColumn;
                questions.getChildren().remove(errorText);
                try {
                    if (Integer.parseInt(answer.getText()) == correctAnswer) {
                        //TODO change frequency of correct answer

                        // When a question is answered correctly, remove the answer box, replace it with a label with the correct answer,
                        // and place the answer box next to the question below
                        questions.getChildren().remove(answer);
                        Label answerText = new Label();
                        answerText.setText((questionRow * questionColumn + ""));
                        Label congratsText = new Label();
                        congratsText.setText(getCongratsMsg());
                        questions.add(congratsText, 2, questionColumn - 1);
                        questions.add(answerText, 1, questionColumn - 1);
                        answer.clear();

                        // continue with this row
                        if (questionColumn < maxTimesTable) {
                            questionColumn++;
                            questions.add(answer, 1, questionColumn - 1);
                        }

                        // move on to next row
                        else if (questionRow < maxTimesTable) {
                            questions.getChildren().clear();
                            questionRow++;
                            questionColumn = 1;
                            generateQuestions();
                        }

                        // ordered multiplication questions are complete
                        else {
                            //TODO generate random questions
                        }
                    }
                    else {
                        errorText.setText("Incorrect. Try again!");
                        questions.add(errorText, 2, questionColumn - 1);
                    }
                } catch (NumberFormatException e) {
                    errorText.setText("Enter a number please!");
                    questions.add(errorText, 2, questionColumn - 1);
                }
            }
        });
    }

    public void initializeArray() {
        for (int i = 0; i < 13; i++) {
            List<Integer> tmpList = new ArrayList<Integer>();
            for (int j = 0; j < 13; j++) {
                tmpList.add(0);
            }
            multiplicationArray.add(tmpList);
        }
    }

    public void printArray() {
        for (int i = 0; i < 13; i++) {
            System.out.println(multiplicationArray.get(i));
        }
    }

    public String getCongratsMsg() {
        int numOfMessages = congratsMessages.size();
        int msgId = (int) Math.floor((Math.random()*numOfMessages));
        return congratsMessages.get(Integer.toString(msgId)).asText();
    }
}