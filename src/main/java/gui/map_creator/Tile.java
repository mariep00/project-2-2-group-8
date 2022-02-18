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

        /*addEventHandler(MouseEvent.MOUSE_DRAGGED, e -> {
            Bounds boundsGridPaneInScene = getParent().localToScene(getParent().getBoundsInLocal());
            double posX = e.getSceneX() - boundsGridPaneInScene.getMinX();
            double posY = e.getSceneY() - boundsGridPaneInScene.getMinY();

            int indexX = (int) Math.floor((posX / tileSize));
            int indexY = (int) Math.floor((posY / tileSize));

            if (indexX < MapCreator.tiles.length && indexY < MapCreator.tiles[0].length && indexX >= 0 && indexY >= 0) {
                if (e.isPrimaryButtonDown()) {
                    MapCreator.tiles[indexX][indexY].changeImageToSelected();
                }
                else if (e.isSecondaryButtonDown()) {
                    MapCreator.tiles[indexX][indexY].changeImageToGrid();
                }
            }
        });*/
    }

    public void changeImageToSelected() {
        setImage(MapCreator.selectedListItem.image);
    }
    public void changeImageToGrid() {
        setImage(MapCreator.gridImage);
    }
}
