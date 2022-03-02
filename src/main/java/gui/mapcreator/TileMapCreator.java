package gui.mapcreator;

import gui.gamescreen.Tile;
import gui.gamescreen.TileImage;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import org.jetbrains.annotations.NotNull;

public class TileMapCreator extends Tile {
    public TileMapCreator(@NotNull TileImage baseImage, @NotNull TileImage characterImage, @NotNull TileImage otherImage, @NotNull TileImage shadedImage, @NotNull TileImage targetArea) {
        super(baseImage, characterImage, otherImage, shadedImage, targetArea);
        setOnMousePressed(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                setImageToSelected();
            }
        });
    }
    public TileMapCreator(@NotNull TileImage baseImage) {
        this(baseImage, new TileImageMapCreator(), new TileImageMapCreator(), new TileImageMapCreator(), new TileImageMapCreator());
    }

    public void setImageToSelected() {
        if (MapCreator.selectedListItem.tileType.imageType == TileType.ImageType.BASE) {
            ((TileImageMapCreator)tileImages[0]).changeImageToSelected();
        }
        else if (MapCreator.selectedListItem.tileType.imageType == TileType.ImageType.CHARACTER) {
            ((TileImageMapCreator)tileImages[1]).changeImageToSelected();
        }
        else if (MapCreator.selectedListItem.tileType.imageType == TileType.ImageType.OTHER) {
            ((TileImageMapCreator)tileImages[2]).changeImageToSelected();
        }
        else if (MapCreator.selectedListItem.tileType.imageType == TileType.ImageType.SHADED) {
            ((TileImageMapCreator)tileImages[3]).changeImageToSelected();
        }
        else if (MapCreator.selectedListItem.tileType.imageType == TileType.ImageType.AREA) {
            ((TileImageMapCreator)tileImages[4]).changeImageToSelected();
        }
    }
    public void resetTile() {
        ((TileImageMapCreator)tileImages[0]).resetImageToFloor();
        for (int i = 1; i < tileImages.length; i++) {
            tileImages[i].resetTileImage();
        }
    }
    public void setBaseImage(Image baseImage) {
        tileImages[0].setImage(baseImage);
    }
    public void setOtherImage(Image otherImage) {
        tileImages[2].setImage(otherImage);
    }
    public void resetCharacterImage() { tileImages[1].setImage(null); }

    public void setBaseImageToFloor() { ((TileImageMapCreator)tileImages[0]).resetImageToFloor(); }

    public boolean isWall() { return ((TileImageMapCreator)tileImages[0]).getTileType() == TileType.WALL; }
    public boolean isShaded() { return ((TileImageMapCreator)tileImages[3]).getTileType() == TileType.SHADED; }
    public boolean isSpawnAreaGuards() { return ((TileImageMapCreator)tileImages[4]).getTileType() == TileType.SPAWN_AREA_GUARDS; }
    public boolean isSpawnAreaIntruders() { return ((TileImageMapCreator)tileImages[4]).getTileType() == TileType.SPAWN_AREA_INTRUDERS; }
    public boolean isTargetArea() { return ((TileImageMapCreator)tileImages[4]).getTileType() == TileType.TARGET_AREA; }
}
