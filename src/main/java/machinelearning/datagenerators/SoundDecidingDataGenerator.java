package machinelearning.datagenerators;

import gamelogic.datacarriers.Sound;
import gamelogic.datacarriers.VisionMemory;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class SoundDecidingDataGenerator {
    public static void generateData(Sound sound, VisionMemory[] lastSeen, int agentIndex) {
        for (int i = 0; i < lastSeen.length; i++) {
            VisionMemory visionMemory = lastSeen[i];
            if (visionMemory != null) {
                arrayToCSV(new double[]{sound.angle(), sound.loudness(), visionMemory.position().angle(), visionMemory.orientation(), visionMemory.secondsAgo(), visionMemory.position().magnitude(), agentIndex == i ? 1 : 0});
            }
        }
    }

    public static void arrayToCSV(double[] array) {
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("src/main/java/machinelearning/data/trainingdata/sound_deciding_experiment2_flipped.csv", true));
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < array.length; i++) {
                stringBuilder.append(array[i]);
                if (i < array.length-1) stringBuilder.append(",");
            }
            bufferedWriter.write(stringBuilder.toString());
            bufferedWriter.newLine();
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
