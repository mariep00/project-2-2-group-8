package gui;

import javafx.geometry.Pos;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

public class ScenarioMenuField extends HBox {
    private final Text fieldDesription;
    private final TextField textField;

    public ScenarioMenuField(String fieldDescription, String initialValue) {
        super(5);
        this.fieldDesription = new Text(fieldDescription);
        this.textField = new TextField(initialValue);
        this.textField.setMaxSize(65, 30);

        super.setAlignment(Pos.CENTER_RIGHT);
        super.getChildren().addAll(fieldDesription, textField);
    }

    public ScenarioMenuField(String fieldDescription, double initialValue) {
        this(fieldDescription, String.valueOf(initialValue));
    }

    public ScenarioMenuField(String fieldDescription, int initialValue) {
        this(fieldDescription, String.valueOf(initialValue));
    }
}
