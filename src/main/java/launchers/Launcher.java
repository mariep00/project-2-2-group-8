package launchers;

import controller.Controller;
import controller.MapBuilder;

import java.io.File;

public class Launcher {
    public static void main(String[] args) {
        String base = Launcher.class.getResource("/maps/").toString();
        String mapName = "testmap.txt";
        //String path = base+mapName;
        String path = "/Users/Yannick/Documents/DKE_UM/Year_2/Project_2-2/code/src/main/resources/maps/testmap.txt";
        new Controller(new MapBuilder(new File(path)).getMap()).start();
    }
}