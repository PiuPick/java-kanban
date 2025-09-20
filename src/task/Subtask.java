package task;

public class Subtask extends Task {
    private final Epic epic;

    public Subtask(Epic epic, String name, String description) {
        super(name, description);
        this.epic = epic;
    }

    public Subtask(Epic epic, String name, String description, Status status) {
        super(name, description, status);
        this.epic = epic;
    }

    public Epic getEpic() {
        return epic.cloneTask();
    }

    @Override
    public Subtask cloneTask() {
        Subtask clone = new Subtask(this.getEpic().cloneTask(), this.getName(), this.getDescription(), this.getStatus());
        clone.setId(this.getId());
        clone.setStartTime(this.getStartTime());
        clone.setDuration(this.getDuration());

        return clone;
    }

    @Override
    public TaskType getType() {
        return TaskType.SUBTASK;
    }
}
