package gamelogic.controller.endingconditions;

import gamelogic.controller.Controller;
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

    @Override
    public boolean mode() {
        return false;
    }

    @Override
    public EndingConditionInterface newInstance() {
        return new EndingSurveillance(map);
    }

    public void updateState (Controller controller){
        for (int i = map.getNumGuards(); i < map.getNumGuards()+map.getNumIntruders(); i++) {
            if (controller.getAgent(i) == null) {
                if (i == (map.getNumGuards() + map.getNumIntruders())-1) {
                    guardsWin = true;
                    return;
                }
            } else break;
        }
        for (int i = 0; i < timeInTargetArea.length; i++) {
            int agentIndex = i + map.getNumGuards();
            if (controller.getAgent(agentIndex) != null) {
                if (map.getTile(controller.getCurrentState().getAgentPosition(agentIndex)).getType() == Tile.Type.TARGET_AREA) {
                    timeInTargetArea[i] += controller.getTimestep();
                } else {
                    timeInTargetArea[i] = 0;
                }
            }
        }
    }

    public boolean getWhoWon() {
        return guardsWin;
    }
}
