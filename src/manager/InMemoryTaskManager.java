package manager;

import task.*;

import java.time.Duration;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    private int counter = 0;
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private final Map<Integer, Subtask> subtasks = new HashMap<>();
    private final HistoryManager historyManager = Managers.getDefaultHistory();
    private final Set<Task> priorityTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));

    private boolean isTasksCollision(Task task1, Task task2) {
        if (task1.getStartTime() == null || task2.getStartTime() == null ||
                task1.getStartTime().isEqual(task2.getEndTime()) ||
                task2.getStartTime().isEqual(task1.getEndTime())) {
            return false;
        }

        return !task1.getEndTime().isBefore(task2.getStartTime()) &&
                !task1.getStartTime().isAfter(task2.getEndTime());
    }

    private boolean checkTimeCollisionTasks(Task task) {
        List<Task> taskList = getPrioritizedTasks();

        if (taskList.isEmpty()) {
            return false;
        } else {
            return taskList.stream()
                    .anyMatch(task2 -> isTasksCollision(task, task2));
        }
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return priorityTasks.stream().toList();
    }

    @Override
    public void updateTask(Task task) throws ManagerSaveException {
        if (!checkTimeCollisionTasks(task)) {
            if (task.getType() == TaskType.SUBTASK) {
                subtasks.put(task.getId(), (Subtask) task);
                Epic epic = epics.get(subtasks.get(task.getId()).getEpic().getId());
                calculateStatus(epic);

                if (task.getStartTime() != null) {
                    priorityTasks.add(task);
                    calculateTime(epic);
                    return;
                }
            } else if (task.getType() == TaskType.EPIC) {
                epics.put(task.getId(), (Epic) task);
            } else {
                tasks.put(task.getId(), task);
            }

            if (task.getStartTime() != null) {
                priorityTasks.add(task);
            }
        }
    }

    private void calculateStatus(Epic epic) {
        List<Integer> subtaskIds = epic.getSubtasks();

        if (subtaskIds.isEmpty()) {
            epic.setStatus(Status.NEW);
            return;
        }

        boolean allDone = true;
        boolean allNew = true;

        for (int subtaskId : subtaskIds) {
            Status status = subtasks.get(subtaskId).getStatus();
            if (status != Status.DONE) allDone = false;
            if (status != Status.NEW) allNew = false;
        }

        if (allDone) {
            epic.setStatus(Status.DONE);
        } else if (allNew) {
            epic.setStatus(Status.NEW);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }

    private void calculateTime(Epic epic) {
        List<Task> prioritizedSubtasks = getPrioritizedTasks()
                .stream()
                .filter(task -> epic.getSubtasks().contains(task.getId()))
                .toList();

        if (!prioritizedSubtasks.isEmpty()) {
            epic.setStartTime(prioritizedSubtasks.getFirst().getStartTime());
            epic.setDuration(Duration.between(epic.getStartTime(), prioritizedSubtasks.getLast().getEndTime()));
        } else {
            epic.setStartTime(null);
            epic.setDuration(null);
        }
    }

    private void setupId(Task task) {
        int idTask = task.getId();
        if (idTask == 0 || tasks.containsKey(idTask) || subtasks.containsKey(idTask) || epics.containsKey(idTask)) {
            if (idTask >= counter) {
                counter = idTask;
            }
            task.setId(getNewId());
        }
    }

    @Override
    public void createTask(Task task) throws ManagerSaveException {
        if (!checkTimeCollisionTasks(task)) {
            setupId(task);
            Task taskClone = task.cloneTask();
            tasks.put(taskClone.getId(), taskClone);

            if (taskClone.getStartTime() != null && taskClone.getDuration() != null) {
                priorityTasks.add(taskClone);
            }
        }
    }

    @Override
    public void createEpic(Epic epic) throws ManagerSaveException {
        if (!checkTimeCollisionTasks(epic)) {
            setupId(epic);
            Epic epicClone = epic.cloneTask();
            epics.put(epicClone.getId(), epicClone);
        }
    }

    @Override
    public void createSubtask(Subtask subtask) throws ManagerSaveException {
        if (!checkTimeCollisionTasks(subtask)) {
            setupId(subtask);
            Subtask subtaskClone = subtask.cloneTask();
            subtasks.put(subtaskClone.getId(), subtaskClone);

            Epic epic = epics.get(subtaskClone.getEpic().getId());
            epic.addSubtaskId(subtaskClone.getId());
            calculateStatus(epic);

            if (subtaskClone.getStartTime() != null && subtaskClone.getDuration() != null) {
                priorityTasks.add(subtaskClone);
                calculateTime(epic);
            }
        }
    }

    @Override
    public void clearTasks() throws ManagerSaveException {
        for (Integer id : tasks.keySet().stream().toList()) {
            deleteTaskById(id);
        }
    }

    @Override
    public void clearEpics() throws ManagerSaveException {
        for (Integer id : epics.keySet()) {
            deleteEpicById(id);
        }
    }

    @Override
    public void clearSubtasks() throws ManagerSaveException {
        for (Integer id : subtasks.keySet().stream().toList()) {
            deleteSubtaskById(id);
        }
    }

    @Override
    public void deleteTaskById(int id) throws ManagerSaveException {
        priorityTasks.remove(tasks.remove(id));
        historyManager.remove(id);
    }

    @Override
    public void deleteEpicById(int id) throws ManagerSaveException {
        epics.remove(id)
                .getSubtasks()
                .forEach(subtaskId -> {
                    subtasks.remove(subtaskId);
                    historyManager.remove(subtaskId);
                    priorityTasks.remove(subtasks.get(subtaskId));
                });
    }

    @Override
    public void deleteSubtaskById(int id) throws ManagerSaveException {
        Subtask subtask = subtasks.get(id);

        subtasks.remove(id);
        historyManager.remove(id);
        priorityTasks.remove(subtask);

        Epic epicOld = epics.get(subtask.getEpic().getId());

        Epic epicNew = new Epic(epicOld.getName(), epicOld.getDescription());
        epicNew.setId(epicOld.getId());
        epicNew.setDuration(epicOld.getDuration());
        epicNew.setStartTime(epicOld.getStartTime());

        List<Integer> epicOldSubtasks = epicOld.getSubtasks().stream()
                .filter(idTemp -> idTemp != id)
                .toList();

        for (Integer epicSubtask : epicOldSubtasks) {
            epicNew.addSubtaskId(epicSubtask);
        }

        updateTask(epicNew);
        calculateStatus(epicNew);
        if (subtask.getStartTime() != null && subtask.getDuration() != null) {
            calculateTime(epicNew);
        }
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
    public List<Subtask> getSubtasks(int idEpic) {
        return epics.get(idEpic).getSubtasks()
                .stream()
                .map(subtasks::get)
                .toList();
    }

    @Override
    public Task getTaskById(int id) {
        Task task = tasks.get(id);
        historyManager.add(task);
        return task.cloneTask();
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = epics.get(id);
        historyManager.add(epic);
        return epic.cloneTask();
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        historyManager.add(subtask);
        return subtask.cloneTask();
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    private int getNewId() {
        return ++counter;
    }
}
