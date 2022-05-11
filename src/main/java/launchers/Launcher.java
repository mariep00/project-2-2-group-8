package launchers;

import gamelogic.agent.tasks.TaskContainer;
import gamelogic.agent.tasks.general.ExplorationTaskFrontier;
import gamelogic.controller.Controller;
import gamelogic.controller.endingconditions.EndingExploration;
import gamelogic.controller.gamemodecontrollers.ControllerExploration;
import gamelogic.maps.MapBuilder;
import gamelogic.maps.ScenarioMap;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public class Launcher {
    private final static boolean MULTITHREAD_LAUNCHER = false; // Change this to enable or disable multithreading in the launcher. I.e. running multiple games in parallel.
    private final static int NUMBER_OF_GAMES = 1000; // Change this to change the number of games to run
    private final static String FILE_NAME = "testmap.txt"; // Change this string to the file name of the map you want to run. Make sure the map is located in resources/maps.
    private final static TaskContainer TASK_CONTAINER = new TaskContainer(new ExplorationTaskFrontier()); // Change this to change the tasks that can be performed by agents

    /**
     * Launcher without GUI
     */
    public static void main(String[] args) throws InterruptedException {
        URL url = Launcher.class.getClassLoader().getResource("maps/"+FILE_NAME);
        ThreadPoolExecutor threadPool = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors()/2, 50, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
        if (url != null) {
            long startTime = System.nanoTime();
            for (int i = 0; i < NUMBER_OF_GAMES; i++) {
                int finalI = i;
                if (MULTITHREAD_LAUNCHER) threadPool.submit(() -> runGame(finalI, url));
                else runGame(finalI, url);
            }
            threadPool.shutdown();
            threadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
            long stopTime = System.nanoTime();
            System.out.println((stopTime - startTime)/1000000);
        }
        else System.out.println("******** Map not found ********");
    }

    private static void runGame(int i, URL url) {
        System.out.println("Game " + i + " / " + NUMBER_OF_GAMES);
        ScenarioMap scenarioMap = null;
        try {
            scenarioMap = new MapBuilder(Paths.get(url.toURI()).toFile()).getMap();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        Controller controller = new ControllerExploration(scenarioMap, new EndingExploration(scenarioMap), TASK_CONTAINER);
        controller.init();
        controller.engine();
    }

}