package experiments;

import gamelogic.agent.tasks.TaskContainer;
import gamelogic.agent.tasks.general.*;
import gamelogic.agent.tasks.guard.ClosePursuingTask;
import gamelogic.agent.tasks.guard.FindSoundSource;
import gamelogic.agent.tasks.guard.VisitLastSeenIntruderPositions;
import gamelogic.agent.tasks.intruder.EvasionTaskBaseline;
import gamelogic.controller.endingconditions.EndingSurveillance;
import gamelogic.controller.gamemodecontrollers.ControllerSurveillance;
import gamelogic.maps.MapBuilder;
import gamelogic.maps.ScenarioMap;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * The three different files are meant to be used on a map where some parameters are changed each time.
 * For example ViewingRange, ViewingAngle etc.
 */

public class ExperimentsSurveillance {

    private static final int ROWS = 9;
    public static final int ITERATIONS = 2500;
    public static final int RUNS = 5;
    private static int[] winForTeam = new int[2];
    private static double[] totalTimeForTeam = new double[2];

    private static PrintWriter out;

    private static final boolean DEBUG = false;
    private static final boolean saveResults = true;
    private static final Random rand = new Random();

    private static final int[][] numberAgents = {{1,1},{3,1},{4,1},{1,3},{3,3},{4,3},{1,4},{3,4},{4,4}};

    // ONLY CHANGE THESE VALUES:
    // Specify Map name from the maps folder here
    private final static String FILE_NAME = "ExperimentSurveillance1.txt";
    // Change these values to the ones from the table you want to run
    private static final int footStepMaxHearingDistance = 16;
    private static final int rotationMaxHearingDistance = 12;
    private static final int yellMaxHearingDistance = 50;



    public ExperimentsSurveillance(ControllerSurveillance controller){
        controller.init();
        controller.engine();
    }

    public static void main(String[] args) throws InterruptedException {
        if (saveResults) {
            createFile();
            if (DEBUG) System.out.println("File created?");
        }
        URL url = ExperimentsSurveillance.class.getClassLoader().getResource("maps/"+FILE_NAME);

        for (int j=0; j<ROWS; j++) {
            //int finalJ = j;
            int numGuards = numberAgents[j][0];
            int numIntruders = numberAgents[j][1];
            out.println("ROW:" + (j+1) + " - NumberGuards: " + numGuards + ", NumberIntruders: " + numIntruders);
            for (int a=1; a<=RUNS; a++) {
                System.out.println(j + ", " + a);
                totalTimeForTeam = new double[2];
                winForTeam = new int[2];
                ThreadPoolExecutor threadPool = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(), 50, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
                for (int i = 1; i <= ITERATIONS; i++) {
                    //int finalI = i;
                    //int finalA = a;
                    threadPool.submit(() -> {
                        ScenarioMap scenarioMap = null;
                        try {
                            scenarioMap = new MapBuilder(Paths.get(url.toURI()).toFile()).getMap();
                        } catch (URISyntaxException e) {
                            e.printStackTrace();
                        }
                        scenarioMap.setNumGuards(numGuards);
                        scenarioMap.setNumIntruders(numIntruders);
                        scenarioMap.setRotatingMaxHearingDistance(rotationMaxHearingDistance);
                        scenarioMap.setYellMaxHearingDistance(yellMaxHearingDistance);
                        scenarioMap.setFootstepMaxHearingDistance(footStepMaxHearingDistance);
                        ControllerSurveillance controller = new ControllerSurveillance(scenarioMap, new EndingSurveillance(scenarioMap), new TaskContainer(new ExplorationTaskFrontier(), new FindSoundSource(), new ClosePursuingTask(), new EvasionTaskBaseline(), new VisitLastSeenIntruderPositions(), new PathfindingTask(), new ExplorationInDirection(), new AvoidCollisionTask(), new CaptureTargetAreaTask()), rand.nextInt());
                        
                        ExperimentsSurveillance experiments = new ExperimentsSurveillance(controller);
                        
                        //System.out.println(finalJ + ", " +  finalA + ", " + finalI);
                        int team = controller.getWhoWon();
                        winForTeam[team]++;
                        totalTimeForTeam[team] = totalTimeForTeam[team] + controller.time;
                    });
                }
                threadPool.shutdown();
                threadPool.awaitTermination(Integer.MAX_VALUE, TimeUnit.SECONDS);
                out.println(" --- RUN:" + a);
                out.println(" ------ GUARD WINS COUNT: " + winForTeam[0] + " ---- AVG TIME FOR WIN: " + (totalTimeForTeam[0]/winForTeam[0]));
                out.println(" ------ INTRUDER WINS COUNT: " + winForTeam[1] + " ---- AVG TIME FOR WIN: " + (totalTimeForTeam[1]/winForTeam[1]));
            }
            out.println();
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
