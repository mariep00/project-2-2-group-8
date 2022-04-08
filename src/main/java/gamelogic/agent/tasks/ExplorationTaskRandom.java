package gamelogic.agent.tasks;

import java.util.Stack;

import gamelogic.maps.graph.ExplorationGraph;
import gamelogic.agent.tasks.TaskContainer.TaskType;

public class ExplorationTaskRandom implements TaskInterface {

    private Stack<Integer> decision;
    private TaskType type = TaskType.EXPLORATION;
    int min = 0;
    int max = 100;
    int w0 = 25;
    int w1 = 50;
    int w2 = 75;

    public ExplorationTaskRandom () {
        decision = new Stack<>();
    }

    @Override
    public Stack<Integer> performTask(ExplorationGraph graph, double orientation) {
        
        int r = (int)(Math.random()*(max-min+1)+min);
        if(r<=w0){
            w0 = 60;
            w1 = 73;
            w2 = 86;
            decision.push(0);
        } else if (r>w0 && w1<=r) {
            w0 = 70;
            w1 = 80;
            w2 = 90;
            decision.push(1);
        }

        else if(r>w1 && w2<=r){
            w0 = 70;
            w1 = 80;
            w2 = 90;
            decision.push(2);
        }

        else {
            w0 = 70;
            w1 = 80;
            w2 = 90;
            decision.push(3);
        }

        return decision;

    }

    @Override
    public TaskType getType() {
        return type;
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