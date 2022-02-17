package gui.map_creator;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;

class Tile extends ImageView {
    public Tile(Image image) {
        super(image);
        super.setFitWidth(45);
        super.setFitHeight(45);
        super.setPickOnBounds(true);

        setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                setImage(MapCreator.selectedListItem.image);
            }
            else if (e.getButton() == MouseButton.SECONDARY) {
                setImage(MapCreator.gridImage);
            }
        });
    }
}
