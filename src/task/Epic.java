package task;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private List<Integer> subtasks = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description);
    }

    public void addSubtaskId(int id) {
        subtasks.add(id);
    }

    public List<Integer> getSubtasks() {
        return subtasks;
    }

    @Override
    public Epic cloneTask() {
        Epic clone = new Epic(this.getName(), this.getDescription());
        clone.setId(this.getId());
        clone.setStatus(this.getStatus());
        clone.setStartTime(this.getStartTime());
        clone.setDuration(this.getDuration());

        for (Integer subtask : this.subtasks) {
            clone.addSubtaskId(subtask);
        }

        return clone;
    }

    @Override
    public TaskType getType() {
        return TaskType.EPIC;
    }
}
