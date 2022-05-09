package gamelogic.agent.tasks;

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
    public TaskInterface getTaskToPerform(ExplorationGraph graph, double pheromoneMarkerDirection, List<Sound> sounds, VisionMemory[] guardsSeen, VisionMemory[] intrudersSeen) {
        // TODO First check if intruder is in sight

        // There are sounds, so we should check if we need to act on this
        // TODO Need some input on what the current task is, while if for example guard is already searching for sound source, it should only switch if a different unmatched sound is closer (maybe also take time ago into account)
        if (!sounds.isEmpty()) {
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
            // There is a sound we cannot match with another guard
            // This means we should check if it's a guard or an intruder
            if (closestUnmatchedSound != null) {
                CheckSoundSource checkSoundSource = (CheckSoundSource) tasks.getTask(TaskContainer.TaskType.CHECK_SOUND_SOURCE);
                checkSoundSource.setSoundToCheck(closestUnmatchedSound);
                return checkSoundSource;
            }
        }
        // There is no sound, or no unmatched sound, nor can the guard see an intruder; so do something else

        // TODO For now return exploration, need to create a task that "searches" for an intruder when agent explored whole map i.e. no frontiers left
        return tasks.getTask(TaskContainer.TaskType.EXPLORATION);
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
