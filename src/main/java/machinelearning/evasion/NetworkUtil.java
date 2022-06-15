package machinelearning.evasion;

import org.deeplearning4j.rl4j.learning.configuration.QLearningConfiguration;
import org.deeplearning4j.rl4j.network.configuration.DQNDenseNetworkConfiguration;
import org.deeplearning4j.rl4j.network.dqn.DQNFactoryStdDense;
import org.nd4j.linalg.learning.config.RmsProp;

public class NetworkUtil {
    public static final int NUMBER_OF_INPUTS = 56; // 16+30+1+8+1
    public static QLearningConfiguration buildConfig() {
        return QLearningConfiguration.builder()
                .maxEpochStep(200)
                .maxStep(750)
                .expRepMaxSize(150000)
                .batchSize(128)
                .targetDqnUpdateFreq(500)
                .updateStart(10)
                .rewardFactor(0.01)
                .gamma(0.99)
                .errorClamp(1)
                .minEpsilon(0.1)
                .epsilonNbStep(1000)
                .doubleDQN(true)
                .build();
    }

    public static DQNFactoryStdDense buildDQNFactory() {
        final DQNDenseNetworkConfiguration build = DQNDenseNetworkConfiguration.builder()
                .l2(1e-3)
                .updater(new RmsProp(2.5e-4))
                .numHiddenNodes(300)
                .numLayers(2)
                .build();

        return new DQNFactoryStdDense(build);
    }

    public static int taskIndexToMovementTask(int taskIndex) {
        if (taskIndex == 0) return 0;
        else if (taskIndex == 1) return 1;
        else if (taskIndex == 2) return 3;
        else if (taskIndex == 3) return 4;
        else return -1;
    }

}
