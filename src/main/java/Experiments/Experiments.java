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

    private static String path1 = "mapGiacomo,90D.txt";
    private static String path2 = "mapGiacomo,135D.txt";
    private static String path3 = "mapGiacomo,180D.txt";


    private static int currentBrain = 1;

    // 1 random
    // 2 frontier
    public Experiments(String path){
        controller = new Controller(new MapBuilder(new File(path)).getMap(),currentBrain);
        controller.init();
        controller.start();
    }

    public static void main(String[] args) {

        currentBrain = 1;
        //random 90D, map Giacomo
        for (int i=0; i < ITERATIONS; i++) {


            Experiments experiments = new Experiments(path1);

            gameCounter ++;

            System.out.println();

            if (DEBUG)System.out.println("#########GAME " + gameCounter +" ############");
            System.out.println();

        }
        currentBrain = 1;
        //random 135D, map Giacomo
        for (int i=0; i < ITERATIONS; i++) {


            Experiments experiments = new Experiments(path2);

            gameCounter ++;

            System.out.println();

            if (DEBUG)System.out.println("#########GAME " + gameCounter +" ############");
            System.out.println();

        }

        currentBrain = 1;
        //random 180D, map Giacomo
        for (int i=0; i < ITERATIONS; i++) {


            Experiments experiments = new Experiments(path3);

            gameCounter ++;

            System.out.println();

            if (DEBUG)System.out.println("#########GAME " + gameCounter +" ############");
            System.out.println();

        }

        currentBrain = 2;
        //frontier 90D, map Giacomo
        for (int i=0; i < ITERATIONS; i++) {


            Experiments experiments = new Experiments(path1);

            gameCounter ++;

            System.out.println();

            if (DEBUG)System.out.println("#########GAME " + gameCounter +" ############");
            System.out.println();

        }







    }

    }

