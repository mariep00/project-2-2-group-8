package gui.gamescreen;

import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import org.jetbrains.annotations.NotNull;

public class Tile extends StackPane {
    public static final int tileSize = 25;
    // Index 0 --> base image (floor, wall..)
    // Index 1 --> character (guard, intruder..)
    // Index 2 --> Something else (scent trail..)
    // Index 3 --> Shaded
    // Index 4 --> Target area
    // Index 5 --> Undiscovered
    // Index 6 --> Vision
    protected final TileImage[] tileImages = new TileImage[7];

    public Tile(@NotNull TileImage baseImage, @NotNull TileImage characterImage, @NotNull TileImage otherImage, @NotNull TileImage shadedImage, @NotNull TileImage targetArea, @NotNull TileImage undiscoveredImage, @NotNull TileImage inVisionImage) {
        super(baseImage, otherImage, characterImage, targetArea, shadedImage, undiscoveredImage, inVisionImage);
        tileImages[0] = baseImage;
        tileImages[1] = characterImage;
        tileImages[2] = otherImage;
        tileImages[3] = shadedImage;
        tileImages[4] = targetArea;
        tileImages[5] = undiscoveredImage;
        tileImages[6] = inVisionImage;

        tileImages[3].setOpacity(0.45);
        tileImages[4].setOpacity(0.3);
        tileImages[5].setOpacity(0.5);
        tileImages[6].setOpacity(0.125);
    }
    public Tile(@NotNull TileImage baseImage) {
        this(baseImage, new TileImageAgent(), new TileImage(), new TileImage(), new TileImage(), new TileImage(), new TileImage());
    }
    public Tile(@NotNull TileImage baseImage, @NotNull TileImage undiscoveredImage) { this (baseImage, new TileImageAgent(), new TileImage(), new TileImage(), new TileImage(), undiscoveredImage, new TileImage()); }
    public void setCharacter(GameScreen gameScreen, Image characterImage, int agentIndex) {
        tileImages[1].setImage(characterImage);
        ((TileImageAgent) tileImages[1]).setAgentIndex(agentIndex);
        ((TileImageAgent) tileImages[1]).setGameScreen(gameScreen);
    }
    public TileImageAgent getTileImageAgent() { return (TileImageAgent) tileImages[1]; }

    public void resetCharacterImage() {
        tileImages[1].resetTileImage();
        ((TileImageAgent) tileImages[1]).setAgentIndex(-1);
    }
    public void setShaded(Image shadedImage) {
        tileImages[3].setImage(shadedImage);
    }
    public void setTargetArea(Image targetArea) { tileImages[4].setImage(targetArea); }
    public void setToExplored() { tileImages[5].resetTileImage(); }
    public void setToInVision(Image inVisionImage) { tileImages[6].setImage(inVisionImage); }
    public void setToOutOfVision() { tileImages[6].resetTileImage(); }
    public void zoom(double zoomfact){
        this.resize(this.getWidth()*zoomfact, this.getHeight()*zoomfact);
    }
}
