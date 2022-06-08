package gamelogic.agent.tasks.general;

import gamelogic.agent.tasks.TaskContainer.TaskType;
import gamelogic.agent.tasks.TaskInterface;
import gamelogic.datacarriers.Sound;
import gamelogic.datacarriers.VisionMemory;
import gamelogic.maps.graph.ExplorationGraph;

import java.util.List;
import java.util.Stack;

public class CaptureTargetAreaTask implements TaskInterface {

    private TaskType type = TaskType.CAPTURE_TARGET_AREA;

    private Stack<Integer> futureMoves;

    private boolean finished = false;


    @Override
    public int performTask(ExplorationGraph graph, double orientation, double pheromoneMarkerDirection, List<Sound> sounds, VisionMemory[] guardsSeen, VisionMemory[] intrudersSeen) {
        if (futureMoves == null || futureMoves.isEmpty()) {
            futureMoves = new Stack<>();
            futureMoves.push(1);
            futureMoves.push(1);
            futureMoves.push(1);
            futureMoves.push(1);
            futureMoves.push(4);
            futureMoves.push(4);
            futureMoves.push(4);
            futureMoves.push(4);
            futureMoves.push(4);
        }
        if (futureMoves.size()==1) finished = true;
        return futureMoves.pop();
    }

    @Override
    public TaskType getType() {
        return type;
    }

    @Override
    public TaskInterface newInstance() {
        return new CaptureTargetAreaTask();
    }

    public boolean isFinished() {
        return finished;
    }
}
