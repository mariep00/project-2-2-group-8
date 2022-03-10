package gui.gamescreen;

import javafx.scene.control.ProgressBar;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

public class ProgressBarCustom extends StackPane {
    private final ProgressBar progressBar;
    private final Text text;
    public ProgressBarCustom() {
        super();
        progressBar = new ProgressBar();
        text = new Text();
        getChildren().addAll(progressBar, text);
    }

    public void setProgress(double value) {
        progressBar.setProgress(value);
        text.setText(Math.round(value*10000)/100.0+"%");
    }

    public ProgressBar getProgressBar() { return progressBar; }
}
