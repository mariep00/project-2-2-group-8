package gui.gamescreen.controller;

import java.util.concurrent.atomic.AtomicBoolean;

public interface ControllerGUIInterface {
    void hideVision(int agentIndex);
    void showVision(int agentIndex);
    void setSimulationDelay(int val);
    void runSimulation();
    void stopSimulation();
    void pauseThreads();
    void continueThreads();
    AtomicBoolean getRunSimulation();
    boolean doesAgentExist(int agentIndex);
}
