package experiments;

import gamelogic.agent.tasks.TaskContainer;
import gamelogic.agent.tasks.general.ExplorationTaskFrontier;
import gamelogic.controller.endingconditions.EndingExploration;
import gamelogic.controller.gamemodecontrollers.ControllerExploration;
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

public class ExperimentsExploration {
    public static final int NUMBER_OF_ITERATIONS_PER_MAP = 4000;
    private static final Random rand = new Random();

    private static final int[] numberAgents = {1,3,4,6};

    private final static String[] MAP_FILE_NAMES = {"Map1.txt","Map2.txt"};

    private final static int[] pheromoneRanges = {0};
    private final static double[] pheromoneReductions = {100000};

    public static void main(String[] args) throws InterruptedException {
        int count = 0;
        for (int pheromoneRange : pheromoneRanges) {
            for (double pheromoneReduction : pheromoneReductions) {
                for (int numberOfExplorationAgents : numberAgents) {
                    for (int mapIndex = 0; mapIndex < MAP_FILE_NAMES.length; mapIndex++) {
                        System.out.println();
                        System.out.println("Percentage done " + ((double) count/(numberAgents.length*pheromoneRanges.length*pheromoneReductions.length*MAP_FILE_NAMES.length)*100) + "%");
                        double[] timeToExplore = new double[NUMBER_OF_ITERATIONS_PER_MAP];

                        ThreadPoolExecutor threadPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
                        for (int iteration = 0; iteration < NUMBER_OF_ITERATIONS_PER_MAP; iteration++) {
                            int finalMapIndex = mapIndex;
                            int finalIteration = iteration;

                            //threadPool.submit(() -> {
                                URL url = ExperimentsSurveillance.class.getClassLoader().getResource("maps/" + MAP_FILE_NAMES[finalMapIndex]);
                                ScenarioMap scenarioMap = null;
                                try {
                                    scenarioMap = new MapBuilder(Paths.get(url.toURI()).toFile()).getMap();
                                } catch (URISyntaxException e) {
                                    e.printStackTrace();
                                }

                                scenarioMap.setNumGuards(numberOfExplorationAgents);
                                scenarioMap.setPheromoneMaxSmellingDistance(pheromoneRange);
                                scenarioMap.setPheromoneReduction(pheromoneReduction);
                                timeToExplore[finalIteration] = runGame(scenarioMap);

                            System.out.println(iteration);
                           // });
                        }
                        threadPool.shutdown();
                        threadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);

                        try {
                            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("src/main/java/experiments/results/exploration_"+mapIndex+"_"+numberOfExplorationAgents+"_"+pheromoneRange+"_"+pheromoneReduction+".csv", true));
                            StringBuilder stringBuilder = new StringBuilder();

                            for (int i = 0; i < timeToExplore.length; i++) {
                                stringBuilder.append(timeToExplore[i]);
                                if (i < timeToExplore.length-1) stringBuilder.append(",");
                            }
                            bufferedWriter.write(stringBuilder.toString());
                            bufferedWriter.newLine();

                            stringBuilder.setLength(0);
                            double[] confidenceIntervalTime = calculateConfidenceInterval(timeToExplore);
                            stringBuilder.append(confidenceIntervalTime[0]).append(",").append(confidenceIntervalTime[1]).append(",").append(confidenceIntervalTime[2]);
                            bufferedWriter.write(stringBuilder.toString());

                            bufferedWriter.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        count++;
                    }
                }
            }
        }
    }

    private static double[] calculateConfidenceInterval(double[] timeToExplore) {
        double mean = 0;
        for (double d : timeToExplore) {
            mean += d;
        }
        mean /= NUMBER_OF_ITERATIONS_PER_MAP;

        Random rand = new Random();
        double[] bootstrapSamplesDiff = new double[1000];
        for (int i = 0; i < bootstrapSamplesDiff.length; i++) {
            double bootstrap = 0;
            for (int j = 0; j < NUMBER_OF_ITERATIONS_PER_MAP; j++) {
                int sampleIndex = rand.nextInt(timeToExplore.length);
                bootstrap += timeToExplore[sampleIndex];
            }

            bootstrapSamplesDiff[i] = (bootstrap/NUMBER_OF_ITERATIONS_PER_MAP)-mean;
        }

        Arrays.sort(bootstrapSamplesDiff);
        int lowerboundIndex = (int) (bootstrapSamplesDiff.length*0.025);
        int upperboundIndex = (int) (bootstrapSamplesDiff.length*0.975);

        return new double[]{mean, bootstrapSamplesDiff[lowerboundIndex], bootstrapSamplesDiff[upperboundIndex]};
    }

    private static double runGame(ScenarioMap scenarioMap) {
        ControllerExploration controller = new ControllerExploration(scenarioMap, new EndingExploration(scenarioMap), new TaskContainer(new ExplorationTaskFrontier()), rand.nextInt());
        controller.init();
        controller.engine();
        return controller.time;
    }
}
