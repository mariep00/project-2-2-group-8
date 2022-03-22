package controller.maps;

import controller.Vector2D;

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

    public void initialization(ScenarioMap map) {
        for(int i=0; i < this.explorationMap.length; i++){
            for(int j=0; j < this.explorationMap[i].length; j++){
                if(map.getTile(new Vector2D(j, i)).getType() == Tile.Type.WALL){
                    explorationMap[i][j] = 2;
                    totalTilesToExplore--;
                }
            }
        }
    }

    public boolean isExplored() {
        System.out.println(currentTilesExplored + " / " + totalTilesToExplore);
        return totalTilesToExplore == currentTilesExplored;
    }

    public boolean updateExplorationMap(Vector2D coordinate) {
        if (coordinate.y<explorationMap.length && coordinate.x<explorationMap[0].length && coordinate.y>=0 && coordinate.x>=0) {
            if (explorationMap[coordinate.y][coordinate.x] != 2) {
                if (explorationMap[coordinate.y][coordinate.x] == 0) {
                    explorationMap[coordinate.y][coordinate.x] = 1;
                    currentTilesExplored++;
                }
            }
        }
        return totalTilesToExplore==currentTilesExplored;
    }
    public void updateExplorationMap(ArrayList<Vector2D> coordinates) {
        for (Vector2D coordinate : coordinates) {
            if (coordinate.y<explorationMap.length && coordinate.x<explorationMap[0].length && coordinate.y>=0 && coordinate.x>=0) {
                updateExplorationMap(coordinate);
            }
        }
    }

    public int getTotalTilesToExplore() {
        return totalTilesToExplore;
    }

    public int getCurrentTilesExplored() {
        return currentTilesExplored;
    }
}
