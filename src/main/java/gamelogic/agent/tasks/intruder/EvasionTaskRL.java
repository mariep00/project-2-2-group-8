package gamelogic.agent.tasks.intruder;

import gamelogic.agent.tasks.TaskContainer.TaskType;
import gamelogic.agent.tasks.TaskInterface;
import gamelogic.datacarriers.Sound;
import gamelogic.datacarriers.VisionMemory;
import gamelogic.maps.graph.ExplorationGraph;
import machinelearning.evasion.GameStateUtil;
import machinelearning.evasion.NetworkUtil;
import org.apache.commons.lang.ArrayUtils;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.cpu.nativecpu.NDArray;
import org.nd4j.shade.guava.collect.ObjectArrays;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class EvasionTaskRL implements TaskInterface{
    private final MultiLayerNetwork model;
    private VisionMemory visionToEvadeFrom;
    private Sound soundToEvadeFrom;


    public EvasionTaskRL() {
        MultiLayerNetwork modelTemp;
        try {
            modelTemp = MultiLayerNetwork.load(new File("src/main/java/machinelearning/evasion/results/evasion_model"), true);
        }
        catch (IOException e) {
            e.printStackTrace();
            modelTemp = null;
        }
        this.model = modelTemp;
    }

    @Override
    public int performTask(ExplorationGraph graph, double orientation, double pheromoneMarkerDirection, List<Sound> sounds, VisionMemory[] guardsSeen, VisionMemory[] intrudersSeen) {
        double[] visionInput = GameStateUtil.getVisionInput(ObjectArrays.concat(guardsSeen, intrudersSeen, VisionMemory.class), visionToEvadeFrom);
        double[] wallsInput = GameStateUtil.getWallsInput(graph);
        double[] pheromoneInput = new double[]{pheromoneMarkerDirection};
        double[] soundInput = GameStateUtil.getSoundInput(sounds, soundToEvadeFrom);
        double[] orientationInput = new double[]{orientation};

        double[] mergedNormalizedData = ArrayUtils.addAll(ArrayUtils.addAll(ArrayUtils.addAll(ArrayUtils.addAll(GameStateUtil.normalizeVisionInput(visionInput), GameStateUtil.normalizeWallsInput(wallsInput)),
                GameStateUtil.normalizePheromoneMarkerInput(pheromoneInput)), GameStateUtil.normalizeSoundInput(soundInput)), GameStateUtil.normalizeOrientationInput(orientationInput));
        double[][] normalizedData2D = new double[1][mergedNormalizedData.length];
        System.arraycopy(mergedNormalizedData, 0, normalizedData2D[0], 0, mergedNormalizedData.length);
        INDArray output = model.output(new NDArray(normalizedData2D));
        int largestIndex = 0;
        for (int i = 1; i < output.length(); i++) {
            if (output.getDouble(i) > output.getDouble(largestIndex)) {
                largestIndex = i;
            }
        }
        return NetworkUtil.taskIndexToMovementTask(largestIndex);
    }

    @Override
    public void setTarget(double target, VisionMemory visionMemory) {
        this.visionToEvadeFrom = visionMemory;
    }

    @Override
    public void setTarget(double target, Sound sound) {
        this.soundToEvadeFrom = sound;
    }

    @Override 
    public boolean isFinished() {
        return true;
    }

    @Override
    public TaskType getType() {
        return TaskType.INTRUDER_EVASION;
    }

    @Override
    public TaskInterface newInstance() {
        return new EvasionTaskRL();
    }
    
}
