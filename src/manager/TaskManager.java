package manager;

import task.*;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private int counter = 0;
    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();
    private HashMap<Integer, Subtask> subtasks = new HashMap<>();

    public void updateTask(Task task) {
        if (task.getType() == TaskType.SUBTASK) {
            subtasks.put(task.getId(), (Subtask) task);
            checkStatus(subtasks.get(task.getId()).getEpic());
        } else if (task.getType() == TaskType.EPIC) {
            epics.put(task.getId(), (Epic) task);
        } else {
            tasks.put(task.getId(), task);
        }
    }

    private void checkStatus(Epic epic) {
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

    public Task createTask(Task task) {
        task.setId(++counter);
        tasks.put(counter, task);
        return task;
    }

    public Task createTask(String name, String description) {
        Task task = new Task(name, description);
        task.setId(++counter);
        tasks.put(counter, task);
        return task;
    }

    public Epic createEpic(String name, String description) {
        Epic epic = new Epic(name, description);
        epic.setId(++counter);
        updateTask(epic);

        return epic;
    }

    public Subtask createSubtask(Epic epic, String name, String description) {
        Subtask subtask = new Subtask(name, description, epic);
        subtask.setId(++counter);
        ArrayList<Integer> subtaskId = new ArrayList<>();
        subtaskId.add(counter);
        epic.setSubtask(subtaskId);
        updateTask(subtask);

        return subtask;
    }

    public HashMap<Integer, Task> getTasks() {
        final HashMap<Integer, Task> integerTaskHashMap = new HashMap<>(tasks);
        return integerTaskHashMap;
    }

    public HashMap<Integer, Epic> getEpics() {
        final HashMap<Integer, Epic> integerEpicHashMap = new HashMap<>(epics);
        return integerEpicHashMap;
    }

    public HashMap<Integer, Subtask> getSubtasks() {
        final HashMap<Integer, Subtask> integerSubtaskHashMap = new HashMap<>(subtasks);
        return integerSubtaskHashMap;
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
    }

    public Task getTaskById(int id) {
        final Task task = tasks.get(id);
        return task;
    }

    public Epic getEpicById(int id) {
        final Epic epic = epics.get(id);
        return epic;
    }

    public Subtask getSubtaskById(int id) {
        final Subtask subtask = subtasks.get(id);
        return subtask;
    }

    public void deleteTaskById(int id) {
        tasks.remove(id);
    }

    public void deleteEpicById(int id) {
        ArrayList<Integer> subtasksEpic = epics.get(id).getSubtasks();

        for (Integer subtask : subtasksEpic) {
            subtasks.remove(subtask);
        }

        epics.remove(id);
    }

    public void deleteSubtaskById(int id) {
        Epic epic = subtasks.get(id).getEpic();
        ArrayList<Integer> subtasksNew = epic.getSubtasks();
        subtasksNew.remove(subtasks.get(id));
        epic.setSubtask(subtasksNew);

        subtasks.remove(id);

        checkStatus(epic);
    }

    public ArrayList<Integer> getSubtasks(Epic task) {
        return task.getSubtasks();
    }

    public ArrayList<Integer> getSubtasks(int id) {
        return epics.get(id).getSubtasks();
    }
}
