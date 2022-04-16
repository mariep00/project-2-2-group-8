package gui.util;

import gamelogic.maps.MapBuilder;
import gamelogic.maps.ScenarioMap;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.BitSet;

public class HelperGUI {
    public static ScenarioMap loadMapWithFileChooser(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        File selectedFile = fileChooser.showOpenDialog(stage);
        if (selectedFile != null) return new MapBuilder(selectedFile).getMap();
        else return null;
    }
    public static BitSet getBitSetSurroundingWalls(gamelogic.maps.Tile[][] tiles, int x, int y) {
        BitSet bitSet = new BitSet(8);
        byte count = 0;
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i != 0 || j != 0) {
                    if (y + i >= 0 && x + j >= 0 && y + i < tiles.length && x + j < tiles[y].length) {
                        if (tiles[y + i][x + j].getType() == gamelogic.maps.Tile.Type.WALL) {
                            bitSet.set(count);
                        }
                    }
                    count++;
                }
            }
        }
        return bitSet;
    }
}
