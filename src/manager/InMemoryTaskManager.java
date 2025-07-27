package manager;

import task.*;

import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    private int counter = 0;
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private final Map<Integer, Subtask> subtasks = new HashMap<>();
    private final HistoryManager historyManager = Managers.getDefaultHistory();

    @Override
    public List<Subtask> getSubtasks(int idEpic) {
        List<Subtask> subtasksEpic = new ArrayList<>();

        for (Integer idSubtask : epics.get(idEpic).getSubtasks()) {
            subtasksEpic.add(subtasks.get(idSubtask));
        }

        return subtasksEpic;
    }

    @Override
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
        List<Integer> subtasks = epic.getSubtasks();

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

    @Override
    public void createTask(Task task) {
        task.setId(getNewId());
        tasks.put(task.getId(), task);
    }

    @Override
    public void createEpic(Epic epic) {
        epic.setId(getNewId());
        epics.put(epic.getId(), epic);
    }

    @Override
    public void createSubtask(Subtask subtask) {
        subtask.setId(getNewId());
        subtasks.put(subtask.getId(), subtask);

        subtask.getEpic().addSubtaskId(subtask.getId());
        calculateStatus(subtask.getEpic());
    }

    @Override
    public List<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public void clearTasks() {
        tasks.clear();
    }

    @Override
    public void clearEpics() {
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void clearSubtasks() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.getSubtasks().clear();
            calculateStatus(epic);
        }
    }

    @Override
    public Task getTaskById(int id) {
        Task task = tasks.get(id);
        historyManager.add(task);
        return task;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = epics.get(id);
        historyManager.add(epic);
        return epic;
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        historyManager.add(subtask);
        return subtask;
    }

    @Override
    public void deleteTaskById(int id) {
        tasks.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void deleteEpicById(int id) {
        List<Integer> subtasksEpic = epics.remove(id).getSubtasks();
        historyManager.remove(id);

        for (Integer subtask : subtasksEpic) {
            subtasks.remove(subtask);
            historyManager.remove(subtask);
        }

        subtasksEpic.clear();
    }

    @Override
    public void deleteSubtaskById(int id) {
        Epic epic = subtasks.get(id).getEpic();
        for (Integer idSubtask : epic.getSubtasks()) {
            if (idSubtask == id) {
                epic.getSubtasks().remove(idSubtask);
                historyManager.remove(idSubtask);
                break;
            }
        }

        subtasks.remove(id);
        historyManager.remove(id);
        calculateStatus(epic);
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    private int getNewId() {
        return ++counter;
    }
}
