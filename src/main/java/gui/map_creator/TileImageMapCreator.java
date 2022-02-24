package gui.map_creator;

import gui.game_screen.TileImage;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;

public class TileImageMapCreator extends TileImage {
    public TileImageMapCreator() {
        super();
        setup();
    }
    public TileImageMapCreator(Image image) {
        super(image);
        setup();
    }

    private void setup() {
        setOnMousePressed(e -> {
            if (e.getButton() == MouseButton.SECONDARY) {
                resetTileImage();
            }
        });
    }

    public void changeImageToSelected() {
        setImage(MapCreator.selectedListItem.image);
    }
    public void changeImageToGrid() {
        setImage(MapCreator.gridImage);
    }
}
