package experiments;

import gamelogic.controller.Controller;
import gamelogic.MapBuilder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

/**
 * The three different files are meant to be used on a map where some parameters are changed each time.
 * For example ViewingRange, ViewingAngle etc.
 */

public class Experiments {

    public static final int ITERATIONS = 10;
    private static int gameCounter =0;
    private static double totalTime =0;
    private static PrintWriter out;


    public static Controller controller;
    private static final boolean DEBUG = true;
    private static final boolean saveResults = true;

    private static String path1 = "/Users/Johan/Documents/GitHub/project-2-2-group-8/src/main/resources/maps/testmap.txt";
    private static String path2 = "/Users/Johan/Documents/GitHub/project-2-2-group-8/src/main/resources/maps/testmap.txt";
    private static String path3 = "/Users/Johan/Documents/GitHub/project-2-2-group-8/src/main/resources/maps/testmap.txt";


    private static int currentBrain = 1;

    // 1 random
    // 2 frontier
    public Experiments(String path){
        controller = new Controller(new MapBuilder(new File(path)).getMap());
        controller.init();
        controller.engine();
    }

    public static void main(String[] args) {
        if (saveResults) {
            createFile();
            if (DEBUG) System.out.println("File created?");
        }


        //random 90D, map Giacomo

        out.println("\n ######################");
        out.println("random 90D");
        totalTime = 0;
        currentBrain = 1;
        for (int i=0; i < ITERATIONS; i++) {

            Experiments experiments = new Experiments(path1);

            gameCounter ++;

            out.println(gameCounter + ".    Time taken: "+ controller.time);
            totalTime = totalTime + controller.time;
        }
        out.println("Iterations: " + gameCounter);
        out.println(" <<<< TOTAL time: " + totalTime);
        out.println(" ---- AVG TIME: " + (totalTime/gameCounter));

        //random 135D
        currentBrain = 1;

        out.println("\n ######################" );
        out.println("random 135D");
        totalTime = 0;
        gameCounter = 0;
        for (int i=0; i < ITERATIONS; i++) {


            Experiments experiments = new Experiments(path2);

            gameCounter ++;

            out.println(gameCounter + ".    Time taken: "+ controller.time);
            totalTime = totalTime + controller.time;
        }
        out.println("Iterations: " + gameCounter);
        out.println(" <<<< TOTAL time: " + totalTime);
        out.println(" ---- AVG TIME: " + (totalTime/gameCounter));


        //random 180D, map Giacomo
        currentBrain = 1;

        out.println("\n ######################");
        out.println("random 180D");
        totalTime = 0;
        gameCounter = 0;
        for (int i=0; i < ITERATIONS; i++) {
            Experiments experiments = new Experiments(path3);

            gameCounter ++;

            out.println(gameCounter + ".    Time taken: "+ controller.time);
            totalTime = totalTime + controller.time;
        }
        out.println("Iterations: " + gameCounter);
        out.println(" <<<< TOTAL time: " + totalTime);
        out.println(" ---- AVG TIME: " + (totalTime/gameCounter));
        out.close();
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

