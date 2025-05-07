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
        return epic;
    }

    @Override
    public TaskType getType() {
        return TaskType.SUBTASK;
    }
}
