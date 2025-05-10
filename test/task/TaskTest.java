package task;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TaskTest {
    Epic epic1 = new Epic("Эпик 1", "Описание");
    Epic epic2 = new Epic("Эпик 2", "Описание");

    @Test
    public void equalsTasks() {
        Task task1 = new Task("Задача 1", "Описание");
        task1.setId(1);

        Task task2 = new Task("Задача 2", "Описание");
        task2.setId(1);

        assertEquals(task1, task2);
    }

    @Test
    public void equalsInheritorsTaskEpics() {
        epic1.setId(1);
        epic2.setId(1);

        assertEquals(epic1, epic2);
    }

    @Test
    public void equalsInheritorsTaskSubtasks() {
        Subtask subtask1 = new Subtask(epic1, "Подзадача 1", "Описание");
        subtask1.setId(1);

        Subtask subtask2 = new Subtask(epic2, "Подзадача 2", "Описание");
        subtask2.setId(1);

        assertEquals(subtask1, subtask2);
    }
}