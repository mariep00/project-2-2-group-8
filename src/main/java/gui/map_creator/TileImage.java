package gui.map_creator;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;

class TileImage extends ImageView {
    public TileImage() {
        super();
        setup();
    }
    public TileImage(Image image) {
        super(image);
        setup();
    }

    private void setup() {
        super.setFitWidth(Tile.tileSize);
        super.setFitHeight(Tile.tileSize);

        setOnMousePressed(e -> {
            if (e.getButton() == MouseButton.SECONDARY) {
                resetTileImage();
            }
        });
    }

    public void resetTileImage() { setImage(null); }

    public void changeImageToSelected() {
        setImage(MapCreator.selectedListItem.image);
    }
    public void changeImageToGrid() {
        setImage(MapCreator.gridImage);
    }
}
