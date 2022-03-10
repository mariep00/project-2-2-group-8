package controller.maps;

import controller.Vector2D;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class EndingExplorationMap {
    private int width;
    private int height;
    private int[][] explorationMap;
    private int totalTilesToExplore;
    private int currentTilesExplored = 0;

    public EndingExplorationMap(ScenarioMap map){
        this.height = map.getHeight();
        this.width = map.getWidth();
        this.explorationMap = new int[this.height][this.width];
        this.totalTilesToExplore = this.height*this.width;
        initialization(map);
    }

    public void initialization(ScenarioMap map){
        for(int i=0; i < this.explorationMap.length; i++){
            for(int j=0; j < this.explorationMap[i].length; j++){
                if(map.getTile(new Vector2D(j, i)).getType() == Tile.Type.WALL){
                    explorationMap[i][j] = 2;
                    totalTilesToExplore--;
                }
                else if(map.getTile(new Vector2D(j, i)).getType() == Tile.Type.TELEPORT){
                    explorationMap[i][j] = 2;
                    totalTilesToExplore--;
                }
            }
        }
    }

    public boolean isExplored(){
        /*
        for(int i=0; i < this.explorationMap.length; i++){
            for(int j=0; j < this.explorationMap[i].length; j++){
                if(explorationMap[i][j] == 0){
                    return false;
                }
            }
        }
        return true;

         */
        System.out.println(currentTilesExplored + " / " + totalTilesToExplore);
        return totalTilesToExplore == currentTilesExplored;
    }

    public void updateExplorationMap(Vector2D coordinate){
        /*if (this.explorationMap[coordinate.y][coordinate.x] !=2){
            this.explorationMap[coordinate.y][coordinate.x] = 1;
        }*/
        //writeMatrix("nicematrix2.txt", explorationMap);
        if (explorationMap[coordinate.y][coordinate.x] != 2) {
            if (explorationMap[coordinate.y][coordinate.x] == 0) {
                explorationMap[coordinate.y][coordinate.x] = 1;
                currentTilesExplored++;
            }
        }
    }
    public void updateExplorationMap(ArrayList<Vector2D> coordinates) {
        for (Vector2D coordinate : coordinates) {
            updateExplorationMap(coordinate);
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
