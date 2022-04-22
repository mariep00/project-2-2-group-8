package gamelogic.controller.endingconditions;

import gamelogic.controller.Controller;
import gamelogic.maps.Tile;
import gamelogic.maps.graph.ExplorationGraph;

//TODO Add the ending conditions for the surveillance gamemode
public class EndingSurveillance implements EndingConditionInterface {

    private ExplorationGraph graph;
    private static double timerInsideTarget;
    private static double timerOutsideTarget;
    private static Controller controller;
    private static double elapsedTimeInside;
    private static double elapsedTimeOutside;
    private int targetVisitCounter;

    private static final int goalTimeInside = 3;
    private static final int goalTargetGap = 5;

    public EndingSurveillance(ExplorationGraph graph){
        this.graph = graph;
        timerInsideTarget = 0;
        timerOutsideTarget = 0;
        targetVisitCounter = 0;
    }

    @Override
    public boolean gameFinished() {
        /*
        1. All intruders are caught
               - what will happen to intruders when caught?
         */


        //2. Intruders reach the target (and stay inside)
            //This part is done :)

        // Stay X amount of seconds inside target area
        if (graph.getCurrentPosition().getTile().getType() == Tile.Type.TARGET_AREA) {
            targetVisitCounter ++;
            timerOutsideTarget= 0;
            if (countSecondsInsideTargetArea()) {
                return true;
            }
        }
        else if (graph.getCurrentPosition().getTile().getType() != Tile.Type.TARGET_AREA) {
            timerInsideTarget = 0;
            countSecondsOutsideTargetArea();
        }

        //twice in ≥ 3 sec
        if (targetVisitCounter >= 2 && countSecondsOutsideTargetArea() && graph.getCurrentPosition().getTile().getType() == Tile.Type.TARGET_AREA){
            return true;
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
}
