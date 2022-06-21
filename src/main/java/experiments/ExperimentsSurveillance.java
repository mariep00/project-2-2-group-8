package experiments;

import gamelogic.agent.tasks.TaskContainer;
import gamelogic.agent.tasks.general.AvoidCollisionTask;
import gamelogic.agent.tasks.general.ExplorationInDirection;
import gamelogic.agent.tasks.general.ExplorationTaskFrontier;
import gamelogic.agent.tasks.general.PathfindingTask;
import gamelogic.agent.tasks.guard.ClosePursuingTask;
import gamelogic.agent.tasks.guard.FarPursuingTask;
import gamelogic.agent.tasks.guard.FindSoundSource;
import gamelogic.agent.tasks.guard.VisitLastSeenIntruderPositions;
import gamelogic.agent.tasks.intruder.CaptureTargetAreaTask;
import gamelogic.agent.tasks.intruder.EvasionTaskBaseline;
import gamelogic.controller.endingconditions.EndingSurveillance;
import gamelogic.controller.gamemodecontrollers.ControllerSurveillance;
import gamelogic.maps.MapBuilder;
import gamelogic.maps.ScenarioMap;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * The three different files are meant to be used on a map where some parameters are changed each time.
 * For example ViewingRange, ViewingAngle etc.
 */

public class ExperimentsSurveillance {
    public static final int NUMBER_OF_ITERATIONS_PER_MAP = 1000;

    private static final Random rand = new Random();

    private static final int[][] numberAgents = {{1,1},{3,1},{5,1},{1,3},{3,3},{5,3},{1,5},{3,5},{5,5}};

    // ONLY CHANGE THESE VALUES:
    // Specify Map name from the maps folder here
    private final static String[] MAP_FILE_NAMES = {"ExperimentSurveillance1.txt", "ExperimentSurveillance1FlippedSpawns.txt", "ExperimentSurveillance2.txt", "ExperimentSurveillance2FlippedSpawns.txt"};
    // Change these values to the ones from the table you want to run
    // Use array of size 1 to only use that value
    // Because of the loops it will perform experiments on all combinations of these array entries
    // Example:
    // FD = {16, 24} (i.e. FD = 16, FD = 24)
    // RD = {5, 10} (i.e. RD = 5, RD = 10)
    // YD = {30} (i.e. YD = 30)
    // Will result in performing the experiments on:
    // FD = 16, RD = 5, YD = 30
    // FD = 24, RD = 5, YD = 30
    // FD = 16, RD = 10, YD = 30
    // FD = 24, RD = 10, YD = 30
    private static final int[] footStepMaxHearingDistance = {16, 8, 24};
    private static final int[] rotationMaxHearingDistance = {10};
    private static final int[] yellMaxHearingDistance = {30};

    public static void main(String[] args) throws InterruptedException {
        for (int footStepDistance : footStepMaxHearingDistance) {
            for (int rotationDistance : rotationMaxHearingDistance) {
                for (int yellDistance : yellMaxHearingDistance) {
                    for (int[] numberOfAgent : numberAgents) {
                        int numGuards = numberOfAgent[0];
                        int numIntruders = numberOfAgent[1];

                        System.out.println();
                        System.out.println("**** Current configuration " + numGuards + ", " + numIntruders + ", " + footStepDistance + ", " + rotationDistance + ", " + yellDistance + " ****");

                        double[] totalTimeForTeam = new double[2];
                        double[] winForTeam = new double[2];

                        for (int mapIndex = 0; mapIndex < MAP_FILE_NAMES.length; mapIndex++) {
                            System.out.println("---- Current map " + (mapIndex+1) + " of " + MAP_FILE_NAMES.length + " ----");

                            ThreadPoolExecutor threadPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
                            for (int iteration = 0; iteration < NUMBER_OF_ITERATIONS_PER_MAP; iteration++) {
                                int finalMapIndex = mapIndex;
                                threadPool.submit(() -> {
                                    URL url = ExperimentsSurveillance.class.getClassLoader().getResource("maps/" + MAP_FILE_NAMES[finalMapIndex]);
                                    ScenarioMap scenarioMap = null;
                                    try {
                                        scenarioMap = new MapBuilder(Paths.get(url.toURI()).toFile()).getMap();
                                    } catch (URISyntaxException e) {
                                        e.printStackTrace();
                                    }

                                    scenarioMap.setNumGuards(numGuards);
                                    scenarioMap.setNumIntruders(numIntruders);
                                    scenarioMap.setRotatingMaxHearingDistance(rotationDistance);
                                    scenarioMap.setYellMaxHearingDistance(yellDistance);
                                    scenarioMap.setFootstepMaxHearingDistance(footStepDistance);

                                    Object[] result = runGame(scenarioMap);
                                    winForTeam[(int) result[0]]++;
                                    totalTimeForTeam[(int) result[0]] += (double) result[1];
                                });
                            }
                            threadPool.shutdown();
                            threadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
                            System.out.println();
                        }
                        try {
                            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("src/main/java/experiments/results/surveillance_"+numGuards+"_"+numIntruders+"_"+footStepDistance+"_"+rotationDistance+"_"+yellDistance+".csv", true));
                            StringBuilder stringBuilder = new StringBuilder();

                            stringBuilder.append(winForTeam[0]).append(",").append(totalTimeForTeam[0] / winForTeam[0]).append(",").append(winForTeam[1]).append(",").append(totalTimeForTeam[1] / winForTeam[1]);
                            bufferedWriter.write(stringBuilder.toString());
                            bufferedWriter.newLine();

                            stringBuilder.setLength(0);
                            double[] confidenceIntervalWins = calculateConfidenceInterval(winForTeam);
                            stringBuilder.append(confidenceIntervalWins[0]).append(",").append(confidenceIntervalWins[1]).append(",").append(confidenceIntervalWins[2]);
                            bufferedWriter.write(stringBuilder.toString());

                            bufferedWriter.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    private static double[] calculateConfidenceInterval(double[] values) {
        int numberOfGames = (int) (values[0]+values[1]);
        double mean = values[0]/numberOfGames;

        Random rand = new Random();
        double[] bootstrapSamplesDiff = new double[1000];
        for (int i = 0; i < bootstrapSamplesDiff.length; i++) {
            double bootstrap = 0;
            for (int j = 0; j < numberOfGames; j++) {
                int sample = rand.nextInt(numberOfGames+1);
                if (sample < values[0]) bootstrap += 1;
            }

            bootstrapSamplesDiff[i] = (bootstrap/numberOfGames)-mean;
        }
        Arrays.sort(bootstrapSamplesDiff);
        int lowerboundIndex = (int) (bootstrapSamplesDiff.length*0.025);
        int upperboundIndex = (int) (bootstrapSamplesDiff.length*0.975);

        return new double[]{mean, bootstrapSamplesDiff[lowerboundIndex], bootstrapSamplesDiff[upperboundIndex]};
    }

    private static Object[] runGame(ScenarioMap scenarioMap) {
        ControllerSurveillance controller = new ControllerSurveillance(scenarioMap, new EndingSurveillance(scenarioMap), new TaskContainer(new ExplorationTaskFrontier(), new FindSoundSource(), new ClosePursuingTask(), new FarPursuingTask(), new EvasionTaskBaseline(), new VisitLastSeenIntruderPositions(), new PathfindingTask(), new ExplorationInDirection(), new AvoidCollisionTask(), new CaptureTargetAreaTask()), rand.nextInt());
        controller.init();
        controller.engine();
        return new Object[]{controller.getWhoWon(), controller.time};
    }
}
