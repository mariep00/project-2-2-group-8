package machinelearning.evasion.model;

import java.io.IOException;

import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.util.ModelSerializer;

public class EvasionModel {

    private static EvasionModel evasionModel;
    private MultiLayerNetwork network;
    
    private EvasionModel () {
        try {
            network = ModelSerializer.restoreMultiLayerNetwork("src/main/java/machinelearning/evasion/data/results/evasion_model");
        } catch (IOException e) {
        }
    }

    public static EvasionModel getInstance() {
        if (evasionModel==null) {
            evasionModel = new EvasionModel();
        }
        return evasionModel;
    }

    
}
