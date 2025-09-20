package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Subtask;
import task.Task;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    private TaskManager taskManager;
    private File file;
    private Task task;
    private Epic epic;
    private Subtask subtask;

    @Override
    protected FileBackedTaskManager createTaskManager() throws IOException {
        file = File.createTempFile("data", ".csv");
        return new FileBackedTaskManager(file);
    }

    @BeforeEach
    public void beforeEach() throws IOException {
        taskManager = createTaskManager();

        task = new Task("Задача", "Описание");
        task.setStartTime(LocalDateTime.of(2025, 10, 1, 18, 0));
        task.setDuration(Duration.ofHours(2).plusMinutes(15));

        epic = new Epic("Эпик", "Описание");

        subtask = new Subtask(epic, "Подзадача", "Описание");
        subtask.setStartTime(LocalDateTime.of(2025, 12, 31, 23, 55));
        subtask.setDuration(Duration.ofMinutes(5));

        taskManager.createTask(task);
        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask);
    }

    @Test
    void loadFromEmptyFile() throws IOException {
        File emptyFile = File.createTempFile("empty", ".csv");
        taskManager = FileBackedTaskManager.loadFromFile(emptyFile);

        assertTrue(taskManager.getTasks().isEmpty());
        assertTrue(taskManager.getEpics().isEmpty());
        assertTrue(taskManager.getSubtasks().isEmpty());
    }

    @Test
    void saveAndLoadWithTimeParameters() throws IOException {
        File file = File.createTempFile("timeTest", ".csv");
        FileBackedTaskManager manager = new FileBackedTaskManager(file);

        manager.createTask(task);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(file);
        Task loadedTask = loadedManager.getTaskById(task.getId());

        assertEquals(task.getStartTime(), loadedTask.getStartTime());
        assertEquals(task.getDuration(), loadedTask.getDuration());
        assertEquals(task.getEndTime(), loadedTask.getEndTime());
    }

    @Test
    public void loadFromFile() throws ManagerSaveException {
        TaskManager taskManagerLoader = Managers.getDefaultManagerFile(file);

        assertEquals(taskManagerLoader.getTaskById(1).getDescription(), task.getDescription());
        assertEquals(taskManagerLoader.getTaskById(1).getStartTime(), task.getStartTime());
        assertEquals(taskManagerLoader.getTaskById(1).getDuration(), task.getDuration());

        assertEquals(taskManagerLoader.getEpicById(2).getDescription(), epic.getDescription());
        assertEquals(taskManagerLoader.getEpicById(2).getStartTime(), subtask.getStartTime());
        assertEquals(taskManagerLoader.getEpicById(2).getDuration(), subtask.getDuration());

        assertEquals(taskManagerLoader.getSubtaskById(3).getDescription(), subtask.getDescription());
        assertEquals(taskManagerLoader.getSubtaskById(3).getStartTime(), subtask.getStartTime());
        assertEquals(taskManagerLoader.getSubtaskById(3).getDuration(), subtask.getDuration());
    }

    @Test
    public void checkingForAnIncorrectEpicId() throws IOException {
        File file = File.createTempFile("incorrectData", ".csv");

        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file))) {
            bufferedWriter.write("id,type,name,status,description,startTime,duration,epic\n" +
                    "1,TASK,Задача,NEW,Описание,2024-01-15T10:00,PT2H,\n" +
                    "2,EPIC,Эпик,NEW,Описание,null,null,\n" +
                    "3,SUBTASK,Подзадача,NEW,Описание,2025-09-15T02:00,PT6H,777\n");
        }

        assertThrows(IllegalArgumentException.class, () -> {
            taskManager = FileBackedTaskManager.loadFromFile(file);
        });
    }
}
