package experiments;

import gamelogic.agent.tasks.TaskContainer;
import gamelogic.agent.tasks.general.AvoidCollisionTask;
import gamelogic.agent.tasks.general.ExplorationInDirection;
import gamelogic.agent.tasks.general.ExplorationTaskFrontier;
import gamelogic.agent.tasks.general.PathfindingTask;
import gamelogic.agent.tasks.guard.ClosePursuingTask;
import gamelogic.agent.tasks.guard.FindSoundSource;
import gamelogic.agent.tasks.guard.VisitLastSeenIntruderPositions;
import gamelogic.agent.tasks.intruder.EvasionTaskBaseline;
import gamelogic.controller.Controller;
import gamelogic.maps.MapBuilder;
import gamelogic.controller.endingconditions.EndingExploration;
import gamelogic.controller.endingconditions.EndingSurveillance;
import gamelogic.controller.gamemodecontrollers.ControllerExploration;
import gamelogic.controller.gamemodecontrollers.ControllerSurveillance;
import gamelogic.maps.ScenarioMap;

import java.io.File;
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

public class Experiments {

    public static final int ITERATIONS = 2000;
    public static final int RUNS = 4;
    private static int[] winForTeam = new int[2];
    private static double[] totalTimeForTeam = new double[2];

    private static PrintWriter out;

    private static final boolean DEBUG = true;
    private static final boolean saveResults = true;
    private static final Random rand = new Random();

    // Specify Map name from the maps folder here
    private final static String FILE_NAME = "AdvancedTestSurveillance.txt";

    public Experiments(ControllerSurveillance controller){
        controller.init();
        controller.engine();
    }

    public static void main(String[] args) throws InterruptedException {
        if (saveResults) {
            createFile();
            if (DEBUG) System.out.println("File created?");
        }
        URL url = Experiments.class.getClassLoader().getResource("maps/"+FILE_NAME);

        for (int a=1; a<=RUNS; a++) {
            totalTimeForTeam = new double[2];
            winForTeam = new int[2];
            ThreadPoolExecutor threadPool = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(), 50, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
            for (int i = 1; i <= ITERATIONS; i++) {
                int finalI = i;
                int finalA = a;
                threadPool.submit(() -> {
                    ScenarioMap scenarioMap = null;
                    try {
                        scenarioMap = new MapBuilder(Paths.get(url.toURI()).toFile()).getMap();
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                    ControllerSurveillance controller = new ControllerSurveillance(scenarioMap, new EndingSurveillance(scenarioMap), new TaskContainer(new ExplorationTaskFrontier(), new FindSoundSource(), new ClosePursuingTask(), new EvasionTaskBaseline(), new VisitLastSeenIntruderPositions(), new PathfindingTask(), new ExplorationInDirection(), new AvoidCollisionTask()), rand.nextInt());
                    
                    Experiments experiments = new Experiments(controller);
                    
                    System.out.println(finalA + ", " + finalI);
                    //System.out.println(i + ".    Time taken: " + controller.time);
                    int team = controller.getWhoWon();
                    winForTeam[team]++;
                    totalTimeForTeam[team] = totalTimeForTeam[team] + controller.time;
                });
            }
            threadPool.shutdown();
            threadPool.awaitTermination(Integer.MAX_VALUE, TimeUnit.SECONDS);
            out.println("RUN:" + a);
            out.println(" ---- GUARD WINS COUNT: " + winForTeam[0] + " ---- AVG TIME FOR WIN: " + (totalTimeForTeam[0]/winForTeam[0]));
            out.println(" ---- INTRUDER WINS COUNT: " + winForTeam[1] + " ---- AVG TIME FOR WIN: " + (totalTimeForTeam[1]/winForTeam[1]));
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
