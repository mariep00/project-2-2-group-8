package gamelogic.controller;

import gamelogic.Vector2D;
import gamelogic.maps.TeleportEntrance;
import gamelogic.maps.Tile;

public class MovementController {
    private final Controller controller;

    public MovementController(Controller controller) { this.controller = controller; }
    public void moveAgent(int agentIndex, int movementTask) {
        //0 - move forward
        //1 - turn 90deg
        //2 - turn 180deg
        //3 - turn 270deg

        switch (movementTask) {
            case 0:
                updateAgentPosition(agentIndex, agentMoveForward(agentIndex));
                break;
            case 1:
                updateAgentOrientation(agentIndex, 90);
                break;
            case 2:
                updateAgentOrientation(agentIndex, 180);
                break;
            case 3:
                updateAgentOrientation(agentIndex,270);
                break;
        }
    }
    protected void updateAgentPosition(int agentIndex, Vector2D pos) {
        controller.nextState.setAgentPosition(agentIndex, pos);
        controller.agents[agentIndex].updatePosition(controller.convertAbsoluteToRelativeSpawn(pos, agentIndex));
    }
    protected void updateAgentOrientation(int agentIndex, double orientationToAdd) {
        controller.agents[agentIndex].updateOrientation(orientationToAdd);
    }

    private Vector2D agentMoveForward(int agentIndex) {
        int numberOfSteps = getNumberOfStepsAgentCanPerform(agentIndex);
        Vector2D lastPos = controller.currentState.getAgentPosition(agentIndex);
        for (int i = 1; i <= numberOfSteps; i++) {
            Vector2D pos = lastPos.getSide(controller.agents[agentIndex].getOrientation(), i);
            if (pos.x >= controller.scenarioMap.getWidth() || pos.x < 0 || pos.y >= controller.scenarioMap.getHeight() || pos.y < 0) return lastPos;
            Tile tileAtPos = controller.scenarioMap.getTile(pos);
            if (tileAtPos.isWall()) return lastPos;
            if (tileAtPos.isTeleportEntrance()) {
                controller.agents[agentIndex].createTeleportDestinationNode(controller.convertAbsoluteToRelativeSpawn(pos, agentIndex),
                        controller.convertAbsoluteToRelativeSpawn(((TeleportEntrance) tileAtPos.getFeature()).getExit(), agentIndex),
                        tileAtPos, controller.scenarioMap.getTile(((TeleportEntrance) tileAtPos.getFeature()).getExit()));
                return posAfterTeleport(agentIndex, tileAtPos);
            }
            if (isAgentAtPos(pos)) return lastPos;
            else lastPos = pos;
        }
        return lastPos;
    }

    private Vector2D posAfterTeleport(int agentIndex, Tile tileAtPos) {
        TeleportEntrance tp = (TeleportEntrance)tileAtPos.getFeature();
        controller.agents[agentIndex].changeOrientation(tp.getOrientation());
        return tp.getExit();
    }

    private int getNumberOfStepsAgentCanPerform(int agentIndex) {
        double numberOfSteps = controller.agents[agentIndex].getBase_speed()*controller.timestep;
        if (numberOfSteps < 1) return 1;
        else return (int) Math.round(numberOfSteps);
    }

    private boolean isAgentAtPos(Vector2D pos) {
        for (int i = 0; i < controller.numberOfGuards+controller.numberOfIntruders; i++) {
            if (controller.currentState.getAgentPosition(i).equals(pos)) return true;
        }
        return false;
    }
}
