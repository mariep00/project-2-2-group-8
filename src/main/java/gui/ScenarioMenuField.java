package gui;

import javafx.scene.control.TextField;
import javafx.scene.text.Text;

public class ScenarioMenuField {
    public final Text fieldDesription;
    public final TextField textField;

    public ScenarioMenuField(String fieldDescription, String initialValue) {
        this.fieldDesription = new Text(fieldDescription);
        this.fieldDesription.setId("scenarioMenuText");

        this.textField = new TextField(initialValue);
        this.textField.setMaxSize(100, 65);
        this.textField.setId("scenarioMenuTextField");
    }

    public ScenarioMenuField(String fieldDescription, double initialValue) {
        this(fieldDescription, String.valueOf(initialValue));
    }

    public ScenarioMenuField(String fieldDescription, int initialValue) {
        this(fieldDescription, String.valueOf(initialValue));
    }

    public String getText() { return textField.getText(); }
}
