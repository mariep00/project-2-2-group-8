package gamelogic.agent.tasks;

public class TaskContainer {

    // Index 0 --> exploration task
    private final TaskInterface[] tasks;

    public TaskContainer(TaskInterface explorationTask) {
        tasks = new TaskInterface[1];
        tasks[0] = explorationTask;
    }

    public TaskInterface getTask(TaskType type) {
        if (type == TaskType.EXPLORATION) return tasks[0].newInstance();

        return null;
    }

    /*
    private ArrayList<TaskInterface> tasks;
    private int currentTask;
    
    public TaskContainer () {
        tasks = new ArrayList<>();
    }

    public TaskInterface getCurrentTask() {
        return tasks.get(currentTask);
    }

    public void switchToTask(TaskType type) {
        for (int i=0; i<tasks.size(); i++) {
            if (tasks.get(i).getType().equals(type)) {
                currentTask = i;
            }
        }
    }

    public void addTask(TaskInterface task) {
        tasks.add(task);
    }
     */

    //TODO: Task types can be changed just wrote some tasks that I could think of
    public enum TaskType {
        EXPLORATION,
        PURSUIT,
        ESCAPE,
        COVER,
        STANDBY;
    }
}
