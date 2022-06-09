package machinelearning.learners;

import org.datavec.api.records.reader.impl.csv.CSVRecordReader;
import org.datavec.api.split.FileSplit;
import org.deeplearning4j.datasets.datavec.RecordReaderDataSetIterator;
import org.deeplearning4j.nn.conf.BackpropType;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.evaluation.classification.Evaluation;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.SplitTestAndTrain;
import org.nd4j.linalg.dataset.api.preprocessor.DataNormalization;
import org.nd4j.linalg.dataset.api.preprocessor.NormalizerMinMaxScaler;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SoundDecidingLearner {
    private static final Random rand = new Random();
    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("--- Reading the data... ---");
        File directoryToLook = new File("src/main/java/machinelearning/data/trainingdata/");
        CSVRecordReader recordReader = new CSVRecordReader(0, ',');
        FileSplit fileSplit = new FileSplit(directoryToLook, new String[]{".csv"});
        recordReader.initialize(fileSplit);

        RecordReaderDataSetIterator iterator = new RecordReaderDataSetIterator.Builder(recordReader, 25)
                .classification(5, 2)
                .build();

        List<DataSet> trainingData = new ArrayList<>();
        List<DataSet> testData = new ArrayList<>();

        System.out.println("Shuffling and splitting the data...");

        while (iterator.hasNext()) {
            DataSet batch = iterator.next();
            batch.shuffle();
            SplitTestAndTrain testAndTrain = batch.splitTestAndTrain(0.8);
            trainingData.add(testAndTrain.getTrain());
            testData.add(testAndTrain.getTest());
        }

        DataSet trainingDataMerged = DataSet.merge(trainingData);
        DataSet testDataMerged = DataSet.merge(testData);

        System.out.println("Normalizing the data...");

        DataNormalization normalizer = new NormalizerMinMaxScaler();
        normalizer.fit(trainingDataMerged);
        normalizer.transform(trainingDataMerged);
        normalizer.transform(testDataMerged);

        MultiLayerNetwork model;
        try {
            System.out.println("Loading the model...");
            model = ModelSerializer.restoreMultiLayerNetwork("src/main/java/machinelearning/data/results/sound_deciding_model");
        }
        catch (IOException e) {
            System.out.println("* Model does not exist yet *");
            System.out.println("creating the model...");
            MultiLayerConfiguration configuration
                    = new NeuralNetConfiguration.Builder()
                    .activation(Activation.TANH)
                    .weightInit(WeightInit.XAVIER)
                    .list()
                    .layer(0, new DenseLayer.Builder().nIn(5).nOut(3).build())
                    .layer(1, new DenseLayer.Builder().nIn(3).nOut(3).build())
                    .layer(2, new OutputLayer.Builder(
                            LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
                            .activation(Activation.SOFTMAX)
                            .nIn(3).nOut(2).build())
                    .backpropType(BackpropType.Standard)
                    .build();

            model = new MultiLayerNetwork(configuration);
            model.init();
        }

        System.out.println("Learning...");

        final int NUMBER_OF_EPOCHS = 1;
        for (int i = 0; i < NUMBER_OF_EPOCHS; i++) {
            System.out.println("Current epoch: " + (i+1) + "...");
            for (DataSet dataSet : trainingData) {
                model.fit(dataSet);
            }
            INDArray outputTrainingData = model.output(trainingDataMerged.getFeatures());
            Evaluation evalTrainingData = new Evaluation(2);
            evalTrainingData.eval(trainingDataMerged.getLabels(), outputTrainingData);

            INDArray outputTestData = model.output(testDataMerged.getFeatures());
            Evaluation evalTestData = new Evaluation(2);
            evalTestData.eval(testDataMerged.getLabels(), outputTestData);

            BufferedWriter bufferedWriter1 = new BufferedWriter(new FileWriter("src/main/java/machinelearning/data/results/sound_deciding_epoch_vs_accuracy.csv", true));
            bufferedWriter1.write(evalTrainingData.accuracy() + "," + evalTestData.accuracy());
            bufferedWriter1.newLine();
            bufferedWriter1.close();

            BufferedWriter bufferedWriter2 = new BufferedWriter(new FileWriter("src/main/java/machinelearning/data/results/sound_deciding_epoch_vs_confusion_matrix.csv", true));
            bufferedWriter2.write(evalTrainingData.falsePositiveRate() + "," + evalTrainingData.falseNegativeRate() + "," + evalTestData.falsePositiveRate() + "," + evalTestData.falseNegativeRate());
            bufferedWriter2.newLine();
            bufferedWriter2.close();
        }

        System.out.println("Saving the model...");
        ModelSerializer.writeModel(model, "src/main/java/machinelearning/data/results/sound_deciding_model", true);

        INDArray outputTrainingData = model.output(trainingDataMerged.getFeatures());
        Evaluation evalTrainingData = new Evaluation(2);
        evalTrainingData.eval(trainingDataMerged.getLabels(), outputTrainingData);
        System.out.println("--- Results on the training data ---");
        System.out.println(evalTrainingData.stats());

        INDArray outputTestData = model.output(testDataMerged.getFeatures());
        Evaluation evalTestData = new Evaluation(2);
        evalTestData.eval(testDataMerged.getLabels(), outputTestData);
        System.out.println("--- Results on the test data ---");
        System.out.println(evalTestData.stats());
    }
}
