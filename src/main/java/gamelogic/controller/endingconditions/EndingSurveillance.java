package gamelogic.controller.endingconditions;

import gamelogic.controller.State;
import gamelogic.maps.ScenarioMap;
import gamelogic.maps.Tile;

public class EndingSurveillance implements EndingConditionInterface {

    private ScenarioMap map;

    private final int goalTimeInside = 3;

    private final double[] timeInTargetArea;
    private boolean guardsWin = false;

    public EndingSurveillance(ScenarioMap map) {
        this.map = map;
        timeInTargetArea = new double[map.getNumIntruders()];
    }

    @Override
    public boolean gameFinished() {
        if (guardsWin) return true;
        // Stay X amount of seconds inside target area
        for (Double temp : timeInTargetArea) {
            if (temp >= goalTimeInside) return true;
        }
        return false;
    }

    public void updateState (State currentState, double timeStep){
        for (int i = map.getNumGuards(); i<= map.getNumGuards() + map.getNumIntruders(); i++) {
            if (currentState.getAgentPosition(i) == null){
                if (i == map.getNumGuards() + map.getNumIntruders()){
                    guardsWin = true;
                    return;
                }
            } else break;
        }
        for (int i = 0; i < timeInTargetArea.length; i++) {
            int agentIndex = i+map.getNumGuards();
            if (map.getTile(currentState.getAgentPosition(agentIndex)).getType() == Tile.Type.TARGET_AREA) {
                if (timeInTargetArea[i] == 0) {
                    timeInTargetArea[i] += timeStep;
                }
            }
            else {
                timeInTargetArea[i] = 0;
            }
        }
    }
}
