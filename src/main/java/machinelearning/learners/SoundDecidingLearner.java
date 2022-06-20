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
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

;

public class SoundDecidingLearner {
    public static void main(String[] args) throws Exception {
        System.out.println("Reading the data...");
        DataSet[] dataSets = getDataSets();
        DataSet trainingData = dataSets[0];
        DataSet testingData = dataSets[1];

        MultiLayerNetwork model;
        try {
            System.out.println("Loading the model...");
            model = ModelSerializer.restoreMultiLayerNetwork("src/main/java/machinelearning/data/experiments.results/sound_deciding_model");

            /*
            for (DataSet d : testingData) {
                double[][] temp = new double[1][6];
                for (int i = 0; i < 6; i++) {
                    temp[0][i] = d.getFeatures().getDouble(i);
                }
                NDArray arr = new NDArray(temp);
                System.out.println(arr);
                //System.out.println(model.output(arr));
            }*/

        }
        catch (IOException e) {
            System.out.println("* Model does not exist yet *");
            System.out.println("creating the model...");
            MultiLayerConfiguration configuration
                    = new NeuralNetConfiguration.Builder()
                    .updater(new Adam.Builder().epsilon(1e-6).learningRate(8e-4).build())
                    .l2(1e-4)
                    .activation(Activation.TANH)
                    .weightInit(WeightInit.XAVIER)
                    .list()
                    .layer(0, new DenseLayer.Builder().nIn(6).nOut(5).build())
                    .layer(1, new DenseLayer.Builder().nIn(5).nOut(4).build())
                    .layer(2, new OutputLayer.Builder(
                            LossFunctions.LossFunction.MEAN_SQUARED_LOGARITHMIC_ERROR)
                            .activation(Activation.SOFTMAX)
                            .nIn(4).nOut(2).build())
                    .backpropType(BackpropType.Standard)
                    .build();

            model = new MultiLayerNetwork(configuration);
            model.init();
        }

        System.out.println("Learning...");

        /*
        UIServer uiServer = UIServer.getInstance();

        //Configure where the network information (gradients, activations, score vs. time etc) is to be stored
        //Then add the StatsListener to collect this information from the network, as it trains
        StatsStorage statsStorage = new FileStatsStorage(new File("src/main/java/machinelearning/data/experiments.results/sound_deciding_results"));
        int listenerFrequency = 100;
        model.setListeners(new StatsListener(statsStorage, listenerFrequency));

        uiServer.attach(statsStorage); //Attach the StatsStorage instance to the UI: this allows the contents of the StatsStorage to be visualized
        */

        final int NUMBER_OF_EPOCHS = 2000;
        for (int i = 0; i < NUMBER_OF_EPOCHS; i++) {
            System.out.println("Current epoch: " + (i+1) + "...");

            trainingData.shuffle();
            List<DataSet> trainingDataBatched = trainingData.batchBy(20000);
            for (DataSet dataSet : trainingDataBatched) {
                model.fit(dataSet);
            }

            INDArray outputTrainingData = model.output(trainingData.getFeatures());
            Evaluation evalTrainingData = new Evaluation(2);
            evalTrainingData.eval(trainingData.getLabels(), outputTrainingData);

            INDArray outputTestData = model.output(testingData.getFeatures());
            Evaluation evalTestData = new Evaluation(2);
            evalTestData.eval(testingData.getLabels(), outputTestData);

            BufferedWriter bufferedWriter1 = new BufferedWriter(new FileWriter("src/main/java/machinelearning/data/experiments.results/sound_deciding_epoch_vs_accuracy.csv", true));
            bufferedWriter1.write(evalTrainingData.accuracy() + "," + evalTestData.accuracy() + "," + evalTrainingData.f1() + "," + evalTestData.f1());
            bufferedWriter1.newLine();
            bufferedWriter1.close();

            ModelSerializer.writeModel(model, "src/main/java/machinelearning/data/experiments.results/sound_deciding_model", true);

            if (i % 20 == 0) ModelSerializer.writeModel(model, "src/main/java/machinelearning/data/experiments.results/sound_deciding_model_"+(i+654), true);

            /*
            BufferedWriter bufferedWriter2 = new BufferedWriter(new FileWriter("src/main/java/machinelearning/data/experiments.results/sound_deciding_epoch_vs_confusion_matrix.csv", true));
            bufferedWriter2.write(evalTrainingData.falsePositiveRate() + "," + evalTrainingData.falseNegativeRate() + "," + evalTestData.falsePositiveRate() + "," + evalTestData.falseNegativeRate());
            bufferedWriter2.newLine();
            bufferedWriter2.close();*/
        }

        INDArray outputTrainingData = model.output(trainingData.getFeatures());
        Evaluation evalTrainingData = new Evaluation(2);
        evalTrainingData.eval(trainingData.getLabels(), outputTrainingData);
        System.out.println("--- Results on the training data ---");
        System.out.println(evalTrainingData.stats());

        INDArray outputTestData = model.output(testingData.getFeatures());
        Evaluation evalTestData = new Evaluation(2);
        evalTestData.eval(testingData.getLabels(), outputTestData);
        System.out.println("--- Results on the test data ---");
        System.out.println(evalTestData.stats());
    }

    private static DataSet[] getDataSets() throws IOException, InterruptedException {
        File directoryToLook = new File("src/main/java/machinelearning/data/trainingdata/");
        CSVRecordReader recordReader = new CSVRecordReader(0, ',');
        FileSplit fileSplit = new FileSplit(directoryToLook, new String[]{".csv"});
        recordReader.initialize(fileSplit);

        RecordReaderDataSetIterator iterator = new RecordReaderDataSetIterator.Builder(recordReader, 5000)
                .classification(6, 2)
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
        //System.out.println("BEFORE " + Arrays.toString(Arrays.copyOf(testDataMerged.asList().toArray(), 20)));
        getMinMax(trainingDataMerged);
        DataNormalization normalizer = new NormalizerMinMaxScaler();
        normalizer.fit(trainingDataMerged);
        normalizer.transform(trainingDataMerged);
        normalizer.transform(testDataMerged);
        //System.out.println("AFTER " + Arrays.toString(Arrays.copyOf(testDataMerged.asList().toArray(), 20)));



        return new DataSet[]{trainingDataMerged, testDataMerged};
    }

    private static void getMinMax(DataSet dataSet) {
        List<DataSet> data = dataSet.asList();
        double[] min = new double[]{Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE};
        double[] max = new double[]{Double.MIN_VALUE, Double.MIN_VALUE, Double.MIN_VALUE, Double.MIN_VALUE, Double.MIN_VALUE, Double.MIN_VALUE};
        for (DataSet d : data) {
            INDArray arr = d.getFeatures();
            for (int i = 0; i < arr.length(); i++) {
                if (arr.getDouble(i) < min[i]) min[i] = arr.getDouble(i);
                if (arr.getDouble(i) > max[i]) max[i] = arr.getDouble(i);
            }
        }
        System.out.println(Arrays.toString(min));
        System.out.println(Arrays.toString(max));
    }
}
