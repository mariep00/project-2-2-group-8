package gui.gamescreen;

import javafx.scene.image.Image;

public class TileImageAgent extends TileImage {
    public final AgentGUI agentGUI;

    public TileImageAgent(AgentGUI agentGUI, Image image) {
        super(image);
        this.agentGUI = agentGUI;
    }
}
