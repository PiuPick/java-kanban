package manager;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import task.*;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {
    private static TaskManager taskManager;
    private static Task task;

    @BeforeAll
    public static void beforeAll() throws ManagerSaveException {
        taskManager = Managers.getDefault();

        task = new Task("Задача", "Описание");
        taskManager.createTask(task);

        Epic epic = new Epic("Эпик", "Описание");
        taskManager.createEpic(epic);

        Subtask subtask = new Subtask(epic, "Подзадача", "Описание");
        taskManager.createSubtask(subtask);
    }

    @Test
    public void addTasks() {
        assertEquals(TaskType.TASK, taskManager.getTaskById(1).getType());
    }

    @Test
    public void addEpic() {
        assertEquals(TaskType.EPIC, taskManager.getEpicById(2).getType());
    }

    @Test
    public void addSubtask() {
        assertEquals(TaskType.SUBTASK, taskManager.getSubtaskById(3).getType());
    }

    @Test
    public void conflictBetweenGeneratedIdAndManuallyCreatedOne() throws ManagerSaveException {
        Task taskConflict = new Task("Задача со своим ID", "Описание");
        taskConflict.setId(1);
        taskManager.createTask(taskConflict);

        assertNotEquals(task.getId(), taskConflict.getId());
    }

    @Test
    public void immutabilityOfTheTask() throws ManagerSaveException {
        Task taskUnchanging = new Task("Задача 123", "Описание 123");

        taskManager.createTask(taskUnchanging);

        assertEquals("Задача 123", taskUnchanging.getName());
        assertEquals("Описание 123", taskUnchanging.getDescription());
        assertEquals(Status.NEW, taskUnchanging.getStatus());
        assertTrue(taskUnchanging.getId() > 0);
    }
}