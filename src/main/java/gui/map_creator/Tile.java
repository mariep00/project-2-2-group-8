package gui.map_creator;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;

class Tile extends ImageView {
    public static final int tileSize = 45;

    public Tile(Image image) {
        super(image);
        super.setFitWidth(tileSize);
        super.setFitHeight(tileSize);
        super.setPickOnBounds(true);

        setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                changeImageToSelected();
            }
            else if (e.getButton() == MouseButton.SECONDARY) {
                changeImageToGrid();
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
