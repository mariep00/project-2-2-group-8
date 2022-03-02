package gui.gamescreen;

import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import org.jetbrains.annotations.NotNull;

public class Tile extends StackPane {
    public static final int tileSize = 25;
    // Index 0 --> base image (floor, wall..)
    // Index 1 --> character (guard, intruder..)
    // Index 2 --> Something else (teleport, scent trail..)
    // Index 3 --> Shaded
    // Index 4 --> Target area
    protected final TileImage[] tileImages = new TileImage[5];

    public Tile(@NotNull TileImage baseImage, @NotNull TileImage characterImage, @NotNull TileImage otherImage, @NotNull TileImage shadedImage, @NotNull TileImage targetArea) {
        super(baseImage, otherImage, characterImage, targetArea, shadedImage);
        tileImages[0] = baseImage;
        tileImages[1] = characterImage;
        tileImages[2] = otherImage;
        tileImages[3] = shadedImage;
        tileImages[4] = targetArea;

        tileImages[3].setOpacity(0.45);
        tileImages[4].setOpacity(0.3);
    }
    public Tile(@NotNull TileImage baseImage) {
        this(baseImage, new TileImage(), new TileImage(), new TileImage(), new TileImage());
    }
    public Tile(@NotNull TileImage baseImage, @NotNull TileImage otherImage) { this(baseImage, new TileImage(), otherImage, new TileImage(), new TileImage()); }

    public void setCharacterImage(Image characterImage) {
        tileImages[1].setImage(characterImage);
    }
    public void resetCharacterImage() {
        tileImages[1].resetTileImage();
    }
    public void setShaded(Image shadedImage) {
        tileImages[3].setImage(shadedImage);
    }
    public void setTargetArea(Image targetArea) {
        tileImages[4].setImage(targetArea);
    }
}
