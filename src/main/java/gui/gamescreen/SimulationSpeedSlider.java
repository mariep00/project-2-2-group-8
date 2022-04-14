package gui.gamescreen;

import javafx.geometry.Pos;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class SimulationSpeedSlider extends VBox {
    public final Slider slider;
    public SimulationSpeedSlider() {
        super();
        slider = new Slider();
        slider.setMax(800);
        slider.setMin(5);
        slider.setMajorTickUnit(266.67);
        slider.setShowTickMarks(true);
        slider.setValue(400);
        slider.setPrefWidth(260);
        slider.setPrefHeight(35);
        Text title = new Text("Simulation speed");
        Text slow = new Text("Slow");
        Text fast = new Text("Fast");
        HBox hbox = new HBox(slow, slider, fast);
        slow.setTranslateY(4);
        fast.setTranslateY(4);
        super.getChildren().addAll(title, hbox);
        super.setAlignment(Pos.CENTER);
    }
}
