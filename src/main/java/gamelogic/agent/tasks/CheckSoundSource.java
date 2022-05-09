package gamelogic.agent.tasks;

import datastructures.Vector2D;
import datastructures.Vector2DDouble;
import gamelogic.agent.AStar;
import gamelogic.controller.MovementController;
import gamelogic.datacarriers.Sound;
import gamelogic.datacarriers.VisionMemory;
import gamelogic.maps.graph.ExplorationGraph;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

public class CheckSoundSource implements TaskInterface {
    private int distanceToCheck = 10;
    private Sound soundToCheck;
    private Stack<Integer> futureMoves;

    @Override
    public int performTask() {
        throw new UnsupportedOperationException("This method is not supported for this class");
    }

    public void setSoundToCheck(Sound soundToCheck) {
        this.soundToCheck = soundToCheck;
    }

    @Override
    public int performTask(ExplorationGraph graph, double orientation, double pheromoneMarkerDirection) {
        // Keep in mind this will most of the times be called once, while if there's is no unmatched sound i.e. source is found agent will perform different task,
        // and if sound is still unmatched a new tasks with angle will be created. Tho, could be that source is never found.
        // TODO Need a finishing condition if source was not found
        if (futureMoves.isEmpty()) {
            double xDir = Math.cos(Math.toRadians(soundToCheck.angle()));
            double yDir = Math.sin(Math.toRadians(soundToCheck.angle()));
            Vector2DDouble goalDouble = new Vector2DDouble(xDir, yDir);
            Vector2D goal = null;
            for (int i = distanceToCheck; i >= 2; i--) {
                Vector2D pos = goalDouble.withLength(i).round();
                if (graph.getNode(pos) != null) {
                    goal = pos;
                    break;
                }
            }

            if (goal != null) {
                LinkedList<Vector2D> nodesToGoal = AStar.calculate(graph, graph.getCurrentPosition(), graph.getNode(goal));
                this.futureMoves = MovementController.convertPath(graph, orientation, nodesToGoal, 3);
            }
            // There is no goal, i.e. the direction of the sound is not explored yet
            // Create futureMoves s.t. the agent rotates towards the direction of the sound
            else {
                // TODO Create futureMoves s.t. the agent rotates towards the direction of the sound
            }
        }
        return futureMoves.pop();
    }

    @Override
    public int performTask(ExplorationGraph graph, double orientation, double pheromoneMarkerDirection, List<Sound> sounds, VisionMemory[] guardsSeen, VisionMemory[] intrudersSeen) {
        throw new UnsupportedOperationException("This method is not supported for this class");
    }

    @Override
    public TaskContainer.TaskType getType() {
        return TaskContainer.TaskType.CHECK_SOUND_SOURCE;
    }

    @Override
    public TaskInterface newInstance() {
        return new CheckSoundSource();
    }
}
