package gui.game_screen;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class TileImage extends ImageView {
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
    }

    public void resetTileImage() { setImage(null); }
}
