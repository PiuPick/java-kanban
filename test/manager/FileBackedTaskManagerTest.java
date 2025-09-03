package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {
    private static TaskManager taskManager;
    private static File file;
    private static Task task;
    private static Epic epic;
    private static Subtask subtask;

    @BeforeEach
    public void beforeEach() throws IOException {
        file = File.createTempFile("data", ".csv");
        taskManager = Managers.getDefaultManagerFile(file);

        task = new Task("Задача", "Описание");
        taskManager.createTask(task);

        epic = new Epic("Эпик", "Описание");
        taskManager.createEpic(epic);

        subtask = new Subtask(epic, "Подзадача", "Описание");
        taskManager.createSubtask(subtask);
    }

    @Test
    public void addTasksToFile() throws IOException {
        String text = "";

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            bufferedReader.readLine();
            while (bufferedReader.ready()) {
                text += bufferedReader.readLine();
            }
        } catch (IOException e) {
            throw new ManagerSaveException(e);
        }

        assertEquals("1,TASK,Задача,NEW,Описание," +
                "2,EPIC,Эпик,NEW,Описание," +
                "3,SUBTASK,Подзадача,NEW,Описание,2", text);
    }

    @Test
    public void loadFromFile() throws ManagerSaveException {
        TaskManager taskManagerLoader = Managers.getDefaultManagerFile(file);

        System.out.println(task.getDescription());

        assertEquals(taskManagerLoader.getTaskById(1).getDescription(), task.getDescription());
        assertEquals(taskManagerLoader.getEpicById(2).getDescription(), epic.getDescription());
        assertEquals(taskManagerLoader.getSubtaskById(3).getDescription(), subtask.getDescription());
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
