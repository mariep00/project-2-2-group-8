package gamelogic.controller.endingconditions;

import gamelogic.controller.Controller;
import gamelogic.controller.State;
import gamelogic.controller.gamemodecontrollers.ControllerSurveillance;
import gamelogic.maps.ScenarioMap;
import gamelogic.maps.Tile;
import gamelogic.maps.graph.ExplorationGraph;

//TODO Add the ending conditions for the surveillance gamemode
public class EndingSurveillance implements EndingConditionInterface {

    private static ScenarioMap map;
    private static double timerInsideTarget;
    private static double timerOutsideTarget;
    private static ControllerSurveillance controller;
    private static double elapsedTimeInside;
    private static double elapsedTimeOutside;
    private int targetVisitCounter;
    private State currentState;

    private static final int goalTimeInside = 3;
    private static final int goalTargetGap = 5;

    public EndingSurveillance(ScenarioMap map){
        this.map = map;
        timerInsideTarget = 0;
        timerOutsideTarget = 0;
        targetVisitCounter = 0;
        currentState = null;
    }

    @Override
    public boolean gameFinished() {
        /*
        1. All intruders are caught
               - what will happen to intruders when caught?

               The code inside the for loop will works if we assume agents position will be set to null once they are caught
         */
        for(int i= controller.getNumberOfGuards(); i<= controller.getNumberOfGuards() + controller.getNumberOfIntruders(); i++) {
            if (currentState.getAgentPosition(i) == null){
                if (i == controller.getNumberOfGuards() + controller.getNumberOfIntruders()){
                    return true;
                }
                continue;
            }
            else break;
        }

        //2. Intruders reach the target (and stay inside)
            //This part is done :)

        // Stay X amount of seconds inside target area
        for (int i= controller.getNumberOfGuards(); i<= controller.getNumberOfGuards() + controller.getNumberOfIntruders(); i++) {
            if (map.getTile(currentState.getAgentPosition(i)).getType() == Tile.Type.TARGET_AREA) {
                targetVisitCounter++;
                timerOutsideTarget = 0;
                if (countSecondsInsideTargetArea()) {
                    return true;
                }
            } else if (map.getTile(currentState.getAgentPosition(i)).getType() != Tile.Type.TARGET_AREA) {
                timerInsideTarget = 0;
                countSecondsOutsideTargetArea();
            }

            //twice in ≥ 3 sec
            if (targetVisitCounter >= 2 && countSecondsOutsideTargetArea() && map.getTile(currentState.getAgentPosition(i)).getType() == Tile.Type.TARGET_AREA) {
                return true;
            }
        }
        return false;
    }

    public static boolean countSecondsInsideTargetArea(){
        if (timerInsideTarget == 0){
            timerInsideTarget = controller.getTime();
        }
        else{
            elapsedTimeInside =  controller.time - timerInsideTarget;
            if (elapsedTimeInside == goalTimeInside) return true;
        }
        return false;
    }

    public static boolean countSecondsOutsideTargetArea(){
        if (timerOutsideTarget == 0){
            timerOutsideTarget = controller.getTime();
        }
        else{
            elapsedTimeOutside = controller.time - timerOutsideTarget;
            if (elapsedTimeOutside >= goalTargetGap) return true;
        }
        return false;
    }

    public void updateState (State state){
        currentState = state;

    }
}
