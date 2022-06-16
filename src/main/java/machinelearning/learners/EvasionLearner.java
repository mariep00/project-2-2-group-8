package machinelearning.learners;

import gamelogic.agent.tasks.TaskContainer;
import gamelogic.agent.tasks.general.*;
import gamelogic.agent.tasks.guard.ClosePursuingTask;
import gamelogic.agent.tasks.guard.FarPursuingTask;
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
import org.deeplearning4j.rl4j.network.dqn.DQN;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Random;

public class EvasionLearner {
    private final static String FILE_NAME = "ExperimentSurveillance1FlippedSpawns.txt"; // Change this string to the file name of the map you want to run. Make sure the map is located in resources/maps.
    private final static TaskContainer TASK_CONTAINER = new TaskContainer(new ExplorationTaskFrontier(), new FindSoundSource(), new ClosePursuingTask(), new FarPursuingTask(), new EvasionTaskBaseline(),
            new VisitLastSeenIntruderPositions(), new PathfindingTask(), new ExplorationInDirection(), new AvoidCollisionTask(), new CaptureTargetAreaTask()); // Change this to change the tasks that can be performed by agents
    private final static int NUMBER_OF_ITERATIONS = 6;

    public static void main(String[] args) throws IOException {
        for (int i = 0; i < NUMBER_OF_ITERATIONS; i++) {
            System.out.println("------ Iteration " + i + " ------");
            System.out.println("Setting up the learning environment..");
            URL url = Launcher.class.getClassLoader().getResource("maps/" + FILE_NAME);
            ScenarioMap scenarioMap = null;
            try {
                scenarioMap = new MapBuilder(Paths.get(url.toURI()).toFile()).getMap();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }

            ControllerSurveillanceRLEvasion controller = new ControllerSurveillanceRLEvasion(scenarioMap, new EndingSurveillance(scenarioMap), TASK_CONTAINER, new Random().nextInt());
            controller.init();
            Environment env = new Environment(controller);

            QLearningDiscreteDense<GameState> dql;
            try {
                System.out.println("Loading model..");
                dql = new QLearningDiscreteDense<>(env, DQN.load("src/main/java/machinelearning/evasion/results/evasion_model"), NetworkUtil.buildConfig());
            } catch (IOException e) {
                System.out.println("* Model does not exist yet *");
                System.out.println("Creating model..");
                dql = new QLearningDiscreteDense<>(env, NetworkUtil.buildDQNFactory(), NetworkUtil.buildConfig());
            }

            System.out.println("Training model..");
            dql.train();
            env.close();

            System.out.println("Saving model..");
            dql.getNeuralNet().save("src/main/java/machinelearning/evasion/results/evasion_model");
        }
    }
}
