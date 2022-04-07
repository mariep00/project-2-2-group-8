package gamelogic.controller;

import gamelogic.Vector2D;
import gamelogic.maps.TeleportEntrance;
import gamelogic.maps.Tile;

public class MovementController {
    protected void updateAgentPosition(int agentIndex, Vector2D pos) {
        agentPositions[agentIndex] = pos;
        agentsGuards[agentIndex].updatePosition(convertAbsoluteToRelativeSpawn(pos, agentIndex));
    }
    protected void updateAgentOrientation(int agentIndex, double orientationToAdd) {
        agentsGuards[agentIndex].updateOrientation(orientationToAdd);
    }

    private Vector2D agentMoveForward(int agentIndex) {
        int numberOfSteps = getNumberOfSteps(agentIndex);
        Vector2D lastPos = agentPositions[agentIndex];
        for (int i = 1; i <= numberOfSteps; i++) {
            Vector2D pos = agentPositions[agentIndex].getSide(agentsGuards[agentIndex].getOrientation(), i);
            if (pos.x >= scMap.getWidth() || pos.x < 0 || pos.y >= scMap.getHeight() || pos.y < 0) return lastPos;
            Tile tileAtPos = scMap.getTile(pos);
            if (tileAtPos.isWall()) return lastPos;
            if (tileAtPos.isTeleportEntrance()) {
                agentsGuards[agentIndex].creatTeleportDestinationNode(convertAbsoluteToRelativeSpawn(pos, agentIndex), convertAbsoluteToRelativeSpawn(((TeleportEntrance) tileAtPos.getFeature()).getExit(), agentIndex), tileAtPos, scMap.getTile(((TeleportEntrance) tileAtPos.getFeature()).getExit()));
                return posAfterTeleport(agentIndex, tileAtPos);
            }
            if (isAgentAtPos(pos)) return lastPos;
            else lastPos = pos;
        }
        return lastPos;
    }

    private Vector2D posAfterTeleport(int agentIndex, Tile tileAtPos) {
        TeleportEntrance tp = (TeleportEntrance)tileAtPos.getFeature();
        agentsGuards[agentIndex].changeOrientation(tp.getOrientation());
        return tp.getExit();
    }

    private int getNumberOfSteps(int agentIndex) {
        double numberOfSteps = agentsGuards[agentIndex].getBase_speed()*timestep;
        if (numberOfSteps < 1) return 1;
        else return (int) Math.round(numberOfSteps);
    }

    private boolean isAgentAtPos(Vector2D pos) {
        for (int i = 0; i < agentPositions.length; i++) {
            if (agentPositions[i].equals(pos)) return true;
        }
        return false;
    }
}
