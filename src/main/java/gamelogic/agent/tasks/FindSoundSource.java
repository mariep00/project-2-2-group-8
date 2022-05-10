package gamelogic.agent.tasks;

import datastructures.Vector2D;
import datastructures.Vector2DDouble;
import gamelogic.agent.AStar;
import gamelogic.controller.MovementController;
import gamelogic.datacarriers.Sound;
import gamelogic.maps.graph.ExplorationGraph;

import java.util.LinkedList;
import java.util.Stack;

public class FindSoundSource implements TaskInterface {
    private int distanceToSearch = 10;
    private Sound soundToFind;
    private Stack<Integer> futureMoves;
    private ExplorationGraph explorationGraph;
    private Vector2D goal;

    private int tickCount = 0;

    @Override
    public int performTask(ExplorationGraph graph, double orientation, double pheromoneMarkerDirection) {
        this.explorationGraph = graph;
        // Keep in mind this will most of the times be called once, while if there's is no unmatched sound i.e. source is found agent will perform different task,
        // and if sound is still unmatched a new tasks with angle will be created. Tho, could be that source is never found.
        if (futureMoves.isEmpty()) {
            // Create a double vector in the direction of the sound
            // Start at the maximum distance we want to check, move closer if we can't reach (or don't know yet) that position
            double xDir = Math.cos(Math.toRadians(soundToFind.angle()));
            double yDir = Math.sin(Math.toRadians(soundToFind.angle()));
            Vector2DDouble goalDouble = new Vector2DDouble(xDir, yDir);
            for (int i = distanceToSearch; i >= 2; i--) {
                Vector2D pos = goalDouble.withLength(i).round();
                if (graph.getNode(pos) != null) {
                    goal = pos;
                    break;
                }
            }

            if (goal != null) {
                LinkedList<Vector2D> nodesToGoal = AStar.calculate(graph, graph.getCurrentPosition(), graph.getNode(goal));
                this.futureMoves = MovementController.convertPath(graph, orientation, nodesToGoal, -1);
            }
            // There is no goal, i.e. the direction of the sound is not explored yet
            // Create futureMoves s.t. the agent rotates towards the direction of the sound
            else {
                // First value is the angle lower bound, second the angle upper bound, third the corresponding discrete angle
                // I.e. an angle between 315 and 45 degrees has a corresponding discrete angle of 0 degrees
                int[][] directionRanges = {{315, 45, 0}, {45, 135, 90}, {135, 225, 180}, {225, 315, 270}};
                int direction = -1;
                for (int[] directionRange : directionRanges) {
                    if (soundToFind.angle() >= directionRange[0] && soundToFind.angle() <= directionRange[1]) {
                        direction = directionRange[2];
                    }
                }

                if (direction != -1) {
                    int degreesToTurn = direction - (int) orientation;
                    if (degreesToTurn == 90) {
                        futureMoves.push(1);
                    }
                    else if (degreesToTurn == -90) {
                        futureMoves.push(3);
                    }
                    else if (degreesToTurn == 180 || degreesToTurn == -180) {
                        futureMoves.push(1);
                        futureMoves.push(1);
                    }
                }
            }
        }
        tickCount++;
        return futureMoves.pop();
    }

    @Override
    public boolean isFinished() { return explorationGraph.getCurrentPosition().COORDINATES.equals(goal); }

    @Override
    public void setTarget(Sound target) { this.soundToFind = target; }

    @Override
    public int getTickCount() { return tickCount; }

    @Override
    public boolean equals(Object other) {
        if (other == null) return false;
        if (other.getClass() == this.getClass()) {
            return ((FindSoundSource) other).getTarget().equals(this.soundToFind);
        }
        return false;
    }

    @Override
    public TaskContainer.TaskType getType() { return TaskContainer.TaskType.FIND_SOUND_SOURCE; }

    @Override
    public TaskInterface newInstance() { return new FindSoundSource(); }
}
