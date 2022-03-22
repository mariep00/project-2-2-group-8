package Experiments;

import controller.Controller;
import controller.MapBuilder;

import java.io.File;

public class Experiments {

    public static final int ITERATIONS = 1000;
    private static int gameCounter =0;

    private String path = "/Users/Yannick/Documents/DKE_UM/Year_2/Project_2-2/code/src/main/resources/maps/testmap.txt";
    public static Controller controller;
    private static final boolean DEBUG = true;

    public Experiments(){
        controller = new Controller(new MapBuilder(new File(path)).getMap());
        controller.init();
        controller.start();
    }

    public static void main(String[] args) {

        for (int i=0; i < ITERATIONS; i++) {

            Experiments experiments = new Experiments();

            gameCounter++;

            System.out.println();

            if (DEBUG)System.out.println("#########GAME " + gameCounter +" ############");
            System.out.println();

        }
    }

    }

