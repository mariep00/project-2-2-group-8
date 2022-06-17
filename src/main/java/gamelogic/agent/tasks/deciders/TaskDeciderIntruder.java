package gamelogic.agent.tasks.deciders;

import datastructures.Vector2D;
import gamelogic.agent.tasks.TaskContainer;
import gamelogic.agent.tasks.TaskContainer.TaskType;
import gamelogic.agent.tasks.TaskInterface;
import gamelogic.controller.VisionController;
import gamelogic.datacarriers.Sound;
import gamelogic.datacarriers.VisionMemory;
import gamelogic.maps.Tile;
import gamelogic.maps.graph.ExplorationGraph;
import gamelogic.maps.graph.Node;
import util.MathHelpers;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class TaskDeciderIntruder implements TaskDeciderInterface{

    private final TaskContainer tasks;
    private double angleSpawnToGoal;
    private final double soundThreshold = 0.3;
    private final double secondsAgoThreshold = 2.0;
    private final double angleThreshold = 40.0;

    private double currentAnticipatedDistance;
    private double lastEvasionAngle; // -1 if last task was not evasion

    public TaskDeciderIntruder(TaskContainer taskContainer) {
        this.tasks = taskContainer;
        currentAnticipatedDistance = 10.0;
    }

    @Override
    public TaskInterface getTaskToPerform(ExplorationGraph graph, List<Sound> sounds, VisionMemory[] guardsSeen, VisionMemory[] intrudersSeen, TaskInterface currentTask) {
        // First check if there is a guard in vision
        VisionMemory closestGuard = isGuardInVision(guardsSeen);
        // if there is a guard in vision and the priority of the current task is less than the one of this task, then we should switch to the evasion task
        if (closestGuard != null && (currentTask == null || currentTask.getPriority()<TaskContainer.TaskType.INTRUDER_EVASION.priority || currentTask.isFinished())) {
            TaskInterface evasionTask = tasks.getTask(TaskType.INTRUDER_EVASION);
            // set the target to the angle of the guard which is in vision
            evasionTask.setTarget(closestGuard.position().angle(), closestGuard);
            return evasionTask;
        }

        // check if there is an unknown sound close by
        Sound soundToAvoid = checkForSound(sounds, intrudersSeen);
        // if there is a relevant sound and the priority of the current task is less than the one of this task, then we should switch to this task
        if (soundToAvoid != null && (currentTask == null || currentTask.getPriority()<TaskContainer.TaskType.INTRUDER_EVASION.priority || currentTask.isFinished())) {
            TaskInterface evasionTask = tasks.getTask(TaskType.INTRUDER_EVASION);
            evasionTask.setTarget(soundToAvoid.angle(), soundToAvoid);
            return evasionTask;
        }

        // check if there is a friendly agent close by
        VisionMemory agentInFront = checkAgentInFront(intrudersSeen);
        // if there is an agent in front and the priority of the current task is less than the one of this task, then we should switch to this task
        if (currentTask != null && agentInFront != null && (currentTask.getPriority()<TaskContainer.TaskType.AVOID_COLLISION.priority || currentTask.isFinished())) {
            return tasks.getTask(TaskType.AVOID_COLLISION);
        }
        
        // set the lastEvasionAngle to -1 because the last task was not evasion
        if (currentTask != null && currentTask.getType()!=TaskType.INTRUDER_EVASION) lastEvasionAngle = -1.0;

        if (currentTask != null && (currentTask.getPriority() < TaskType.CAPTURE_TARGET_AREA.priority || currentTask.isFinished())){
            if (graph.getCurrentPosition().getTile().getType() == Tile.Type.TARGET_AREA){
                return  tasks.getTask(TaskType.CAPTURE_TARGET_AREA);
            }
        }
        
        // if there is nothing more important than exploring or going to the goal
        if(currentTask == null || (currentTask.getPriority()<=TaskContainer.TaskType.PATHFINDING.priority || currentTask.isFinished())) {
            // check if the target area has already been discovered by this agent
            LinkedList<Node> targetArea = graph.getTargetArea();
            // if the target area has been discovered, then perform pathfinding to it
            if (targetArea != null) {

                Vector2D goal = targetArea.get(0).COORDINATES;
                TaskInterface pathfindingTask = tasks.getTask(TaskType.PATHFINDING);
                pathfindingTask.setTarget(goal);
                return pathfindingTask;
            } else { //if the target area is unkown try to find it by exploring in that direction
                if (currentTask == null || currentTask.getType() != TaskType.EXPLORATION_DIRECTION || currentTask.isFinished()) {
                    Vector2D anticipatedGoal = getAnticipatedGoal(graph);
                    TaskInterface explorationTask = tasks.getTask(TaskType.EXPLORATION_DIRECTION);
                    explorationTask.setTarget(anticipatedGoal);
                    return explorationTask;
                }
            }
        }
        return currentTask;
    }

    private VisionMemory checkAgentInFront(VisionMemory[] intrudersSeen) {
        for(VisionMemory memory : intrudersSeen) {
            if (memory != null) {
                // check if there is an intruder currently visible and directly in front of own position
                if (memory.secondsAgo() == 0 && memory.position().magnitude() == 1.0) {
                    return memory;
                }
            }
        }
        return null;
    }

    private VisionMemory isGuardInVision(VisionMemory[] guardsSeen) {
        VisionMemory closestGuard = null;
        // Check if there's a guard currently in vision
        for (VisionMemory memory : guardsSeen) {
            if (memory != null) {
                if (memory.secondsAgo() == 0) {
                    // Keep track of the closest guard
                    if (closestGuard == null || memory.position().magnitude() < closestGuard.position().magnitude()) {
                        closestGuard = memory;
                    }
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
            // if the sound is above a certain threshold it should be considered
            if (sound.loudness() > soundThreshold) {
                //check if there was an intruder in that direction, if so then the sound should not be considered
                if (!isSoundMatched(sound, anglesIntruders)) {
                    if (soundToAvoid == null) soundToAvoid = sound;
                    if (sound.loudness() > soundToAvoid.loudness()) soundToAvoid = sound;
                }
            }
        }
        return soundToAvoid;
    }

    private boolean isSoundMatched(Sound sound, List<Double> anglesIntruders) {
        for (Double angle : anglesIntruders) {
            double diffAngle = MathHelpers.differenceBetweenAngles(sound.angle(), angle);
            if (diffAngle <= angleThreshold) {
                return true;
            }
        }
        return false;
    }

    private Vector2D getAnticipatedGoal(ExplorationGraph graph) {
        Vector2D potentialGoal = VisionController.calculatePoint(new Vector2D(0, 0), currentAnticipatedDistance, angleSpawnToGoal);
        
        Vector2D[] potentialArea = potentialGoal.getArea();
        int counter = 0;
        for(Vector2D vector : potentialArea) {
            Vector2D[] area = vector.getArea();
            for (Vector2D vectorA : area) {
                if(graph.isVisited(vectorA)) {
                    counter++;
                }
            }
        }
        if (counter>=1) currentAnticipatedDistance = currentAnticipatedDistance + 10;
        
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
            double difference = Math.abs(lastEvasionAngle - newAngle);
            difference = (difference > 180 ? 360 - difference : difference);
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

    public void setTargetAngle(double angle) {
        this.angleSpawnToGoal = angle;
    }

    @Override
    public TaskDeciderInterface newInstance() { return new TaskDeciderIntruder(tasks); }
    
}
