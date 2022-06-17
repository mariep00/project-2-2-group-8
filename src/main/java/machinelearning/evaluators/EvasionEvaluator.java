package machinelearning.evaluators;

import gamelogic.agent.tasks.TaskContainer;
import gamelogic.agent.tasks.general.*;
import gamelogic.agent.tasks.guard.ClosePursuingTask;
import gamelogic.agent.tasks.guard.FarPursuingTask;
import gamelogic.agent.tasks.guard.FindSoundSource;
import gamelogic.agent.tasks.guard.VisitLastSeenIntruderPositions;
import gamelogic.agent.tasks.intruder.EvasionTaskBaseline;
import gamelogic.agent.tasks.intruder.EvasionTaskRL;
import gamelogic.controller.endingconditions.EndingSurveillance;
import gamelogic.controller.gamemodecontrollers.ControllerSurveillance;
import gamelogic.maps.MapBuilder;
import gamelogic.maps.ScenarioMap;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;

public class EvasionEvaluator {

    private static final int MAPS = 8;
    public static final int ITERATIONS = 20;
    private static int[] winIntruder = new int[2];
    private static double[] totalTimeIntruder = new double[2];

    private static PrintWriter out;

    private static final boolean DEBUG = false;
    private static boolean machineLearning = true;

    // Specify Map name from the maps folder here
    private final static String[] FILE_NAME = {"ExperimentSurveillance1.txt" , "ExperimentSurveillance1FlippedSpawns.txt" , "ExperimentSurveillance2.txt" , "ExperimentSurveillance2FlippedSpawns.txt" , "SimpleTestSurveillance.txt" , "SimpleTestSurveillanceFlippedSpawns.txt" , "AdvancedTestSurveillance.txt" , "AdvancedTestSurveillanceFlippedSpawns.txt"};


    public static void main(String[] args) throws InterruptedException {
        createFile();
        
        for (int j = 0; j < MAPS; j++) {
            System.out.println("Current map: " + j);
            URL url = EvasionEvaluator.class.getClassLoader().getResource("maps/"+FILE_NAME[j]);
            totalTimeIntruder = new double[2];
            winIntruder = new int[2];
            System.out.println("Iteration: ");
            for (int i = 0; i <= ITERATIONS; i++) {
                System.out.print(i +" ");
                ScenarioMap scenarioMap = null;
                try {
                    scenarioMap = new MapBuilder(Paths.get(url.toURI()).toFile()).getMap();
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
                ControllerSurveillance controller;
                if (machineLearning) {
                    controller = new ControllerSurveillance(scenarioMap, new EndingSurveillance(scenarioMap), new TaskContainer(new ExplorationTaskFrontier(), new FindSoundSource(), new ClosePursuingTask(), new FarPursuingTask(), new EvasionTaskRL(), new VisitLastSeenIntruderPositions(), new PathfindingTask(), new ExplorationInDirection(), new AvoidCollisionTask(), new CaptureTargetAreaTask()), i);
                } else {
                    controller = new ControllerSurveillance(scenarioMap, new EndingSurveillance(scenarioMap), new TaskContainer(new ExplorationTaskFrontier(), new FindSoundSource(), new ClosePursuingTask(), new FarPursuingTask(), new EvasionTaskBaseline(), new VisitLastSeenIntruderPositions(), new PathfindingTask(), new ExplorationInDirection(), new AvoidCollisionTask(), new CaptureTargetAreaTask()), i);
                }
                controller.init();
                controller.engine();

                int team = controller.getWhoWon();
                if (team == 1) {
                    if (machineLearning) {
                        winIntruder[0]++;
                        totalTimeIntruder[0] = totalTimeIntruder[team] + controller.time;
                    } else {
                        winIntruder[1]++;
                        totalTimeIntruder[1] = totalTimeIntruder[team] + controller.time;
                    }
                }
                if (i == ITERATIONS && machineLearning) {
                    i = 0;
                    machineLearning = false;
                }
            }
            System.out.println();

            out.println(" --- MAP: " + FILE_NAME[j] );
            out.println(" ------ ML INTRUDER WINS COUNT: " + winIntruder[0] + " ---- AVG TIME FOR WIN: " + (totalTimeIntruder[0]/winIntruder[0]));
            out.println(" ------ INTRUDER WINS COUNT: " + winIntruder[1] + " ---- AVG TIME FOR WIN: " + (totalTimeIntruder[1]/winIntruder[1]));
        }

        out.close();


    }

    public static PrintWriter createFile(){
        String outputFileName = "EvasionEvaluation.txt";
        out = null;
        try {
            out = new PrintWriter(outputFileName);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    
}
