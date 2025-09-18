package task;

import manager.ManagerSaveException;
import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

class SubtaskTest {
    @Test
    public void subtaskObjectCannotAddedToItself() throws ManagerSaveException {
        TaskManager taskManager = Managers.getDefault();

        Epic epic = new Epic("Эпик", "Описание");
        epic.setId(1);

        Subtask subtask = new Subtask(epic, "Подзадача", "Описание");
        subtask.setId(1);

        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask);

        assertNotEquals(taskManager.getSubtasks().getFirst(), taskManager.getEpics().getFirst());
    }
}