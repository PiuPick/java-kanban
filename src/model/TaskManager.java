package model;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private static int id = 0;
    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();
    private HashMap<Integer, Subtask> subtasks = new HashMap<>();

    public void updateTask(Task task) {
        if (task instanceof Subtask subtask) {
            subtasks.put(subtask.getId(), subtask);
            checkStatus(subtask.getEpic());
        } else if (task instanceof Epic epic) {
            epics.put(epic.getId(), epic);
            checkStatus(epic);
        } else {
            tasks.put(task.getId(), task);
        }
    }

    private void checkStatus(Epic epic) {
        ArrayList<Subtask> subtasks = epic.getSubtasks();

        if (subtasks.isEmpty()) {
            epic.setStatus(Status.NEW);
            return;
        }

        boolean flag = true;

        for (Subtask subtask : subtasks) {
            if (subtask.getStatus() != Status.NEW) {
                flag = false;
                break;
            }
        }
        if (flag) {
            epic.setStatus(Status.NEW);
            return;
        }

        for (Subtask subtask : subtasks) {
            if (subtask.getStatus() != Status.DONE) {
                epic.setStatus(Status.IN_PROGRESS);
                return;
            }
        }
        epic.setStatus(Status.DONE);
    }

    public Task createTask(Task task) {
        tasks.put(task.getId(), task);
        return task;
    }

    public Task createTask(String name, String description) {
        Task task = new Task(name, description);
        tasks.put(task.getId(), task);
        return task;
    }

    public Epic createEpic(String name, String description) {
        Epic epic = new Epic(name, description);
        updateTask(epic);

        return epic;
    }

    public Subtask createSubtask(Epic epic, String name, String description) {
        Subtask subtask = new Subtask(name, description, epic);
        epic.setSubtask(subtask);
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
        ArrayList<Subtask> subtasksEpic = epics.get(id).getSubtasks();

        for (Subtask subtask : subtasksEpic) {
            subtasks.remove(subtask.getId());
        }

        epics.remove(id);
    }

    public void deleteSubtaskById(int id) {
        Epic epic = subtasks.get(id).getEpic();
        ArrayList<Subtask> subtasksNew = epic.getSubtasks();
        subtasksNew.remove(subtasks.get(id));
        epic.setSubtask(subtasksNew);

        subtasks.remove(id);

        checkStatus(epic);
    }

    public ArrayList<Subtask> getSubtasks(Epic task) {
        return task.getSubtasks();
    }

    public ArrayList<Subtask> getSubtasks(int id) {
        return epics.get(id).getSubtasks();
    }

    public static int getNewId() {
        return ++id;
    }
}
