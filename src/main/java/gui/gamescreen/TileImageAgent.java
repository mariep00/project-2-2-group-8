package gui.gamescreen;

public class TileImageAgent extends TileImage {
    private GameScreen gameScreen;
    private int agentIndex;

    public TileImageAgent() {
        super();
        gameScreen = null;
        agentIndex = -1;
    }

    public void setAgentIndex(int agentIndex) { this.agentIndex = agentIndex; }
    public void setGameScreen(GameScreen gameScreen) { this.gameScreen = gameScreen; }
    public void onClick() {
        if (gameScreen != null && agentIndex != -1) gameScreen.toggleVision(agentIndex);
    }
}
