package gamelogic.agent.tasks.deciders;

import gamelogic.agent.tasks.TaskContainer;
import gamelogic.agent.tasks.TaskInterface;
import gamelogic.agent.tasks.guard.FindGuardYellSource;
import gamelogic.agent.tasks.guard.FindSoundSource;
import gamelogic.datacarriers.Sound;
import gamelogic.datacarriers.VisionMemory;
import gamelogic.maps.graph.ExplorationGraph;

import java.util.ArrayList;
import java.util.List;

public class TaskDeciderGuard implements TaskDeciderInterface {
    private final TaskContainer tasks;

    private final double secondsAgoThreshold = 2.5;
    private final double angleThreshold = 50;
    private final double soundAndMarkerThreshold = 50;

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
            return getPursuingTask(guardsSeen, intruderToPursuit);
        }

        // check if there is a friendly agent in front
        VisionMemory agentInFront = checkAgentInFront(guardsSeen);
        // if there is an agent in front and the priority of the current task is less than the one of this task, then we should switch to this task
        if (agentInFront != null && (currentTask.getPriority() < TaskContainer.TaskType.AVOID_COLLISION.priority || currentTask.isFinished())) {
            return tasks.getTask(TaskContainer.TaskType.AVOID_COLLISION);
        }

        // 2. Check if there is a guard yell
        Sound guardYell = getGuardYell(guardYells);
        if (guardYell != null && (currentTask.getPriority() <= TaskContainer.TaskType.FIND_GUARD_YELL_SOURCE.priority || currentTask.isFinished())) {
            // There was a guard yell, try to find it
            TaskInterface findGuardYellSource = new FindGuardYellSource();
            findGuardYellSource.setTarget(guardYell);
           return findGuardYellSource;
        }

        // 3. Check if there is an unmatched sound (i.e. is there a sound we cannot link to a guard we've previously seen)
        Sound closestUnmatchedSound = getUnmatchedSound(sounds, guardsSeen, pheromoneMarkerDirection);
        if (closestUnmatchedSound != null && (currentTask.getPriority() < TaskContainer.TaskType.FIND_SOUND_SOURCE.priority || currentTask.isFinished())) {
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
            else if (currentTask.getType() == TaskContainer.TaskType.VISIT_LAST_SEEN_INTRUDER_POSITIONS) {
                // TODO Create some follow up task
            }
            else {
                return tasks.getTask(TaskContainer.TaskType.VISIT_LAST_SEEN_INTRUDER_POSITIONS);
            }
        }

        return currentTask;
    }

    @Override
    public TaskDeciderInterface newInstance() {
        return new TaskDeciderGuard(tasks);
    }

    private TaskInterface getPursuingTask(VisionMemory[] guardsSeen, VisionMemory intruder) {
        /*double closestGuardDistance = Integer.MAX_VALUE;
        VisionMemory closestGuard = null;
        for (VisionMemory visionMemory : guardsSeen) {
            if (visionMemory != null && visionMemory.secondsAgo() == 0) {
                if (visionMemory.position().dist(intruder.position()) < closestGuardDistance) {
                    closestGuardDistance = visionMemory.position().dist(intruder.position());
                    closestGuard = visionMemory;
                }
            }
        }
        if (closestGuard != null) {
            if (closestGuardDistance < intruder.position().magnitude()) {
                TaskInterface taskToRetrun = tasks.getTask(TaskContainer.TaskType.GUARD_PURSUIT_FAR);
                taskToRetrun.setTarget(intruder, closestGuard);
                return taskToRetrun;
            }
        }
            */
        TaskInterface taskToReturn = tasks.getTask(TaskContainer.TaskType.GUARD_PURSUIT_CLOSE);
        taskToReturn.setTarget(intruder);
        return taskToReturn;
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

    private VisionMemory checkAgentInFront(VisionMemory[] guardsSeen) {
        for(VisionMemory memory : guardsSeen) {
            if (memory != null) {
                // check if there is a guard currently visible and directly in front of own position
                if (memory.secondsAgo() == 0 && memory.position().magnitude() == 1.0) {
                    return memory;
                }
            }
        }
        return null;
    }

    private Sound getUnmatchedSound(List<Sound> sounds, VisionMemory[] guardsSeen, double pheromoneMarkerDirection) {
        if (!sounds.isEmpty()) {
            // There are sounds, so we should check if we need to act on this
            List<Double> guardsSeenAngles = anglesOfGuardsSeen(guardsSeen);
            Sound closestUnmatchedSound = null;
            for (Sound sound : sounds) {
                boolean matchedSound = false;
                for (Double guardAngle : guardsSeenAngles) {
                    double diff = Math.abs(guardAngle - sound.angle());
                    if ((diff > 180 ? 360 - diff : diff) <= angleThreshold) {
                        matchedSound = true;
                        break;
                    }
                }
                if (!matchedSound) {
                    double diff = Math.abs(sound.angle()-pheromoneMarkerDirection);
                    if ((diff > 180 ? 360 - diff : diff) >= soundAndMarkerThreshold && (closestUnmatchedSound == null || sound.loudness() < closestUnmatchedSound.loudness())) {
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
