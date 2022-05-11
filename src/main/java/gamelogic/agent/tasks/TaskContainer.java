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
    // Index 5 --> guard searches for guard yell source task
    private final TaskInterface[] tasks;
    private final TaskDeciderInterface taskDeciderGuard;
    private final TaskDeciderInterface taskDeciderIntruder;

    public TaskContainer(TaskInterface explorationTask, TaskInterface findSoundSourceTask, TaskInterface guardPursuitTask,
                         TaskInterface intruderEvasionTask, TaskInterface guardVisitLastSeenIntruderPositions,
                         TaskInterface findGuardYellSourceTask) {
        tasks = new TaskInterface[6];
        tasks[0] = explorationTask;
        tasks[1] = findSoundSourceTask;
        tasks[2] = guardPursuitTask;
        tasks[3] = intruderEvasionTask;
        tasks[4] = guardVisitLastSeenIntruderPositions;
        tasks[5] = findGuardYellSourceTask;

        taskDeciderGuard = new TaskDeciderGuard(this);
        // TODO: Pass the right angle upon creation
        taskDeciderIntruder = new TaskDeciderIntruder(this, 0.0);
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
        else if (type == TaskType.GUARD_PURSUIT) return tasks[2].newInstance();
        else if (type == TaskType.INTRUDER_EVASION) return tasks[3].newInstance();
        else if (type == TaskType.VISIT_LAST_SEEN_INTRUDER_POSITIONS) return tasks[4].newInstance();
        else if (type == TaskType.FIND_GUARD_YELL_SOURCE) return tasks[5].newInstance();
        else return null;
    }

    public TaskDeciderInterface getTaskDeciderGuard() { return taskDeciderGuard; }
    public TaskDeciderInterface getTaskDeciderIntruder() { return taskDeciderIntruder; }

    public enum TaskType {
        EXPLORATION(1),
        EXPLORATION_DIRECTION(1),
        GUARD_PURSUIT(5),
        INTRUDER_EVASION(4),
        PATHFINDING(2),
        AVOID_COLLISION(5),
        COVER(-1),
        STANDBY(-1),
        FIND_SOUND_SOURCE(3),
        VISIT_LAST_SEEN_INTRUDER_POSITIONS(1),
        FIND_GUARD_YELL_SOURCE(4);

        public final int priority;

        TaskType(int priority) {
            this.priority = priority;
        }
    }
}
