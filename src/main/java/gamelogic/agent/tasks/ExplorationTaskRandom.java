package gamelogic.agent.tasks;

import gamelogic.agent.tasks.TaskContainer.TaskType;

public class ExplorationTaskRandom implements TaskInterface {

    private TaskType type = TaskType.EXPLORATION;
    int min = 0;
    int max = 100;
    int w0 = 25;
    int w1 = 50;
    int w2 = 75;

    @Override
    public int performTask() {
        
        int r = (int)(Math.random()*(max-min+1)+min);
        if(r<=w0){
            w0 = 60;
            w1 = 73;
            w2 = 86;
            return 0;
        } else if (r>w0 && w1<=r) {
            w0 = 70;
            w1 = 80;
            w2 = 90;
            return 1;
        }

        // TODO Shouldn't turning 180 degrees be removed here? Also for presentation / report, what this baseline does is called a random walk
        else if(r>w1 && w2<=r){
            w0 = 70;
            w1 = 80;
            w2 = 90;
            return 2;
        }

        else {
            w0 = 70;
            w1 = 80;
            w2 = 90;
            return 3;
        }
    }

    @Override
    public TaskType getType() {
        return type;
    }

    @Override
    public TaskInterface newInstance() { return new ExplorationTaskRandom(); }
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