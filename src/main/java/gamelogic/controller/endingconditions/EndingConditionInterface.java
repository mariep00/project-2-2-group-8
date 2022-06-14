package gamelogic.controller.endingconditions;

public interface EndingConditionInterface {
    boolean gameFinished();
    boolean mode();
    EndingConditionInterface newInstance();
}
