package machinelearning.evasion;

import datastructures.Vector2D;
import gamelogic.agent.Agent;
import gamelogic.maps.Tile;

public class GameStateUtil {
    //0 - move forward
    //1 - turn 90deg
    //3 - turn 270deg
    //4 - idle

    // Remember to pass the right current possition, i.e. absolute position instead of the relative position
    public static double getStateForDirection(Agent agent, Tile[][] map, Vector2D currentPosition, double angle, int movementTask) {
        double angleToMoveTo = angle <= 179 ? angle + 180 : 180 - angle;
        if (movementTask == 0) {
            Vector2D nextPosition = currentPosition.getSide(agent.getOrientation());
            if (map[nextPosition.y][nextPosition.x].isWall()) return -1;
            else {
                double diff = Math.abs(agent.getOrientation()-angleToMoveTo);
                if ((diff > 180 ? 360 - diff : diff) < 60) {
                    return 1;
                }
                else return 0;
            }
        }
        else if (movementTask == 1 || movementTask == 3){
            double newOrientation = movementTask == 1 ? addAngle(agent.getOrientation(), 90) : addAngle(agent.getOrientation(), 270);
            double diff1 = Math.abs(agent.getOrientation()-angleToMoveTo);
            double diff2 = Math.abs(newOrientation-angleToMoveTo);
            if (diff2 < diff1) return 1;
            else return 0;
        }
        else return 0;

    }

    private static double addAngle(double angle, double toAdd) {
        return angle+toAdd < 360 ? angle+toAdd : (angle+toAdd)-360;
    }

}
