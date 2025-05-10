package task;

import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

class SubtaskTest {
    @Test
    public void subtaskObjectCannotAddedToItself() {
        TaskManager taskManager = Managers.getDefault();

        Epic epic = new Epic("Эпик", "Описание");
        epic.setId(1);
        taskManager.createEpic(epic);

        Subtask subtask = new Subtask(epic, "Подзадача", "Описание");
        subtask.setId(1);
        taskManager.createSubtask(subtask);

        assertNotEquals(taskManager.getSubtasks().getFirst(), taskManager.getEpics().getFirst());
    }
}