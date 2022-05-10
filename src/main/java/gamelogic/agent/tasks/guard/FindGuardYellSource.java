package gamelogic.agent.tasks.guard;

import gamelogic.agent.tasks.TaskContainer;
import gamelogic.agent.tasks.TaskInterface;
import gamelogic.datacarriers.Sound;
import gamelogic.maps.graph.ExplorationGraph;

import java.util.Stack;

public class FindGuardYellSource implements TaskInterface {
    private Stack<Integer> futureMoves;
    private Sound guardYellToFind;

    @Override
    public int performTask(ExplorationGraph graph, double orientation) {
        if (futureMoves.isEmpty()) {
            // The sound has an angle, but because the distance, as the crow flies, is 50 (or more?, didn't discuss yet), also taking into account that there is noise added,
            // the range of where it actually is, is enormous. We have to take into account loudness to determine how far it is, but we are not allowed to use the fact that we know the distance?
            // Point is; if there are walls in between, and the agent hasn't explored that part of the map, we need to something like explore with the preference of frontiers in the direction of the yell.

            // Idea; give frontier exploration task an angle as input that it prefers (for normal frontier exploration it would be the opposite of the sum of the pheromones), now it's the angle.
            // This angle is computed as follows; Initially it's known together with the loudness, predict some position (how?) where it could be. Every tick update the angle we want to go to, s.t. the angle points from the current
            // position towards the predicted location of the yell. Then just everytime pick the frontier that is the closest towards this predicted direction.
        }
        return futureMoves.pop();
    }

    @Override
    public void setTarget(Sound target) { this.guardYellToFind = target; }

    @Override
    public TaskContainer.TaskType getType() {
        return TaskContainer.TaskType.FIND_GUARD_YELL_SOURCE;
    }

    @Override
    public TaskInterface newInstance() {
        return new FindGuardYellSource();
    }
}
