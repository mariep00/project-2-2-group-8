package gamelogic.controller;

import datastructures.Vector2D;
import gamelogic.controller.gamemodecontrollers.ControllerSurveillance;
import gamelogic.maps.TeleportEntrance;
import gamelogic.maps.Tile;
import gamelogic.maps.graph.ExplorationGraph;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Stack;

public class MovementController {
    private final Controller controller;

    public MovementController(Controller controller) { this.controller = controller; }
    public void moveAgent(int agentIndex, int movementTask) {
        // TODO Remove turning 180 degrees?
        //0 - move forward
        //1 - turn 90deg
        //2 - turn 180deg
        //3 - turn 270deg
        //4 - idle

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
            case 4:
                break;
        }
    }
    protected void updateAgentPosition(int agentIndex, Vector2D pos) {
        controller.nextState.setAgentPosition(agentIndex, pos);
        controller.agents[agentIndex].updatePosition(controller.convertAbsoluteToRelativeSpawn(pos, agentIndex));
        if (controller instanceof ControllerSurveillance) {
            ((ControllerSurveillance) controller).soundController.generateWalkSound(agentIndex);
        }
    }
    protected void updateAgentOrientation(int agentIndex, double orientationToAdd) {
        controller.agents[agentIndex].updateOrientation(orientationToAdd);
        if (controller instanceof ControllerSurveillance) {
            ((ControllerSurveillance) controller).soundController.generateTurnSound(agentIndex);
        }
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
            if (controller.agents[i] != null) {
                if (controller.currentState.getAgentPosition(i).equals(pos)) return true;
            }
        }
        return false;
    }
    /**
     * Method to convert a path of Vector2D's to actions in integers for the controller
     * @param graph the exploration graph of the agent
     * @param path the path calculated by e.g. AStar
     * @param length how many moves should be returned, -1 if all
     * @return
     */
    public static Stack<Integer> convertPath (ExplorationGraph graph, double orientation, LinkedList<Vector2D> path, int length) {
        int maxSteps = length;
        if (length == -1) maxSteps = Integer.MAX_VALUE;
        
        Stack<Integer> temporaryStack = new Stack<>();
        Stack<Integer> futureMoves = new Stack<>();
        Vector2D currentPos= graph.getCurrentPosition().COORDINATES;
        Iterator<Vector2D> iterator = path.descendingIterator();
        double current_orientation = orientation;
        int count = 0;

        while (iterator.hasNext() && count <= maxSteps) {
            Vector2D pos = iterator.next();
            int xDif = pos.x - currentPos.x;
            int yDif = pos.y - currentPos.y;
            count++;
            if(xDif==1){
                if(current_orientation==0){
                    temporaryStack.push(0);
                }
                else if(current_orientation==90){
                    temporaryStack.push(3);
                    temporaryStack.push(0);
                }
                else if(current_orientation==180){
                    temporaryStack.push(1);
                    temporaryStack.push(1);
                    temporaryStack.push(0);
                }
                else if(current_orientation==270){
                    temporaryStack.push(1);
                    temporaryStack.push(0);
                }
                current_orientation=0;
            }
            else if(xDif==-1){
                if(current_orientation==180){
                    temporaryStack.push(0);
                }
                else if(current_orientation==270){
                    temporaryStack.push(3);
                    temporaryStack.push(0);
                }
                else if(current_orientation==0){
                    temporaryStack.push(1);
                    temporaryStack.push(1);
                    temporaryStack.push(0);

                }
                else if(current_orientation==90){ 
                    temporaryStack.push(1);
                    temporaryStack.push(0);
                }
                current_orientation=180;
            }
            else if(yDif==1){
                if(current_orientation==90){
                    temporaryStack.push(0);
                }
                else if(current_orientation==180){
                    temporaryStack.push(3);
                    temporaryStack.push(0);
                }
                else if(current_orientation==270){
                    temporaryStack.push(1);
                    temporaryStack.push(1);
                    temporaryStack.push(0);

                }
                else if(current_orientation==0){
                    temporaryStack.push(1);
                    temporaryStack.push(0);
                }
                current_orientation=90;
            }
            else if(yDif==-1){
                if(current_orientation==270){
                    temporaryStack.push(0);
                }
                else if(current_orientation==0){
                    temporaryStack.push(3);
                    temporaryStack.push(0);
                }
                else if(current_orientation==90){
                    temporaryStack.push(1);
                    temporaryStack.push(1);
                    temporaryStack.push(0);

                }
                else if(current_orientation==180){
                    temporaryStack.push(1);
                    temporaryStack.push(0);
                }
                current_orientation=270;
            }
            currentPos=pos;

            if (count>maxSteps) {
                if (current_orientation != orientation) {
                    count--;
                }
            }
        }

        if (length == -1) {
            temporaryStack.push(1);
            temporaryStack.push(1);
            temporaryStack.push(1);
        }

        do{futureMoves.push(temporaryStack.pop());}
        while(!temporaryStack.isEmpty());

        return futureMoves;
    }
}
