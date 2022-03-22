package launchers;

import controller.Controller;
import controller.MapBuilder;

import java.io.File;



public class Launcher {

    public static final int ITERATIONS = 10;
    public static void main(String[] args) {
        String base = Launcher.class.getResource("/maps/").toString();
        String mapName = "testmap.txt";
        //String path = base+mapName;
        String path = "/Users/joaquin/project-2-2-group-8/src/main/resources/maps/testmap.txt";
        Controller controller = new Controller(new MapBuilder(new File(path)).getMap());
        controller.init();
        controller.start();
    }
}