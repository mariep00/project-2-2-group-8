package gamelogic.agent.tasks.deciders;

import datastructures.Vector2D;
import gamelogic.agent.tasks.TaskContainer;
import gamelogic.agent.tasks.TaskContainer.TaskType;
import gamelogic.agent.tasks.TaskInterface;
import gamelogic.controller.VisionController;
import gamelogic.datacarriers.Sound;
import gamelogic.datacarriers.VisionMemory;
import gamelogic.maps.graph.ExplorationGraph;
import gamelogic.maps.graph.Node;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

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
    public TaskInterface getTaskToPerform(ExplorationGraph graph, List<Sound> sounds, VisionMemory[] guardsSeen, VisionMemory[] intrudersSeen, TaskInterface currentTask) {
        // First check if there is a guard in vision
        VisionMemory closestGuard = isGuardInVision(guardsSeen);
        // if there is a guard in vision and the priority of the current task is less than the one of this task, then we should switch to the evasion task
        if (closestGuard != null && (currentTask.getPriority()<=TaskContainer.TaskType.INTRUDER_EVASION.priority || currentTask.isFinished())) {
            TaskInterface evasionTask = tasks.getTask(TaskType.INTRUDER_EVASION);
            // set the target to the angle of the guard which is in vision
            evasionTask.setTarget(getTargetAngle(360.0 - Math.atan2(closestGuard.position().y, closestGuard.position().x)));
            return evasionTask;
        }

        // check if there is an unknown sound close by
        Sound soundToAvoid = checkForSound(sounds, intrudersSeen);
        // if there is a relevant sound and the priority of the current task is less than the one of this task, then we should switch to this task
        if (soundToAvoid != null && (currentTask.getPriority()<=TaskContainer.TaskType.INTRUDER_EVASION.priority || currentTask.isFinished())) {
            TaskInterface evasionTask = tasks.getTask(TaskType.INTRUDER_EVASION);
            evasionTask.setTarget(getTargetAngle(soundToAvoid.angle()));
            return evasionTask;
        }

        // check if there is a friendly agent close by
        VisionMemory agentInFront = checkAgentInFront(intrudersSeen);
        // if there is an agent in front and the priority of the current task is less than the one of this task, then we should switch to this task
        if (agentInFront != null && (currentTask.getPriority()<=TaskContainer.TaskType.AVOID_COLLISION.priority || currentTask.isFinished())) {
            TaskInterface avoidCollTask = tasks.getTask(TaskType.AVOID_COLLISION);
            return avoidCollTask;
        }
        
        // set the lastEvasionAngle to -1 because the last task was not evasion
        if (currentTask.getType()!=TaskType.INTRUDER_EVASION) lastEvasionAngle = -1.0;
        
        // if there is nothing more important than exploring or going to the goal
        if(currentTask.getPriority()<=TaskContainer.TaskType.PATHFINDING.priority || currentTask.isFinished()) {
            // check if the target area has already been discovered by this agent
            LinkedList<Node> targetArea = graph.getTargetArea();
            // if the target area has been discovered, then perform pathfinding to it
            if (targetArea != null) {
                Vector2D goal = targetArea.get(0).COORDINATES;
                TaskInterface pathfindingTask = tasks.getTask(TaskType.PATHFINDING);
                pathfindingTask.setTarget(goal);
                return pathfindingTask;
            } else { //if the target area is unkown try to find it by exploring in that direction
                Vector2D anticipatedGoal = getAnticipatedGoal(graph);
                TaskInterface explorationTask = tasks.getTask(TaskType.EXPLORATION_DIRECTION);
                explorationTask.setTarget(anticipatedGoal);
                return explorationTask;
            }
        }
        return currentTask;
    }

    private VisionMemory checkAgentInFront(VisionMemory[] intrudersSeen) {
        for(VisionMemory memory : intrudersSeen) {
            // check if there is an intruder currently visible and directly in front of own position
            if (memory.secondsAgo() == 0 && memory.position().magnitude()==1.0) {
                return memory;
            }
        }
        return null;
    }

    private VisionMemory isGuardInVision(VisionMemory[] guardsSeen) {
        VisionMemory closestGuard = null;
        // Check if there's a guard currently in vision
        for (VisionMemory memory : guardsSeen) {
            if (memory.secondsAgo() == 0) {
                // Keep track of the closest guard
                if (closestGuard == null || memory.position().magnitude() < closestGuard.position().magnitude()) {
                    closestGuard = memory;
                }
            }
        }
        return closestGuard;
    }

    private Sound checkForSound (List<Sound> sounds, VisionMemory[] intrudersSeen) {
        // if there are no sounds return null
        if (sounds.isEmpty()) return null;
        Sound soundToAvoid = null;
        // calculate the angles of the intruders that were last seen a certain time ago, relative to the current position
        List<Double> anglesIntruders = anglesOfIntrudersSeen(intrudersSeen);
        for (Sound sound: sounds) {
            // TODO: match the threshold to a reasonable number
            // if the sound is above a certain threshold it should be considered
            if (sound.loudness() > soundThreshold) {
                //check if there was an intruder in that direction, if so then the sound should not be considered
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
                lastEvasionAngle = newAngle-(difference/2.0);
                return lastEvasionAngle;
            } else {
                lastEvasionAngle = lastEvasionAngle-(difference/2.0);
                return lastEvasionAngle;
            }
        }
        lastEvasionAngle = newAngle;
        return lastEvasionAngle;
    }
    
}
