package Experiments;

import controller.Controller;
import controller.MapBuilder;
import launchers.Launcher;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class Experiments {

    public static final int ITERATIONS = 10;
    private static int gameCounter =0;
    private static double totalTime =0;
    private static PrintWriter out;


    public static Controller controller;
    private static final boolean DEBUG = true;
    private static final boolean saveResults = true;

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
        if (saveResults) createFile();

        currentBrain = 1;
        //random 90D, map Giacomo
        out.println("Iterarions: " + gameCounter);
        //random 90D
        out.println("------------------");
        out.println("random 90D");
        totalTime = 0;
        for (int i=0; i < ITERATIONS; i++) {


            Experiments experiments = new Experiments(path1);

            gameCounter ++;

            out.println(gameCounter + ".    Time taken: "+ controller.time);
        }
        out.println("Iterations: " + gameCounter);
        out.println(" ---- AVG TIME: " + (totalTime/gameCounter));

        //random 135D
        out.println("------------------");
        out.println("random 135D");
        totalTime = 0;
        currentBrain = 1;
        for (int i=0; i < ITERATIONS; i++) {


            Experiments experiments = new Experiments(path2);

            gameCounter ++;

            out.println(gameCounter + ".    Time taken: "+ controller.time);
        }
        out.println("Iterations: " + gameCounter);
        out.println(" ---- AVG TIME: " + (totalTime/gameCounter));


        currentBrain = 1;
        //random 180D, map Giacomo
        out.println("------------------");
        out.println("random 180D");
        totalTime = 0;
        for (int i=0; i < ITERATIONS; i++) {


            Experiments experiments = new Experiments(path3);

            gameCounter ++;

            out.println(gameCounter + ".    Time taken: "+ controller.time);
            totalTime = totalTime + controller.time;
        }
        out.println("Iterations: " + gameCounter);
        out.println(" ---- AVG TIME: " + (totalTime/gameCounter));
        
    }

    public static PrintWriter createFile(){
        String outputFileName = "experiments.txt";
        out = null;
        try {
            out = new PrintWriter(outputFileName);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
    

    }

