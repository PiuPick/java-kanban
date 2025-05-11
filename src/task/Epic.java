package task;

import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Integer> subtasks = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description);
    }

    public void addSubtaskId(int id) {
        subtasks.add(id);
    }

    public ArrayList<Integer> getSubtasks() {
        return subtasks;
    }

    @Override
    public Epic cloneTask() {
        Epic clone = new Epic(this.getName(), this.getDescription());
        clone.setId(this.getId());
        clone.setStatus(this.getStatus());

        for (Integer subtask : subtasks) {
            clone.addSubtaskId(subtask);
        }

        return clone;
    }

    @Override
    public TaskType getType() {
        return TaskType.EPIC;
    }
}
