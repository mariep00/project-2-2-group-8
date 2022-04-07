package launchers;

import gamelogic.controller.Controller;
import gamelogic.MapBuilder;
import gamelogic.controller.gamemodecontrollers.ControllerExploration;

import java.io.File;



public class Launcher {

    /**
     * Launcher without GUI
     */
    public static void main(String[] args) {
        String path = ""; // Put the path to your map file here
        Controller controller = new ControllerExploration(new MapBuilder(new File(path)).getMap());
        controller.engine();
    }
}