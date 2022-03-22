package launchers;

import controller.Controller;
import controller.MapBuilder;

import java.io.File;



public class Launcher {

    /**
     * Launcher without GUI
     */
    public static void main(String[] args) {
        String path = ""; // Put the path to your map file here
        Controller controller = new Controller(new MapBuilder(new File(path)).getMap());
        controller.init();
        controller.start();
    }
}