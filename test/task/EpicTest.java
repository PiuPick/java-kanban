package task;

import manager.ManagerSaveException;
import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

class EpicTest {
    @Test
    public void epicObjectCannotAddedToItself() throws ManagerSaveException {
        TaskManager taskManager = Managers.getDefault();

        Epic epic = new Epic("Эпик", "Описание");
        taskManager.createEpic(epic);

        Subtask subtask = new Subtask(epic, "Подзадача", "Описание");
        subtask.setId(epic.getId());

        taskManager.createSubtask(subtask);

        assertNotEquals(taskManager.getSubtasks().getFirst(), taskManager.getEpics().getFirst());
    }
}