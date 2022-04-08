package gamelogic.agent.tasks;

import java.util.ArrayList;

public class TaskContainer {

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

    //TODO: Task types can be changed just wrote some tasks that I could think of
    public enum TaskType {
        EXPLORATION,
        PURSUIT,
        ESCAPE,
        COVER,
        STANDBY;
    }
}
