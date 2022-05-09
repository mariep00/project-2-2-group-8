package gamelogic.agent.tasks;

public class TaskContainer {

    // Index 0 --> exploration task
    // Index 1 --> check sound source task
    // Index 2 --> guard pursuit task
    // Index 3 --> intruder evasion task
    private final TaskInterface[] tasks;
    private final TaskDeciderInterface taskDeciderGuard;

    public TaskContainer(TaskInterface explorationTask, TaskInterface checkSoundSourceTask, TaskInterface guardPursuitTask,
                         TaskInterface intruderEvasionTask) {
        tasks = new TaskInterface[4];
        tasks[0] = explorationTask;
        tasks[1] = checkSoundSourceTask;
        tasks[2] = guardPursuitTask;
        tasks[3] = intruderEvasionTask;

        taskDeciderGuard = new TaskDeciderGuard(this);
    }

    public TaskContainer(TaskInterface explorationTask) {
        tasks = new TaskInterface[1];
        tasks[0] = explorationTask;

        taskDeciderGuard = null;
    }

    public TaskInterface getTask(TaskType type) {
        if (type == TaskType.EXPLORATION) return tasks[0].newInstance();
        else if (type == TaskType.CHECK_SOUND_SOURCE) return tasks[1].newInstance();
        else if (type == TaskType.GUARD_PURSUIT) return tasks[2].newInstance();
        else if (type == TaskType.INTRUDER_EVASION) return tasks[3].newInstance();
        else return null;
    }

    public TaskDeciderInterface getTaskDeciderGuard() { return taskDeciderGuard; }


    //TODO: Task types can be changed just wrote some tasks that I could think of
    public enum TaskType {
        EXPLORATION,
        GUARD_PURSUIT,
        INTRUDER_EVASION,
        COVER,
        STANDBY,
        CHECK_SOUND_SOURCE
    }
}
