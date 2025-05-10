package manager;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import task.*;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {
    public static InMemoryTaskManager taskManager;
    public static Task task;
    public static Epic epic;
    public static Subtask subtask;

    @BeforeAll
    public static void beforeAll() {
        taskManager = new InMemoryTaskManager();

        task = new Task("Задача", "Описание");
        taskManager.createTask(task);

        epic = new Epic("Эпик", "Описание");
        taskManager.createEpic(epic);

        subtask = new Subtask(epic, "Подзадача", "Описание");
        taskManager.createSubtask(subtask);
    }

    @Test
    public void addTasks() {
        assertEquals(taskManager.getTaskById(1).getType(), TaskType.TASK);
    }

    @Test
    public void addEpic() {
        assertEquals(taskManager.getEpicById(2).getType(), TaskType.EPIC);
    }

    @Test
    public void addSubtask() {
        assertEquals(taskManager.getSubtaskById(3).getType(), TaskType.SUBTASK);
    }

    @Test
    public void conflictBetweenGeneratedIdAndManuallyCreatedOne() {
        Task taskConflict = new Task("Задача со своим ID", "Описание");
        taskConflict.setId(1);
        taskManager.createTask(taskConflict);

        assertNotEquals(task.getId(), taskConflict.getId());
    }

    @Test
    public void immutabilityOfTheTask() {
        Task taskUnchanging = new Task("Задача 123", "Описание 123");

        taskManager.createTask(taskUnchanging);

        assertEquals(taskUnchanging.getName(), "Задача 123");
        assertEquals(taskUnchanging.getDescription(), "Описание 123");
        assertEquals(taskUnchanging.getStatus(), Status.NEW);
        assertNotNull(taskUnchanging.getId());
    }
}