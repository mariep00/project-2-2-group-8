package Controller;

import java.util.ArrayList;

public class EndingExplorationMap {
    private int width;
    private int height;
    private int[][] explorationMap;

    public EndingExplorationMap(ScenarioMap map){
        this.height = map.getHeight();
        this.width = map.getWidth();
        this.explorationMap = new int[this.height][this.width];
        initialization(map);
    }

    public void initialization(ScenarioMap map){
        for(int i=0; i < this.explorationMap.length; i++){
            for(int j=0; j < this.explorationMap[i].length; j++){
                if(map.getTile(i, j).getType() == Tile.Type.WALL){
                    explorationMap[i][j] = 2;
                }
                else if(map.getTile(i, j).getType() == Tile.Type.TELEPORT){
                    explorationMap[i][j] = 2;
                }
            }
        }
    }

    public boolean isExplored(){
        for(int i=0; i < this.explorationMap.length; i++){
            for(int j=0; j < this.explorationMap[i].length; j++){
                if(explorationMap[i][j] == 0){
                    return false;
                }
            }
        }
        return true;
    }

    public void updateExplorationMap(Vector2D coordinates){
        if(this.explorationMap[coordinates.x][coordinates.y] !=2){
            this.explorationMap[coordinates.x][coordinates.y] = 1;
        }
    }


}
