package gui.gamescreen;

import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

public class ProgressBar extends StackPane {
    private final javafx.scene.control.ProgressBar progressBar;
    private final Text text;
    public ProgressBar() {
        super();
        progressBar = new javafx.scene.control.ProgressBar();
        text = new Text();
        getChildren().addAll(progressBar, text);
    }

    public void setProgress(double value) {
        progressBar.setProgress(value);
        text.setText(Math.round(value*10000)/100.0+"%");
    }

    public javafx.scene.control.ProgressBar getProgressBar() { return progressBar; }
}
