package gamelogic.agent.tasks.guard;

import datastructures.Vector2D;
import gamelogic.agent.AStar;
import gamelogic.agent.tasks.TaskContainer;
import gamelogic.agent.tasks.TaskInterface;
import gamelogic.agent.tasks.general.ExplorationInDirection;
import gamelogic.controller.Controller;
import gamelogic.controller.MovementController;
import gamelogic.controller.VisionController;
import gamelogic.datacarriers.Sound;
import gamelogic.datacarriers.VisionMemory;
import gamelogic.maps.graph.ExplorationGraph;
import gamelogic.maps.graph.Node;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

public class FindSoundSource implements TaskInterface {
    private Sound soundToFind;
    private Stack<Integer> futureMoves;
    private ExplorationGraph explorationGraph;
    private Vector2D goal;
    private ExplorationInDirection explorationInDirection;

    private Vector2D positionWhenTaskStarted;

    private int tickCount = 0;

    private int maxDistance;
    private int minDistance;
    private boolean firstTime = true;
    private boolean finished = false;

    @Override
    public int performTask(ExplorationGraph graph, double orientation, double pheromoneMarkerDirection, List<Sound> sounds, VisionMemory[] guardsSeen, VisionMemory[] intrudersSeen) {
        this.explorationGraph = graph;
        if (firstTime) {
            positionWhenTaskStarted = graph.getCurrentPosition().COORDINATES;
            maxDistance = (int) Math.round(Controller.addNoise(16*soundToFind.loudness(), 8));
            minDistance = (int ) Math.round(Controller.addNoise(((float) 9/2)*soundToFind.loudness(), 8));
            firstTime = false;
        }
        // Keep in mind this will most of the times be called once, while if there's is no unmatched sound i.e. source is found agent will perform different task,
        // and if sound is still unmatched a new tasks with angle will be created. Tho, could be that source is never found.
        if ((explorationInDirection == null || explorationInDirection.isFinished()) && (futureMoves == null || futureMoves.isEmpty())) {
            boolean pathFound = false;
            for (int i = maxDistance; i >= minDistance; i--) {
                Node node = graph.getNode(VisionController.calculatePoint(positionWhenTaskStarted, i, soundToFind.angle()));
                if (node != null && !node.getTile().isWall()) {
                    LinkedList<Vector2D> path = AStar.calculate(graph, graph.getCurrentPosition(), node);
                    if (path.size() <= maxDistance) {
                        this.futureMoves = MovementController.convertPath(graph, orientation, path, true);
                        goal = node.COORDINATES;
                        pathFound = true;
                        break;
                    }
                }
            }
            if (!pathFound) {
                explorationInDirection = new ExplorationInDirection();
                goal = VisionController.calculatePoint(positionWhenTaskStarted, maxDistance, soundToFind.angle());
                explorationInDirection.setTarget(goal);
            }
        }
        tickCount++;
        finished = explorationGraph.getCurrentPosition().COORDINATES.equals(goal);
        if (futureMoves != null) return futureMoves.pop();
        else {
            finished = explorationGraph.getCurrentPosition().COORDINATES.dist(goal) <= minDistance;
            return explorationInDirection.performTask(graph, orientation, pheromoneMarkerDirection, sounds, guardsSeen, intrudersSeen);
        }
    }

    @Override
    public boolean isFinished() { return finished; }

    @Override
    public void setTarget(Sound target) { this.soundToFind = target; }

    @Override
    public int getTickCount() { return tickCount; }

    @Override
    public TaskContainer.TaskType getType() { return TaskContainer.TaskType.FIND_SOUND_SOURCE; }

    @Override
    public TaskInterface newInstance() { return new FindSoundSource(); }
}
