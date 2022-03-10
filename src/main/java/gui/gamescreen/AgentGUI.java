package gui.gamescreen;

import controller.Agent;
import controller.EndingExplorationMap;

public class AgentGUI extends Agent {
    //public final TileImage agentTileImage;
    public final AgentType agentType;

    public AgentGUI(int base_speed, int sprint_speed, double orientation, int brainID, EndingExplorationMap explorationMap, AgentType agentType) {
        super(base_speed, sprint_speed, orientation, brainID, explorationMap);
        //this.agentTileImage = new TileImage(ImageContainer.getInstance().getAgent(agentType, Vector2D.Direction.SOUTH));
        this.agentType = agentType;
    }
}
