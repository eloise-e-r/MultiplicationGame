import javafx.scene.control.Label;

import java.time.LocalDate;

public class Question {
    public String labelText;
    public Integer answer;
    public Integer row;
    public Integer column;
    public Integer daysToAdd = 1;
    public LocalDate dueDate;
    public LocalDate dateLastFailed;

    public Question() {}

    public Question(Integer row, Integer column) {
        this.row = row;
        this.column = column;
        this.answer = row*column;
        this.labelText = (row + " x " + column);
    }
}
