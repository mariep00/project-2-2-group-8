package launchers;

import controller.Controller;
import controller.MapBuilder;

import java.io.File;

public class Launcher {
    public static void main(String[] args) {
        String base = Launcher.class.getResource("/maps/").toString();
        String mapName = "testmap.txt";
        //String path = base+mapName;
        String path = "/Users/giaco/project-2-2-group-8/tetsmap.txt";
        Controller controller = new Controller(new MapBuilder(new File(path)).getMap());
        controller.init();
        controller.start();
    }
}