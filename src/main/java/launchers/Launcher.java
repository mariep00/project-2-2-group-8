package launchers;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import Controller.FOV;
import Controller.Vector2D;

public class Launcher {
    public static void main(String[] args) {
        double visionAngle = 90.0;
        double visionRange = 10.0;
        double direction = 90.0;
        Vector2D n = new Vector2D(0, 0);
        FOV f = new FOV(visionRange);
        f.calculate(visionAngle, visionRange, null, direction);
        int[][] map = f.getMap();
        writeMatrix("vision.txt", map);
    }

    public static void printMatrix (int[][] matrix) {
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                System.out.print(matrix[i][j] + " ");
            }
            System.out.println();
        }
    }

    public static void writeMatrix(String filename, int[][] matrix) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(filename));
    
            for (int i = 0; i < matrix.length; i++) {
                for (int j = 0; j < matrix[i].length; j++) {
                        bw.write(matrix[i][j] + " ");
                }
                bw.newLine();
            }
            bw.flush();
        } catch (IOException e) {}
    }
}
