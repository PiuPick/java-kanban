package manager;

import org.junit.jupiter.api.Test;
import task.Epic;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class ManagersTest {
    @Test
    public void returnedObjectCannotNull() {
        assertNotNull(Managers.getDefault());
    }

    @Test
    public void returnedObjectReadyToWork() {
        TaskManager taskManager = Managers.getDefault();
        Epic epic = new Epic("Эпик", "Описание");
        taskManager.createEpic(epic);

        assertNotNull(taskManager.getEpics().getFirst());
    }
}