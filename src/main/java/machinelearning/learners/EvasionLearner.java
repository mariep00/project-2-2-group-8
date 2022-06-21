package machinelearning.learners;

import gamelogic.agent.tasks.TaskContainer;
import gamelogic.agent.tasks.general.*;
import gamelogic.agent.tasks.guard.ClosePursuingTask;
import gamelogic.agent.tasks.guard.FarPursuingTask;
import gamelogic.agent.tasks.guard.FindSoundSource;
import gamelogic.agent.tasks.guard.VisitLastSeenIntruderPositions;
import gamelogic.agent.tasks.intruder.CaptureTargetAreaTask;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class EvasionLearner {
    private final static String[] MAP_POOL = {"ExperimentSurveillance1.txt", "ExperimentSurveillance2.txt", "ExperimentSurveillance1FlippedSpawns.txt", "ExperimentSurveillance2FlippedSpawns.txt", "ChangedAdvancedSurveillance.txt", "AdvancedTestSurveillance.txt", "SimpleTestSurveillance.txt"};
    private final static TaskContainer TASK_CONTAINER = new TaskContainer(new ExplorationTaskFrontier(), new FindSoundSource(), new ClosePursuingTask(), new FarPursuingTask(), new EvasionTaskBaseline(),
            new VisitLastSeenIntruderPositions(), new PathfindingTask(), new ExplorationInDirection(), new AvoidCollisionTask(), new CaptureTargetAreaTask()); // Change this to change the tasks that can be performed by agents
    private final static int NUMBER_OF_ITERATIONS_PER_MAP = 1; // One iteration is around 24 games, depending on the map

    public static void main(String[] args) throws IOException {
        ArrayList<String> map_pool_shuffled = new ArrayList<>(List.of(MAP_POOL));
        Collections.shuffle(map_pool_shuffled);

        for (int i = 0; i < MAP_POOL.length; i++) {
            System.out.println("*** Map " + i + " ***");
            URL url = Launcher.class.getClassLoader().getResource("maps/" + map_pool_shuffled.get(i));
            ScenarioMap scenarioMap = null;
            try {
                scenarioMap = new MapBuilder(Paths.get(url.toURI()).toFile()).getMap();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            for (int j = 0; j < NUMBER_OF_ITERATIONS_PER_MAP; j++) {
                System.out.println("--- Iteration " + j + " ---");
                ControllerSurveillanceRLEvasion controller = new ControllerSurveillanceRLEvasion(scenarioMap, new EndingSurveillance(scenarioMap), TASK_CONTAINER, new Random().nextInt());
                controller.init();
                Environment env = new Environment(controller);

                QLearningDiscreteDense<GameState> dql;
                try {
                    dql = new QLearningDiscreteDense<>(env, DQN.load("src/main/java/machinelearning/evasion/experiments.results/evasion_model"), NetworkUtil.buildConfig());
                } catch (IOException e) {
                    System.out.println("* Model does not exist yet *");
                    System.out.println("Creating model..");
                    dql = new QLearningDiscreteDense<>(env, NetworkUtil.buildDQNFactory(), NetworkUtil.buildConfig());
                }

                dql.train();
                env.close();

                dql.getNeuralNet().save("src/main/java/machinelearning/evasion/experiments.results/evasion_model");
            }
        }
    }
}
