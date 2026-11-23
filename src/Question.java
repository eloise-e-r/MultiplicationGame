import javafx.scene.control.Label;

import java.time.LocalDate;

public class Question {
    public Label label = new Label();
    public Integer answer;
    public Integer row;
    public Integer column;
    public Integer multiplier = 1;
    public LocalDate dueDate = LocalDate.now();

    public Question(Integer row, Integer column) {
        this.row = row;
        this.column = column;
        this.answer = row*column;
        this.label.setText(row + " x " + column);
    }
}
