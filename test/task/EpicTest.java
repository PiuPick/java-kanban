package task;

import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

class EpicTest {
    @Test
    public void epicObjectCannotAddedToItself() {
        TaskManager taskManager = Managers.getDefault();

        Epic epic = new Epic("Эпик", "Описание");
        taskManager.createEpic(epic);

        Subtask subtask = new Subtask(epic, "Подзадача", "Описание");
        subtask.setId(epic.getId());

        taskManager.createSubtask(subtask);

        assertNotEquals(taskManager.getSubtasks().getFirst(), taskManager.getEpics().getFirst());
    }
}