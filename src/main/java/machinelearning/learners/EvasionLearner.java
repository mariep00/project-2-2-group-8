package machinelearning.learners;

import gamelogic.agent.tasks.TaskContainer;
import gamelogic.agent.tasks.general.AvoidCollisionTask;
import gamelogic.agent.tasks.general.ExplorationInDirection;
import gamelogic.agent.tasks.general.ExplorationTaskFrontier;
import gamelogic.agent.tasks.general.PathfindingTask;
import gamelogic.agent.tasks.guard.ClosePursuingTask;
import gamelogic.agent.tasks.guard.FindSoundSource;
import gamelogic.agent.tasks.guard.VisitLastSeenIntruderPositions;
import gamelogic.agent.tasks.intruder.EvasionTaskBaseline;
import gamelogic.controller.endingconditions.EndingSurveillance;
import gamelogic.controller.gamemodecontrollers.ControllerSurveillanceRLEvasion;
import gamelogic.maps.MapBuilder;
import gamelogic.maps.ScenarioMap;
import launchers.Launcher;
import machinelearning.evasion.Environment;
import machinelearning.evasion.GameState;
import machinelearning.evasion.NetworkUtil;
import org.deeplearning4j.rl4j.learning.sync.qlearning.discrete.QLearningDiscreteDense;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Random;

public class EvasionLearner {
    private final static String FILE_NAME = "ExperimentSurveillance2FlippedSpawns.txt"; // Change this string to the file name of the map you want to run. Make sure the map is located in resources/maps.
    private final static TaskContainer TASK_CONTAINER = new TaskContainer(new ExplorationTaskFrontier(), new FindSoundSource(), new ClosePursuingTask(), new ClosePursuingTask(), new EvasionTaskBaseline(),
            new VisitLastSeenIntruderPositions(), new PathfindingTask(), new ExplorationInDirection(), new AvoidCollisionTask()); // Change this to change the tasks that can be performed by agents


    public static void main(String[] args) throws IOException {
        URL url = Launcher.class.getClassLoader().getResource("maps/"+FILE_NAME);
        ScenarioMap scenarioMap = null;
        try {
            scenarioMap = new MapBuilder(Paths.get(url.toURI()).toFile()).getMap();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        ControllerSurveillanceRLEvasion controller = new ControllerSurveillanceRLEvasion(scenarioMap, new EndingSurveillance(scenarioMap), TASK_CONTAINER, new Random().nextInt());
        controller.init();
        Environment env = new Environment(controller);
        QLearningDiscreteDense<GameState> dql = new QLearningDiscreteDense<GameState>(env, NetworkUtil.buildDQNFactory(), NetworkUtil.buildConfig());
        dql.train();
        env.close();

        dql.getNeuralNet().save("src/main/java/machinelearning/evasion/results/evasion_model");
    }
}
