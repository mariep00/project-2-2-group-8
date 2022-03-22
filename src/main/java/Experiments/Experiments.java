package Experiments;

import controller.Controller;
import controller.MapBuilder;
import launchers.Launcher;

import java.io.File;

public class Experiments {

    public static final int ITERATIONS = 10;
    private static int gameCounter =0;

    public static Controller controller;
    private static final boolean DEBUG = true;

    public Experiments(String path){
        controller = new Controller(new MapBuilder(new File(path)).getMap());
        controller.init();
        controller.start();
    }

    public static void main(String[] args) {

        //random 90D
        for (int i=0; i < ITERATIONS; i++) {

            String path1 = "/Users/giaco/project-2-2-group-8/mapGiacomo,90D.txt";
            Experiments experiments = new Experiments(path1);

            gameCounter ++;

            System.out.println();

            if (DEBUG)System.out.println("#########GAME " + gameCounter +" ############");
            System.out.println();

        }
        //random 135D
        for (int i=0; i < ITERATIONS; i++) {

            String path2 = "/Users/giaco/project-2-2-group-8/mapGiacomo,135D.txt";
            Experiments experiments = new Experiments(path2);

            gameCounter ++;

            System.out.println();

            if (DEBUG)System.out.println("#########GAME " + gameCounter +" ############");
            System.out.println();

        }
        //random 180D
        for (int i=0; i < ITERATIONS; i++) {

            String path3 = "/Users/giaco/project-2-2-group-8/mapGiacomo,180D.txt";
            Experiments experiments = new Experiments(path3);

            gameCounter ++;

            System.out.println();

            if (DEBUG)System.out.println("#########GAME " + gameCounter +" ############");
            System.out.println();

        }

    }

    }

