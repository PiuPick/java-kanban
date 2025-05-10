package manager;

import org.junit.jupiter.api.Test;
import task.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

class HistoryManagerTest {
    @Test
    void historyShouldKeepPreviousVersionOfTask() {
        TaskManager manager = Managers.getDefault();

        Task task = new Task("Задача", "Описание");
        manager.createTask(task);
        int taskId = task.getId();

        manager.getTaskById(taskId);
        manager.getTasks().getFirst().setName("Задача изменилась");
        manager.getTaskById(taskId);

        List<Task> history = manager.getHistory();

        assertNotEquals(history.get(0).getName(), history.get(1).getName());
    }
}