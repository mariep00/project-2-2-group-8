package experiments;

import gamelogic.agent.tasks.TaskContainer;
import gamelogic.agent.tasks.general.ExplorationTaskFrontier;
import gamelogic.controller.Controller;
import gamelogic.maps.MapBuilder;
import gamelogic.controller.endingconditions.EndingExploration;
import gamelogic.controller.gamemodecontrollers.ControllerExploration;
import gamelogic.maps.ScenarioMap;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

/**
 * The three different files are meant to be used on a map where some parameters are changed each time.
 * For example ViewingRange, ViewingAngle etc.
 */

public class Experiments {

    public static final int ITERATIONS = 2000;
    public static final int RUNS = 4;
    private static int gameCounter =0;
    private static double totalTime =0;
    private static PrintWriter out;


    public static Controller controller;
    private static final boolean DEBUG = true;
    private static final boolean saveResults = true;

    private static String path1 = "C://Users//giaco//Downloads//map1.txt";
    //private static String path1 = "testmap.txt";
    //private static String path2 = "/Users/Johan/Documents/GitHub/project-2-2-group-8/src/main/resources/maps/testmap.txt";
    //private static String path3 = "/Users/Johan/Documents/GitHub/project-2-2-group-8/src/main/resources/maps/testmap.txt";


    private static int currentBrain = 1;

    // 1 random
    // 2 frontier
    public Experiments(String path){
        ScenarioMap scenarioMap = new MapBuilder(new File(path)).getMap();
        controller = new ControllerExploration(scenarioMap, new EndingExploration(scenarioMap), new TaskContainer(new ExplorationTaskFrontier()));
        controller.init();
        controller.engine();
    }

    public static void main(String[] args) {
        if (saveResults) {
            createFile();
            if (DEBUG) System.out.println("File created?");
        }


        currentBrain = 1;
        for (int a=1; a<=RUNS; a++) {
            totalTime = 0;
            gameCounter = 0;
            for (int i = 0; i < ITERATIONS; i++) {

                Experiments experiments = new Experiments(path1);
                gameCounter++;
                System.out.println(gameCounter + ".    Time taken: " + controller.time);
                totalTime = totalTime + controller.time;
            }
            out.println("RUN:" + a);
            out.println(" ---- AVG TIME: " + (totalTime / gameCounter));
        }
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

