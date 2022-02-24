package gui.map_creator;

import gui.game_screen.Tile;
import gui.game_screen.TileImage;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import org.jetbrains.annotations.NotNull;

public class TileMapCreator extends Tile {
    public TileMapCreator(@NotNull TileImage baseImage, @NotNull TileImage characterImage, @NotNull TileImage otherImage) {
        super(baseImage, characterImage, otherImage);
        setOnMousePressed(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                setImageToSelected();
            }
        });
    }
    public TileMapCreator(@NotNull TileImage baseImage) {
        this(baseImage, new TileImageMapCreator(), new TileImageMapCreator());
    }
    public TileMapCreator(@NotNull TileImage baseImage, @NotNull TileImage otherImage) { this(baseImage, new TileImageMapCreator(), otherImage); }

    public void setImageToSelected() {
        if (MapCreator.selectedListItem.imageType == ImageType.BASE) {
            ((TileImageMapCreator)tileImages[0]).changeImageToSelected();
        }
        else if (MapCreator.selectedListItem.imageType == ImageType.CHARACTER) {
            ((TileImageMapCreator)tileImages[1]).changeImageToSelected();
        }
        else if (MapCreator.selectedListItem.imageType == ImageType.OTHER) {
            ((TileImageMapCreator)tileImages[2]).changeImageToSelected();
        }
    }
    public void resetTile() {
        for (TileImage tileImage : tileImages) {
            tileImage.resetTileImage();
        }
    }
    public void setBaseImage(Image baseImage) {
        tileImages[0].setImage(baseImage);
    }
    public void setOtherImage(Image otherImage) {
        tileImages[2].setImage(otherImage);
    }
    public void resetCharacterImage() { tileImages[1].setImage(null); }

    public void setBaseImageToGrid() { ((TileImageMapCreator)tileImages[0]).changeImageToGrid(); }
}
