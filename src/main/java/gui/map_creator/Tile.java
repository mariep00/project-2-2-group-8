package gui.map_creator;

import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.StackPane;
import org.jetbrains.annotations.NotNull;

public class Tile extends StackPane {
    public static final int tileSize = 45;
    // Index 0 --> base image (floor, wall..)
    // Index 1 --> character (guard, intruder..)
    // Index 2 --> Something else (teleport, scent trail..)
    private final TileImage[] tileImages = new TileImage[3];

    public Tile(@NotNull TileImage baseImage, @NotNull TileImage characterImage, @NotNull TileImage otherImage) {
        super(baseImage, characterImage, otherImage);
        tileImages[0] = baseImage;
        tileImages[1] = characterImage;
        tileImages[2] = otherImage;

        setOnMousePressed(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                setImageToSelected();
            }
        });
    }
    public Tile(@NotNull TileImage baseImage) {
        this(baseImage, new TileImage(), new TileImage());
    }
    public Tile(@NotNull TileImage baseImage, @NotNull TileImage otherImage) {
        this(baseImage, new TileImage(), otherImage);
    }

    public void resetTile() {
        for (TileImage tileImage : tileImages) {
            tileImage.resetTileImage();
        }
    }

    public void setImageToSelected() {
        if (MapCreator.selectedListItem.imageType == ImageType.BASE) {
            tileImages[0].changeImageToSelected();
        }
        else if (MapCreator.selectedListItem.imageType == ImageType.CHARACTER) {
            tileImages[1].changeImageToSelected();
        }
        else if (MapCreator.selectedListItem.imageType == ImageType.OTHER) {
            tileImages[2].changeImageToSelected();
        }
    }
    public void setBaseImage(Image baseImage) {
        tileImages[0].setImage(baseImage);
    }
    public void setCharacterImage(Image characterImage) {
        tileImages[1].setImage(characterImage);
    }
    public void setOtherImage(Image otherImage) {
        tileImages[2].setImage(otherImage);
    }
    public void resetCharacterImage() { tileImages[1].setImage(null); }

    public void setBaseImageToGrid() { tileImages[0].changeImageToGrid(); }
}
