package gamelogic.agent.tasks;

import gamelogic.agent.tasks.deciders.TaskDeciderGuard;
import gamelogic.agent.tasks.deciders.TaskDeciderInterface;
import gamelogic.agent.tasks.deciders.TaskDeciderIntruder;

public class TaskContainer {

    // Index 0 --> exploration task
    // Index 1 --> check sound source task
    // Index 2 --> guard pursuit task
    // Index 3 --> intruder evasion task
    private final TaskInterface[] tasks;
    private final TaskDeciderInterface taskDeciderGuard;
    private final TaskDeciderInterface taskDeciderIntruder;

    public TaskContainer(TaskInterface explorationTask, TaskInterface checkSoundSourceTask, TaskInterface guardPursuitTask,
                         TaskInterface intruderEvasionTask) {
        tasks = new TaskInterface[4];
        tasks[0] = explorationTask;
        tasks[1] = checkSoundSourceTask;
        tasks[2] = guardPursuitTask;
        tasks[3] = intruderEvasionTask;

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
        else if (type == TaskType.CHECK_SOUND_SOURCE) return tasks[1].newInstance();
        else if (type == TaskType.GUARD_PURSUIT) return tasks[2].newInstance();
        else if (type == TaskType.INTRUDER_EVASION) return tasks[3].newInstance();
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
        COVER(-1),
        STANDBY(-1),
        CHECK_SOUND_SOURCE(4);

        public final int priority;

        TaskType(int priority) {
            this.priority = priority;
        }
    }
}
