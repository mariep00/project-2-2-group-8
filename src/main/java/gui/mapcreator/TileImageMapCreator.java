package gui.mapcreator;

import gui.gamescreen.ImageContainer;
import gui.gamescreen.TileImage;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;

public class TileImageMapCreator extends TileImage {
    private TileType tileType;
    public TileImageMapCreator() {
        super();
        tileType = null;
        setup();
    }
    public TileImageMapCreator(Image image, TileType tileType) {
        super(image);
        this.tileType = tileType;
        setup();
    }

    private void setup() {
        setOnMousePressed(e -> {
            if (e.getButton() == MouseButton.SECONDARY) {
                resetImageToFloor();
            }
        });
    }

    public void changeImageToSelected() {
        this.tileType = MapCreator.selectedListItem.tileType;
        setImage(MapCreator.selectedListItem.image);
    }
    public void resetImageToFloor() {
        this.tileType = TileType.FLOOR;
        setImage(ImageContainer.getInstance().getFloor());
    }

    @Override
    public void resetTileImage() {
        super.resetTileImage();
        this.tileType = null;
    }

    public TileType getTileType() { return tileType; }
}
