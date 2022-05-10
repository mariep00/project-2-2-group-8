package gamelogic.agent.tasks.deciders;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import datastructures.Vector2D;
import gamelogic.agent.tasks.TaskContainer;
import gamelogic.agent.tasks.TaskInterface;
import gamelogic.agent.tasks.TaskContainer.TaskType;
import gamelogic.controller.VisionController;
import gamelogic.datacarriers.Sound;
import gamelogic.datacarriers.VisionMemory;
import gamelogic.maps.graph.ExplorationGraph;
import gamelogic.maps.graph.Node;

public class TaskDeciderIntruder implements TaskDeciderInterface{

    private final TaskContainer tasks;
    // TODO: Adjust thresholds
    private final double angleSpawnToGoal;
    private final double soundThreshold = 5.0;
    private final double secondsAgoThreshold = 7.0;
    private final double angleThreshold = 10.0;

    private double currentAnticipatedDistance;
    private double lastEvasionAngle; // -1 if last task was not evasion

    public TaskDeciderIntruder(TaskContainer taskContainer, double angleSpawnToGoal) {
        this.tasks = taskContainer;
        this.angleSpawnToGoal = angleSpawnToGoal;
        currentAnticipatedDistance = 10.0;
    }

    @Override
    public TaskInterface getTaskToPerform(ExplorationGraph graph, double pheromoneMarkerDirection, List<Sound> sounds, VisionMemory[] guardsSeen, VisionMemory[] intrudersSeen, TaskInterface currentTask) {
        
        VisionMemory closestGuard = isGuardInVision(guardsSeen);
        if (closestGuard != null && (currentTask.getPriority()<=TaskContainer.TaskType.INTRUDER_EVASION.priority || currentTask.isFinished())) {
            TaskInterface evasionTask = tasks.getTask(TaskType.INTRUDER_EVASION);
            evasionTask.setTarget(getTargetAngle(360.0 - Math.atan2(closestGuard.position().y, closestGuard.position().x)));
            return evasionTask;
        }

        Sound soundToAvoid = checkForSound(sounds, intrudersSeen);
        if (soundToAvoid != null && (currentTask.getPriority()<=TaskContainer.TaskType.INTRUDER_EVASION.priority || currentTask.isFinished())) {
            TaskInterface evasionTask = tasks.getTask(TaskType.INTRUDER_EVASION);
            evasionTask.setTarget(getTargetAngle(soundToAvoid.angle()));
            return evasionTask;
        }

        VisionMemory agentInFront = checkAgentInFront(intrudersSeen);
        if (agentInFront != null && (currentTask.getPriority()<=TaskContainer.TaskType.AVOID_COLLISION.priority || currentTask.isFinished())) {
            TaskInterface avoidCollTask = tasks.getTask(TaskType.AVOID_COLLISION);
            return avoidCollTask;
        }
        
        lastEvasionAngle = -1.0;
        
        if(currentTask.getPriority()<=TaskContainer.TaskType.PATHFINDING.priority || currentTask.isFinished()) {
            LinkedList<Node> targetArea = graph.getTargetArea();
            if (targetArea != null) {
                Vector2D goal = targetArea.get(0).COORDINATES;
                TaskInterface pathfindingTask = tasks.getTask(TaskType.PATHFINDING);
                pathfindingTask.setTarget(goal);
                return pathfindingTask;
            } else {
                Vector2D anticipatedGoal = getAnticipatedGoal(graph);
                TaskInterface explorationTask = tasks.getTask(TaskType.EXPLORATION_DIRECTION);
                explorationTask.setTarget(anticipatedGoal);
                return explorationTask;
            }
        }
        return null;
    }

    private VisionMemory checkAgentInFront(VisionMemory[] intrudersSeen) {
        for(VisionMemory memory : intrudersSeen) {
            if (memory.secondsAgo() == 0 && memory.position().magnitude()==1.0) {
                return memory;
            }
        }
        return null;
    }

    private VisionMemory isGuardInVision(VisionMemory[] guardsSeen) {
        VisionMemory closestGuard = null;
        // Check if there's an intruder in vision, if so take the one that's the closest to pursuit
        for (VisionMemory memory : guardsSeen) {
            if (memory.secondsAgo() == 0) {
                // Can take the magnitude of the position, while the position is always relative to the current position
                if (closestGuard == null || memory.position().magnitude() < closestGuard.position().magnitude()) {
                    closestGuard = memory;
                }
            }
        }
        return closestGuard;
    }

    private Sound checkForSound (List<Sound> sounds, VisionMemory[] intrudersSeen) {
        if (sounds.isEmpty()) return null;
        Sound soundToAvoid = null;
        List<Double> anglesIntruders = anglesOfIntrudersSeen(intrudersSeen);
        for (Sound sound: sounds) {
            // TODO: match the threshold to a reasonable number
            if (sound.loudness() > soundThreshold) {
                if (!isSoundMatched(sound, anglesIntruders)) {
                    if (soundToAvoid == null) soundToAvoid = sound;
                    if (soundToAvoid != null && sound.loudness() > soundToAvoid.loudness()) soundToAvoid = sound;
                }
            }
        }
        return soundToAvoid;
    }

    private boolean isSoundMatched(Sound sound, List<Double> anglesIntruders) {
        for (Double angle : anglesIntruders) {
            if (Math.abs(sound.angle() - angle) <= angleThreshold) {
                return true;
            }
        }
        return false;
    }

    private Vector2D getAnticipatedGoal(ExplorationGraph graph) {
        
        Vector2D potentialGoal = VisionController.calculatePoint(new Vector2D(0, 0), currentAnticipatedDistance, angleSpawnToGoal);
        if(graph.isVisited(potentialGoal)) currentAnticipatedDistance = currentAnticipatedDistance + 10.0;
        return potentialGoal;
    }

    private List<Double> anglesOfIntrudersSeen(VisionMemory[] intrudersSeen) {
        ArrayList<Double> angles = new ArrayList<>();
        for (VisionMemory visionMemory : intrudersSeen) {
            if (visionMemory != null && visionMemory.secondsAgo() <= secondsAgoThreshold) {
                angles.add(visionMemory.position().angle());
            }
        }
        return angles;
    }

    private double getTargetAngle(double newAngle) {
        if (lastEvasionAngle != -1.0) {
            double difference = Math.sqrt((newAngle*newAngle)-(lastEvasionAngle*lastEvasionAngle));
            if (newAngle > lastEvasionAngle) {
                return newAngle-(difference/2.0);
            } else {
                return lastEvasionAngle-(difference/2.0);
            }
        }
        return newAngle;
    }
    
}
