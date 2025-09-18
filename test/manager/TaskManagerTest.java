package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Status;
import task.Subtask;
import task.Task;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

abstract class TaskManagerTest<T extends TaskManager> {
    protected T taskManager;
    private Epic epic;
    private Task task1;
    private Task task2;
    private Subtask subtask1;
    private Subtask subtask2;

    protected abstract T createTaskManager() throws IOException;

    @BeforeEach
    void setUp() throws IOException {
        taskManager = createTaskManager();

        task1 = new Task("Задача 1", "Описание задачи 1");
        task2 = new Task("Задача 2", "Описание задачи 2");
        epic = new Epic("Эпик", "Описание эпика");
        subtask1 = new Subtask(epic, "Подзадача 1", "Описание подзадачи 1");
        subtask2 = new Subtask(epic, "Подзадача 2", "Описание подзадачи 2");

        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
    }

    @Test
    public void conflictBetweenGeneratedIdAndManuallyCreatedOne() throws ManagerSaveException {
        task2.setId(1);
        taskManager.createTask(task2);

        assertNotEquals(taskManager.getTaskById(1).getName(), task2.getName());
    }

    @Test
    public void addTasks() {
        assertEquals(task1, taskManager.getTaskById(1));
        assertEquals(epic, taskManager.getEpicById(3));
        assertEquals(subtask1, taskManager.getSubtaskById(4));
    }

    @Test
    void testTimeCollisionDetection() throws ManagerSaveException {
        task1.setStartTime(LocalDateTime.of(2024, 1, 15, 10, 0));
        task1.setDuration(Duration.ofHours(2));

        task2.setStartTime(LocalDateTime.of(2024, 1, 15, 11, 0));
        task2.setDuration(Duration.ofHours(1));

        taskManager.updateTask(task1);
        taskManager.updateTask(task2);

        assertEquals(1, taskManager.getPrioritizedTasks().size());
    }

    @Test
    void testPrioritizedTasksOrder() throws ManagerSaveException {
        task1.setStartTime(LocalDateTime.of(2024, 1, 15, 12, 0));
        task1.setDuration(Duration.ofHours(1));

        task2.setStartTime(LocalDateTime.of(2024, 1, 15, 10, 0));
        task2.setDuration(Duration.ofHours(1));

        taskManager.updateTask(task1);
        taskManager.updateTask(task2);

        List<Task> prioritized = taskManager.getPrioritizedTasks();
        assertEquals(task2, prioritized.getFirst());
    }

    @Test
    public void immutabilityOfTheTask() throws ManagerSaveException {
        task1.setName("Задача 123");
        assertNotEquals("Задача 123", taskManager.getTaskById(task1.getId()).getName());
    }

    @Test
    void testEpicTimeCalculation() throws ManagerSaveException {
        subtask1.setStartTime(LocalDateTime.of(2024, 1, 15, 10, 0));
        subtask1.setDuration(Duration.ofHours(2));

        subtask2.setStartTime(LocalDateTime.of(2024, 1, 15, 9, 0));
        subtask2.setDuration(Duration.ofHours(1));

        taskManager.updateTask(subtask1);
        taskManager.updateTask(subtask2);

        assertEquals(subtask2.getStartTime(), taskManager.getEpicById(3).getStartTime());
        assertEquals(subtask1.getEndTime(), taskManager.getEpicById(3).getEndTime());
    }

    @Test
    void testEpicStatusCalculation() throws ManagerSaveException {
        subtask1.setStatus(Status.NEW);
        subtask2.setStatus(Status.NEW);
        taskManager.updateTask(subtask2);
        taskManager.updateTask(subtask1);

        assertEquals(Status.NEW, taskManager.getEpics().getFirst().getStatus());

        subtask1.setStatus(Status.DONE);
        subtask2.setStatus(Status.DONE);
        taskManager.updateTask(subtask2);
        taskManager.updateTask(subtask1);

        assertEquals(Status.DONE, taskManager.getEpics().getFirst().getStatus());

        subtask1.setStatus(Status.NEW);
        subtask2.setStatus(Status.DONE);
        taskManager.updateTask(subtask2);
        taskManager.updateTask(subtask1);

        assertEquals(Status.IN_PROGRESS, taskManager.getEpics().getFirst().getStatus());

        subtask1.setStatus(Status.IN_PROGRESS);
        subtask2.setStatus(Status.IN_PROGRESS);
        taskManager.updateTask(subtask2);
        taskManager.updateTask(subtask1);

        assertEquals(Status.IN_PROGRESS, taskManager.getEpics().getFirst().getStatus());
    }

    @Test
    void getPrioritizedTasksShouldReturnTasksInChronologicalOrder() throws ManagerSaveException {
        task1.setStartTime(LocalDateTime.of(2025, 1, 15, 10, 0));
        task1.setDuration(Duration.ofHours(1));

        task2.setStartTime(LocalDateTime.of(2025, 1, 10, 14, 30));
        task2.setDuration(Duration.ofHours(2));

        subtask1.setStartTime(LocalDateTime.of(2025, 1, 20, 9, 0));
        subtask1.setDuration(Duration.ofMinutes(30));

        taskManager.updateTask(task1);
        taskManager.updateTask(subtask1);
        taskManager.updateTask(task2);

        List<Task> prioritizedTasks = taskManager.getPrioritizedTasks();

        assertEquals(3, prioritizedTasks.size());
        assertEquals("Задача 2", prioritizedTasks.get(0).getName());
        assertEquals("Задача 1", prioritizedTasks.get(1).getName());
        assertEquals("Подзадача 1", prioritizedTasks.get(2).getName());
    }

    @Test
    void theConnectionOfTheEpicWithTheSubtask() {
        assertEquals(epic, taskManager.getSubtaskById(4).getEpic());
        assertEquals(subtask1.getId(), taskManager.getEpicById(3).getSubtasks().getFirst());
    }

    @Test
    void aTaskWithNoPriorityShouldBeMissing() throws ManagerSaveException {
        task1.setStartTime(LocalDateTime.of(2025, 1, 15, 10, 0));
        task1.setDuration(Duration.ofHours(1));

        taskManager.updateTask(task1);

        List<Task> prioritizedTasks = taskManager.getPrioritizedTasks();

        assertEquals(1, prioritizedTasks.size());
    }

    @Test
    void getPrioritizedTasksShouldUpdateWhenTasksAreRemoved() throws ManagerSaveException {
        task1.setStartTime(LocalDateTime.of(2025, 1, 15, 10, 0));
        task1.setDuration(Duration.ofHours(1));

        task2.setStartTime(LocalDateTime.of(2025, 1, 10, 14, 30));
        task2.setDuration(Duration.ofHours(2));

        taskManager.updateTask(task1);
        taskManager.updateTask(task2);

        List<Task> initialTasks = taskManager.getPrioritizedTasks();
        taskManager.deleteTaskById(task1.getId());
        List<Task> updatedTasks = taskManager.getPrioritizedTasks();

        assertEquals(2, initialTasks.size());
        assertEquals(1, updatedTasks.size());
        assertEquals(task2, updatedTasks.getFirst());
    }

    @Test
    void checkingTheOperationOfTheTaskHistoryListFunction() throws ManagerSaveException {
        taskManager.getSubtaskById(subtask2.getId());
        taskManager.getSubtaskById(subtask1.getId());
        taskManager.getTaskById(task2.getId());
        taskManager.getTaskById(task1.getId());

        List<Task> history = taskManager.getHistory();

        assertEquals(history.get(0).getName(), subtask2.getName());
        assertEquals(history.get(1).getName(), subtask1.getName());
        assertEquals(history.get(2).getName(), task2.getName());
        assertEquals(history.get(3).getName(), task1.getName());
    }

    @Test
    void checkingTheTaskDeletionFunction() throws ManagerSaveException {
        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task2.getId());
        taskManager.getEpicById(epic.getId());

        taskManager.deleteTaskById(task2.getId());

        List<Task> history = taskManager.getHistory();

        assertEquals(2, history.size());
        assertEquals(history.getFirst().getName(), task1.getName());
        assertEquals(history.getLast().getName(), epic.getName());

        taskManager.getSubtaskById(subtask1.getId());
        taskManager.deleteSubtaskById(subtask1.getId());

        history = taskManager.getHistory();

        assertEquals(2, history.size());
        assertEquals(history.getFirst().getName(), task1.getName());
        assertEquals(history.getLast().getName(), epic.getName());

        taskManager.getSubtaskById(subtask2.getId());
        taskManager.deleteTaskById(task1.getId());

        history = taskManager.getHistory();

        assertEquals(2, history.size());
        assertEquals(history.getFirst().getName(), epic.getName());
        assertEquals(history.getLast().getName(), subtask2.getName());
    }

    @Test
    void checkingToGetRidOfDuplicates() {
        taskManager.getTaskById(task2.getId());
        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task2.getId());

        List<Task> history = taskManager.getHistory();

        assertEquals(2, history.size());
        assertEquals(task1, history.getFirst());
        assertEquals(task2, history.getLast());
    }

    @Test
    void emptyTasksHistory() {
        List<Task> history = taskManager.getHistory();
        assertEquals(0, history.size());
    }
}
