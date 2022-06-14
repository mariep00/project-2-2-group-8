package machinelearning.evasion;

import org.deeplearning4j.rl4j.space.Encodable;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

public class GameState implements Encodable {
    private final double[] inputs;
    private final boolean skip;

    public GameState(final double[] inputs, boolean skip) {
        this.inputs = inputs;
        this.skip = skip;
    }

    @Override
    public double[] toArray() {
        return inputs;
    }

    @Override
    public boolean isSkipped() {
        return skip;
    }

    @Override
    public INDArray getData() {
        return Nd4j.create(inputs);
    }

    @Override
    public Encodable dup() {
        return null;
    }

    public INDArray getMatrix() {
        return Nd4j.create(inputs);
    }
}
