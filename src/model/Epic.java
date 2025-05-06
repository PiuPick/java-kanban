package model;

import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Subtask> subtasks = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description);
    }

    public void setSubtask(ArrayList<Subtask> childTasks) {
        this.subtasks = childTasks;
    }

    public void setSubtask(Subtask subtask) {
        subtasks.add(subtask);
    }

    public ArrayList<Subtask> getSubtasks() {
        final ArrayList<Subtask> clone = (ArrayList<Subtask>) subtasks.clone();
        return clone;
    }
}
