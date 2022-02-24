package gui.game_screen;

import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import org.jetbrains.annotations.NotNull;

public class Tile extends StackPane {
    public static final int tileSize = 35;
    // Index 0 --> base image (floor, wall..)
    // Index 1 --> character (guard, intruder..)
    // Index 2 --> Something else (teleport, scent trail..)
    protected final TileImage[] tileImages = new TileImage[3];

    public Tile(@NotNull TileImage baseImage, @NotNull TileImage characterImage, @NotNull TileImage otherImage) {
        super(baseImage, otherImage, characterImage);
        tileImages[0] = baseImage;
        tileImages[1] = characterImage;
        tileImages[2] = otherImage;
    }
    public Tile(@NotNull TileImage baseImage) {
        this(baseImage, new TileImage(), new TileImage());
    }
    public Tile(@NotNull TileImage baseImage, @NotNull TileImage otherImage) { this(baseImage, new TileImage(), otherImage); }

    public void setCharacterImage(Image characterImage) {
        tileImages[1].setImage(characterImage);
    }
    public void resetCharacterImage() {
        tileImages[1].resetTileImage();
    }
}
