package gamelogic.agent.tasks.deciders;

import datastructures.Vector2D;
import gamelogic.agent.AStar;
import gamelogic.agent.tasks.TaskContainer;
import gamelogic.agent.tasks.TaskInterface;
import gamelogic.agent.tasks.guard.FindSoundSource;
import gamelogic.controller.Controller;
import gamelogic.controller.VisionController;
import gamelogic.datacarriers.Sound;
import gamelogic.datacarriers.VisionMemory;
import gamelogic.maps.graph.ExplorationGraph;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class TaskDeciderGuard implements TaskDeciderInterface {
    private final TaskContainer tasks;

    private final double secondsAgoThreshold = 1;
    private final double angleThreshold = 25;

    public TaskDeciderGuard(TaskContainer taskContainer) {
        this.tasks = taskContainer;
    }

    // TODO Would be good to have a guard yell variant for when guard catches an intruder
    @Override
    public TaskInterface getTaskToPerform(ExplorationGraph graph, double pheromoneMarkerDirection, List<Sound> sounds, VisionMemory[] guardsSeen, VisionMemory[] intrudersSeen, List<Sound> guardYells, TaskInterface currentTask, double orientation) {
        // We're going to check on what task to perform based on the priority of a task
        // For example, starting the pursuit when the guard sees an intruder is the most important, thus has the highest priority,
        // thus we will start by checking this.

        // 1. Check if intruder is in vision
        VisionMemory intruderToPursuit = getIntruderInVision(intrudersSeen);
        if (intruderToPursuit != null) {
            // There is an intruder in vision
            // We should start the pursuit task
            TaskInterface pursuitTask = tasks.getTask(TaskContainer.TaskType.GUARD_PURSUIT);
            pursuitTask.setTarget(intruderToPursuit);
            return pursuitTask;
        }

        // 2. Check if there is a guard yell
        Sound guardYell = getGuardYell(guardYells);
        // TODO Right now a guard yell is more important than checking sound input from footsteps. I.e. it will never check for the source of footsteps if it is finding the source of a guard yell. Is that okay? Otherwise we need to store the guard yell same as we do with the VisionMemory.
        if (guardYell != null && (currentTask.getPriority() <= TaskContainer.TaskType.FIND_GUARD_YELL_SOURCE.priority || currentTask.isFinished())) {
            // There was a guard yell, try to find it
           return getTaskToPerformOnGuardYell(graph, orientation, guardYell);
        }

        // TODO Smth that might be an issue; For example guard is already searching for sound source, it should only switch if a different unmatched sound is closer (maybe also take time ago into account)
        // Right now when there is a new unmatched sound it will always switch to this newest one

        // 3. Check if there is an unmatched sound (i.e. is there a sound we cannot link to a guard we've previously seen)
        Sound closestUnmatchedSound = getUnmatchedSound(sounds, guardsSeen);
        if (closestUnmatchedSound != null && (currentTask.getPriority() <= TaskContainer.TaskType.FIND_SOUND_SOURCE.priority || currentTask.isFinished())) {
            // There is a sound we cannot match with another guard
            // This means we should check if it's a guard or an intruder
            FindSoundSource findSoundSource = (FindSoundSource) tasks.getTask(TaskContainer.TaskType.FIND_SOUND_SOURCE);
            findSoundSource.setTarget(closestUnmatchedSound);
            return findSoundSource;
        }

        // 4. Nothing special to do, so just explore
        if (currentTask.isFinished()) {
            if (!graph.frontiers.isEmpty()) {
                return tasks.getTask(TaskContainer.TaskType.EXPLORATION);
            }
            else {
                return tasks.getTask(TaskContainer.TaskType.VISIT_LAST_SEEN_INTRUDER_POSITIONS);
            }
        }

        return currentTask;
    }

    private TaskInterface getTaskToPerformOnGuardYell(ExplorationGraph graph, double orientation, Sound guardYellToFind) {
        double maxDistance = Controller.addNoise(50*guardYellToFind.loudness(), 8);
        double minDistance = Controller.addNoise(((float)50/2)*guardYellToFind.loudness(), 8);
        Vector2D startingPosition = VisionController.calculatePoint(graph.getCurrentPosition().COORDINATES, maxDistance, guardYellToFind.angle());
        Vector2D possiblePosition = getPossibleOriginGuardYell(graph, startingPosition, maxDistance, minDistance, guardYellToFind);
        if (possiblePosition != null) {
            while (true) {
                LinkedList<Vector2D> path = AStar.calculate(graph, graph.getCurrentPosition(), graph.getNode(startingPosition));
                if (path.size() >= minDistance && path.size() <= maxDistance) {
                    TaskInterface pathfindingTask = tasks.getTask(TaskContainer.TaskType.PATHFINDING);
                    pathfindingTask.setTarget(graph, orientation, path);
                    return pathfindingTask;

                } else {
                    possiblePosition = getPossibleOriginGuardYell(graph, possiblePosition, maxDistance, minDistance, guardYellToFind);
                    if (possiblePosition == null) break;
                }
            }
        }
        // No position was found in direction, so do explorationInDirection
        TaskInterface explorationInDirection = tasks.getTask(TaskContainer.TaskType.EXPLORATION_DIRECTION);
        Vector2D potentialGoal = VisionController.calculatePoint(new Vector2D(0, 0), (maxDistance+minDistance)/2, guardYellToFind.angle());
        explorationInDirection.setTarget(potentialGoal);
        return explorationInDirection;
    }

    private Vector2D getPossibleOriginGuardYell(ExplorationGraph graph, Vector2D startingPosition, double maxDistance, double minDistance, Sound guardYellToFind) {
        double currentDistance = maxDistance;
        Vector2D currentPosition = startingPosition;
        while (!graph.isVisited(currentPosition)) {
            if (currentDistance < minDistance) {
                return null;
            }
            currentDistance--;
            currentPosition = VisionController.calculatePoint(graph.getCurrentPosition().COORDINATES, currentDistance, guardYellToFind.angle());
        }
        return currentPosition;
    }

    private Sound getGuardYell(List<Sound> guardYells) {
        Sound loudestYell = null;
        for (Sound sound : guardYells) {
            if (loudestYell == null || sound.loudness() > loudestYell.loudness()) {
                loudestYell = sound;
            }
        }
        return loudestYell;
    }

    private Sound getUnmatchedSound(List<Sound> sounds, VisionMemory[] guardsSeen) {
        if (!sounds.isEmpty()) {
            // There are sounds, so we should check if we need to act on this
            List<Double> guardsSeenAngles = anglesOfGuardsSeen(guardsSeen);
            Sound closestUnmatchedSound = null;
            for (Sound sound : sounds) {
                boolean matchedSound = false;
                for (Double guardAngle : guardsSeenAngles) {
                    if (Math.abs(sound.angle() - guardAngle) <= angleThreshold) {
                        matchedSound = true;
                        break;
                    }
                }
                if (!matchedSound) {
                    if (closestUnmatchedSound == null || sound.loudness() < closestUnmatchedSound.loudness()) {
                        closestUnmatchedSound = sound;
                    }
                }
            }
            return closestUnmatchedSound;
        }
        return null;
    }

    private VisionMemory getIntruderInVision(VisionMemory[] intrudersSeen) {
        VisionMemory intruderToPursuit = null;
        // Check if there's an intruder in vision, if so take the one that's the closest to pursuit
        for (VisionMemory visionMemory : intrudersSeen) {
            if (visionMemory != null) {
                if (visionMemory.secondsAgo() == 0) {
                    // Can take the magnitude of the position, while the position is always relative to the current position
                    if (intruderToPursuit == null || visionMemory.position().magnitude() < intruderToPursuit.position().magnitude()) {
                        intruderToPursuit = visionMemory;
                    }
                }
            }
        }

        return intruderToPursuit;
    }

    private List<Double> anglesOfGuardsSeen(VisionMemory[] guardsSeen) {
        ArrayList<Double> angles = new ArrayList<>();
        for (VisionMemory visionMemory : guardsSeen) {
            if (visionMemory != null && visionMemory.secondsAgo() <= secondsAgoThreshold) {
                angles.add(visionMemory.position().angle());
            }
        }
        return angles;
    }
}
