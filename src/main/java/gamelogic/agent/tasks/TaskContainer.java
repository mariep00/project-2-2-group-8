package gamelogic.agent.tasks;

import gamelogic.agent.tasks.deciders.TaskDeciderGuard;
import gamelogic.agent.tasks.deciders.TaskDeciderInterface;
import gamelogic.agent.tasks.deciders.TaskDeciderIntruder;

public class TaskContainer {

    // Index 0 --> exploration task
    // Index 1 --> find sound source task
    // Index 2 --> guard pursuit task
    // Index 3 --> intruder evasion task
    // Index 4 --> guard visits last seen intruder positions task
    // Index 5 --> pathfinding task
    // Index 6 --> exploration into direction task
    private final TaskInterface[] tasks;
    private final TaskDeciderInterface taskDeciderGuard;
    private final TaskDeciderInterface taskDeciderIntruder;

    public TaskContainer(TaskInterface explorationTask, TaskInterface findSoundSourceTask, TaskInterface guardPursuitTaskClose,
                         TaskInterface guardPursuitTaskFar, TaskInterface intruderEvasionTask, TaskInterface guardVisitLastSeenIntruderPositions,
                         TaskInterface pathfindingTask, TaskInterface explorationInDirectionTask, TaskInterface avoidCollisionTask, TaskInterface captureTargetArea) {
        tasks = new TaskInterface[10];
        tasks[0] = explorationTask;
        tasks[1] = findSoundSourceTask;
        tasks[2] = guardPursuitTaskClose;
        tasks[3] = guardPursuitTaskFar;
        tasks[4] = intruderEvasionTask;
        tasks[5] = guardVisitLastSeenIntruderPositions;
        tasks[6] = pathfindingTask;
        tasks[7] = explorationInDirectionTask;
        tasks[8] = avoidCollisionTask;
        tasks[9] = captureTargetArea;

        taskDeciderGuard = new TaskDeciderGuard(this);
        taskDeciderIntruder = new TaskDeciderIntruder(this);
    }

    public TaskContainer(TaskInterface explorationTask) {
        tasks = new TaskInterface[1];
        tasks[0] = explorationTask;

        taskDeciderGuard = null;
        taskDeciderIntruder = null;
    }

    public TaskInterface getTask(TaskType type) {
        if (type == TaskType.EXPLORATION) return tasks[0].newInstance();
        else if (type == TaskType.FIND_SOUND_SOURCE) return tasks[1].newInstance();
        else if (type == TaskType.GUARD_PURSUIT_CLOSE) return tasks[2].newInstance();
        else if (type == TaskType.GUARD_PURSUIT_FAR) return tasks[3].newInstance();
        else if (type == TaskType.INTRUDER_EVASION) return tasks[4].newInstance();
        else if (type == TaskType.VISIT_LAST_SEEN_INTRUDER_POSITIONS) return tasks[5].newInstance();
        else if (type == TaskType.PATHFINDING) return tasks[6].newInstance();
        else if (type == TaskType.EXPLORATION_DIRECTION) return tasks[7].newInstance();
        else if (type == TaskType.AVOID_COLLISION) return tasks[8].newInstance();
        else if (type == TaskType.CAPTURE_TARGET_AREA) return tasks[9].newInstance();
        else return null;
    }

    public TaskDeciderInterface getTaskDeciderGuard() { return taskDeciderGuard.newInstance(); }
    public TaskDeciderInterface getTaskDeciderIntruder() { return taskDeciderIntruder.newInstance(); }

    public enum TaskType {
        EXPLORATION(1),
        EXPLORATION_DIRECTION(1),
        GUARD_PURSUIT_CLOSE(5),
        GUARD_PURSUIT_FAR(5),
        INTRUDER_EVASION(4),
        PATHFINDING(2),
        AVOID_COLLISION(5),
        FIND_SOUND_SOURCE(3),
        VISIT_LAST_SEEN_INTRUDER_POSITIONS(1),
        FIND_GUARD_YELL_SOURCE(4),

        CAPTURE_TARGET_AREA(3);

        public final int priority;

        TaskType(int priority) {
            this.priority = priority;
        }
    }
}
