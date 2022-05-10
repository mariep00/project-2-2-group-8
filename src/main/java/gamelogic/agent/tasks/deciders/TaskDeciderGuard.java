package gamelogic.agent.tasks.deciders;

import gamelogic.agent.tasks.CheckSoundSource;
import gamelogic.agent.tasks.TaskContainer;
import gamelogic.agent.tasks.TaskInterface;
import gamelogic.datacarriers.Sound;
import gamelogic.datacarriers.VisionMemory;
import gamelogic.maps.graph.ExplorationGraph;

import java.util.ArrayList;
import java.util.List;

public class TaskDeciderGuard implements TaskDeciderInterface {
    private final TaskContainer tasks;

    private final double secondsAgoThreshold = 1;
    private final double angleThreshold = 25;

    public TaskDeciderGuard(TaskContainer taskContainer) {
        this.tasks = taskContainer;
    }

    @Override
    public TaskInterface getTaskToPerform(ExplorationGraph graph, double pheromoneMarkerDirection, List<Sound> sounds, VisionMemory[] guardsSeen, VisionMemory[] intrudersSeen, TaskInterface currentTask) {
        // We're going to check on what task to perform based on the priority of a task
        // For example, starting the pursuit when the guard sees an intruder is the most important, thus has the highest priority,
        // thus we will start by checking this.

        // 1. Check if intruder is in vision
        VisionMemory intruderToPursuit = isIntruderInVision(intrudersSeen);
        if (intruderToPursuit != null) {
            // There is an intruder in vision
            // We should start the pursuit task
            TaskInterface pursuitTask = tasks.getTask(TaskContainer.TaskType.GUARD_PURSUIT);
            pursuitTask.setTarget(intruderToPursuit);
            return pursuitTask;
        }

        // TODO Smth that might be an issue; For example guard is already searching for sound source, it should only switch if a different unmatched sound is closer (maybe also take time ago into account)
        // Right now when there is a new unmatched sound it will always switch to this newest one

        // 2. Check if there is an unmatched sound (i.e. is there a sound we cannot link to a guard we've previously seen)
        Sound closestUnmatchedSound = isThereUnmatchedSound(sounds, guardsSeen);
        if (closestUnmatchedSound != null && currentTask.getPriority() <= TaskContainer.TaskType.CHECK_SOUND_SOURCE.priority) {
            // There is a sound we cannot match with another guard
            // This means we should check if it's a guard or an intruder
            CheckSoundSource checkSoundSource = (CheckSoundSource) tasks.getTask(TaskContainer.TaskType.CHECK_SOUND_SOURCE);
            checkSoundSource.setTarget(closestUnmatchedSound);
            return checkSoundSource;
        }
        // Nothing special to do, so just explore
        if (currentTask.getPriority() <= TaskContainer.TaskType.EXPLORATION.priority || currentTask.isFinished()) {
            if (!graph.frontiers.isEmpty()) {
                return tasks.getTask(TaskContainer.TaskType.EXPLORATION);
            }
        }
        return null; // TODO Need to create a task that "searches" for an intruder when agent explored whole map i.e. no frontiers left
    }

    private Sound isThereUnmatchedSound(List<Sound> sounds, VisionMemory[] guardsSeen) {
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

    private VisionMemory isIntruderInVision(VisionMemory[] intrudersSeen) {
        VisionMemory intruderToPursuit = null;
        // Check if there's an intruder in vision, if so take the one that's the closest to pursuit
        for (VisionMemory visionMemory : intrudersSeen) {
            if (visionMemory.secondsAgo() == 0) {
                // Can take the magnitude of the position, while the position is always relative to the current position
                if (intruderToPursuit == null || visionMemory.position().magnitude() < intruderToPursuit.position().magnitude()) {
                    intruderToPursuit = visionMemory;
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
