package controller;

import controller.graph.ExplorationGraph;
import java.util.concurrent.ThreadLocalRandom;

public class RandomBrain implements BrainInterface {

    private int decision;
    int min = 0;
    int max = 100;
    int w0 = 25;
    int w1 = 50;
    int w2 = 75;

    @Override
    public int makeDecision(ExplorationGraph graph) {

        int r = (int)(Math.random()*(max-min+1)+min);

        if(r<=w0){
            w0 = 50; //TODO adjust the weights
            w1 = 20;
            w2 = 10;

            return 0;
        }

        else if(r>w0 && w1<=r){
            w0 = 70;
            w1 = 10;
            w2 = 10;
            return 1;
        }

        else if(r>w1 && w2<=r){
            w0 = 70;
            w1 = 10;
            w2 = 10;
            return 2;
        }

        else {
            w0 = 70;
            w1 = 10;
            w2 = 10;
            return 3;
        }

    }

}


//0 - move forward
//1 - turn 90deg
//2 - turn 180deg
//3 - turn 270deg

//         270
//          |
// 180 ----------- 0
//          |
//          90