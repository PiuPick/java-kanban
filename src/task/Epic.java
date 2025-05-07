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

    public void setSubtask(Integer subtask) {
        subtasks.add(subtask);
    }

    public ArrayList<Integer> getSubtasks() {
        final ArrayList<Integer> clone = (ArrayList<Integer>) subtasks.clone();
        return clone;
    }
}
