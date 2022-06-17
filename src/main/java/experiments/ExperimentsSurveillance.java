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
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * The three different files are meant to be used on a map where some parameters are changed each time.
 * For example ViewingRange, ViewingAngle etc.
 */

public class ExperimentsSurveillance {
    public static final int NUMBER_OF_ITERATIONS = 25;

    private static final Random rand = new Random();

    private static final int[][] numberAgents = {{1,1},{3,1},{5,1},{1,3},{3,3},{5,3},{1,5},{3,5},{5,5}};

    // ONLY CHANGE THESE VALUES:
    // Specify Map name from the maps folder here
    private final static String[] MAP_FILE_NAMES = {"ExperimentSurveillance1.txt", "ExperimentSurveillance1FlippedSpawns.txt", "ExperimentSurveillance2.txt", "ExperimentSurveillance2FlippedSpawns.txt"};
    // Change these values to the ones from the table you want to run
    private static final int[] footStepMaxHearingDistance = {16};
    private static final int[] rotationMaxHearingDistance = {5};
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

                            URL url = ExperimentsSurveillance.class.getClassLoader().getResource("maps/" + MAP_FILE_NAMES[mapIndex]);
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

                            ThreadPoolExecutor threadPool = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(), 50, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
                            ScenarioMap finalScenarioMap = scenarioMap;
                            System.out.println("Iteration ");
                            for (int iteration = 0; iteration < NUMBER_OF_ITERATIONS; iteration++) {
                                int finalIteration = iteration;
                                threadPool.submit(() -> {
                                    System.out.print(finalIteration + ", ");
                                    Object[] result = runGame(finalScenarioMap);
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
                            double[] confidenceIntervalWins = calculateConfidenceInterval( winForTeam);
                            stringBuilder.append(confidenceIntervalWins[0]).append(",").append(confidenceIntervalWins[1]).append(",").append(confidenceIntervalWins[2]);
                            bufferedWriter.write(stringBuilder.toString());
                            bufferedWriter.newLine();

                            stringBuilder.setLength(0);
                            double[] confidenceIntervalTime = calculateConfidenceInterval(totalTimeForTeam);
                            stringBuilder.append(confidenceIntervalTime[0]).append(",").append(confidenceIntervalTime[1]).append(",").append(confidenceIntervalTime[2]);

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
        double[] bootstrapSamplesDiff = new double[500];
        for (int i = 0; i < bootstrapSamplesDiff.length; i++) {
            double bootstrap = rand.nextInt(numberOfGames);
            bootstrapSamplesDiff[i] = (bootstrap/numberOfGames)-mean;
        }
        Arrays.sort(bootstrapSamplesDiff);
        int upperboundIndex = (int) (bootstrapSamplesDiff.length*0.025);
        int lowerboundIndex = (int) (bootstrapSamplesDiff.length*0.975);

        System.out.println(Arrays.toString(bootstrapSamplesDiff));

        return new double[]{mean, bootstrapSamplesDiff[lowerboundIndex], bootstrapSamplesDiff[upperboundIndex]};

        /*double std = Math.sqrt(((Math.pow(1-mean, 2)*values[0])+(Math.pow(0-mean, 2)*values[1]))/(values[0]+values[1]-1));
        double division = std / Math.sqrt(values[0]+values[1]);
        double studentTValue = 1.984217;
        return new double[]{mean, mean-(studentTValue*division), mean+(studentTValue+division)};*/
    }

    private static Object[] runGame(ScenarioMap scenarioMap) {
        ControllerSurveillance controller = new ControllerSurveillance(scenarioMap, new EndingSurveillance(scenarioMap), new TaskContainer(new ExplorationTaskFrontier(), new FindSoundSource(), new ClosePursuingTask(), new FarPursuingTask(), new EvasionTaskBaseline(), new VisitLastSeenIntruderPositions(), new PathfindingTask(), new ExplorationInDirection(), new AvoidCollisionTask(), new CaptureTargetAreaTask()), rand.nextInt());
        controller.init();
        controller.engine();
        return new Object[]{controller.getWhoWon(), controller.time};
    }
}
