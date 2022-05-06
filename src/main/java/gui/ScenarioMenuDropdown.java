package gui;

import gamelogic.agent.tasks.TaskInterface;
import javafx.scene.control.ComboBox;
import javafx.scene.text.Text;

public class ScenarioMenuDropdown {
    public final Text fieldDesription;
    public final ComboBox dropdown;

    public ScenarioMenuDropdown(String fieldDescription, TaskInterface... tasks) {
        this.fieldDesription = new Text(fieldDescription);
        this.fieldDesription.setId("scenarioMenuText");

        this.dropdown = new ComboBox<TaskInterface>();
        //this.dropdown.getItems().addAll(tasks);
    }
}
