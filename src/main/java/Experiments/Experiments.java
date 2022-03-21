package Experiments;

import controller.Controller;
import controller.MapBuilder;
import launchers.Launcher;

import java.io.File;

public class Experiments {


    public static void main(String[] args) {
        String base = Launcher.class.getResource("/maps/").toString();
        String mapName = "testmap.txt";
        //String path = base+mapName;
        String path = "/Users/giaco/project-2-2-group-8/tetsmap.txt";

        Controller controller = new Controller(new MapBuilder(new File(path)).getMap());
        controller.init();
        controller.start();

        for (int i = 0; i < 100; i++){

        }
    }

    }

