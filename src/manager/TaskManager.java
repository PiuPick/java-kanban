package manager;

import task.*;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private int counter = 0;
    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();
    private HashMap<Integer, Subtask> subtasks = new HashMap<>();

    public ArrayList<Subtask> getSubtasks(int idEpic) {
        ArrayList<Subtask> subtasksEpic = new ArrayList<>();

        for (Integer idSubtask : epics.get(idEpic).getSubtasks()) {
            subtasksEpic.add(subtasks.get(idSubtask));
        }

        return subtasksEpic;
    }

    public void updateTask(Task task) {
        if (task.getType() == TaskType.SUBTASK) {
            subtasks.put(task.getId(), (Subtask) task);
            calculateStatus(subtasks.get(task.getId()).getEpic());
        } else if (task.getType() == TaskType.EPIC) {
            epics.put(task.getId(), (Epic) task);
        } else {
            tasks.put(task.getId(), task);
        }
    }

    private void calculateStatus(Epic epic) {
        ArrayList<Integer> subtasks = epic.getSubtasks();

        if (subtasks.isEmpty()) {
            epic.setStatus(Status.NEW);
            return;
        }

        boolean flag = true;

        for (Integer subtask : subtasks) {
            if (this.subtasks.get(subtask).getStatus() != Status.NEW) {
                flag = false;
                break;
            }
        }
        if (flag) {
            epic.setStatus(Status.NEW);
            return;
        }

        for (Integer subtask : subtasks) {
            if (this.subtasks.get(subtask).getStatus() != Status.DONE) {
                epic.setStatus(Status.IN_PROGRESS);
                return;
            }
        }
        epic.setStatus(Status.DONE);
    }

    public void createTask(Task task) {
        task.setId(getNewId());
        tasks.put(task.getId(), task);
    }

    public void createEpic(Epic epic) {
        epic.setId(getNewId());
        epics.put(epic.getId(), epic);
    }

    public void createSubtask(Subtask subtask) {
        subtask.setId(getNewId());
        subtasks.put(subtask.getId(), subtask);

        subtask.getEpic().addSubtaskId(subtask.getId());
        calculateStatus(subtask.getEpic());
    }

    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    public ArrayList<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    public void clearTasks() {
        tasks.clear();
    }

    public void clearEpics() {
        epics.clear();
        subtasks.clear();
    }

    public void clearSubtasks() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.getSubtasks().clear();
            calculateStatus(epic);
        }
    }

    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    public Epic getEpicById(int id) {
        return epics.get(id);
    }

    public Subtask getSubtaskById(int id) {
        return subtasks.get(id);
    }

    public void deleteTaskById(int id) {
        tasks.remove(id);
    }

    public void deleteEpicById(int id) {
        ArrayList<Integer> subtasksEpic = epics.remove(id).getSubtasks();

        for (Integer subtask : subtasksEpic) {
            subtasks.remove(subtask);
        }

        subtasksEpic.clear();
    }

    public void deleteSubtaskById(int id) {
        Epic epic = subtasks.get(id).getEpic();
        for (Integer idSubtask : epic.getSubtasks()) {
            if (idSubtask == id) {
                epic.getSubtasks().remove(idSubtask);
                break;
            }
        }

        epic.getSubtasks().clear();

        subtasks.remove(id);
        calculateStatus(epic);
    }

    private int getNewId() {
        return ++counter;
    }
}
