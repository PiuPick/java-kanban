package task;

import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Integer> subtasks = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description);
    }

    public void setSubtask(ArrayList<Integer> childTasks) {
        this.subtasks = childTasks;
    }

    public ArrayList<Integer> getSubtasks() {
        return subtasks;
    }
}
