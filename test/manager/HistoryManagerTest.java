package manager;

import org.junit.jupiter.api.Test;
import task.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HistoryManagerTest {
    @Test
    void theHistoryShouldNotContainAPreliminaryVersionOfTheIssue() {
        TaskManager manager = Managers.getDefault();

        Task task = new Task("Задача", "Описание");
        manager.createTask(task);
        int taskId = task.getId();

        manager.getTaskById(taskId);
        manager.getTasks().getFirst().setName("Задача изменилась");
        manager.getTaskById(taskId);

        List<Task> history = manager.getHistory();

        assertEquals(history.getFirst().getName(), task.getName());
    }
}