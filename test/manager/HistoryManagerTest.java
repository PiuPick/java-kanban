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

    @Test
    void checkingTheOperationOfTheTaskHistoryListFunction() {
        TaskManager manager = Managers.getDefault();

        Task task_1 = new Task("Задача_1", "Описание_1");
        Task task_2 = new Task("Задача_2", "Описание_2");
        Task task_3 = new Task("Задача_3", "Описание_3");
        Task task_4 = new Task("Задача_4", "Описание_4");

        manager.createTask(task_1);
        manager.createTask(task_2);
        manager.createTask(task_3);
        manager.createTask(task_4);

        manager.getTaskById(task_1.getId());
        manager.getTaskById(task_2.getId());
        manager.getTaskById(task_3.getId());
        manager.getTaskById(task_4.getId());

        List<Task> history = manager.getHistory();

        assertEquals(history.get(0).getName(), task_1.getName());
        assertEquals(history.get(1).getName(), task_2.getName());
        assertEquals(history.get(2).getName(), task_3.getName());
        assertEquals(history.get(3).getName(), task_4.getName());
    }

    @Test
    void checkingTheTaskDeletionFunction() {
        TaskManager manager = Managers.getDefault();

        Task task_1 = new Task("Задача_1", "Описание_1");
        Task task_2 = new Task("Задача_2", "Описание_2");
        Task task_3 = new Task("Задача_3", "Описание_3");

        manager.createTask(task_1);
        manager.createTask(task_2);
        manager.createTask(task_3);

        manager.getTaskById(task_1.getId());
        manager.getTaskById(task_2.getId());
        manager.getTaskById(task_3.getId());

        manager.deleteTaskById(task_2.getId());

        List<Task> history = manager.getHistory();

        assertEquals(history.get(0).getName(), task_1.getName());
        assertEquals(history.get(1).getName(), task_3.getName());
    }

    @Test
    void checkingToGetRidOfDuplicatesPerHour() {
        TaskManager manager = Managers.getDefault();

        Task task_1 = new Task("Задача_1", "Описание_1");
        Task task_2 = new Task("Задача_2", "Описание_2");

        manager.createTask(task_1);
        manager.createTask(task_2);

        manager.getTaskById(task_2.getId());
        manager.getTaskById(task_1.getId());
        manager.getTaskById(task_2.getId());

        List<Task> history = manager.getHistory();

        assertEquals(2, history.size());
    }
}