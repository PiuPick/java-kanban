import manager.TaskManager;
import task.*;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");

        TaskManager mgr = new TaskManager();

        // 1) Создание двух независимых тасков:
        Task t1 = new Task("Переезд", "Упаковать вещи");
        Task t2 = new Task("Проверка почты", "Ответить на письма");
        mgr.createTask(t1);
        mgr.createTask(t2);

        // 2) Один эпик с двумя подзадачами:
        Epic epic1 = new Epic("Организовать праздник", "Большой семейный банкет");
        mgr.createEpic(epic1);
        Subtask s1 = new Subtask(epic1, "Закупить продукты", "Составить список блюд");
        Subtask s2 = new Subtask(epic1, "Заказать торт", "Выбрать дизайн");
        mgr.createSubtask(s1);
        mgr.createSubtask(s2);

        // 3) Вывести все списки:
        System.out.println("Tasks: " + mgr.getTasks());
        System.out.println("Epics: " + mgr.getEpics());
        System.out.println("Subtasks: " + mgr.getSubtasks());

        // 4) Изменить статус, проверить пересчёт статуса эпика:
        s1.setStatus(Status.DONE);
        mgr.updateTask(s1);
        System.out.println("Epic1 status after one DONE: " + epic1.getStatus());

        // 5) Удалить задачу и эпик:
        mgr.deleteTaskById(t2.getId());
        mgr.deleteEpicById(epic1.getId());
        System.out.println("После удаления: Tasks=" + mgr.getTasks().size() +
                ", Epics=" + mgr.getEpics().size() +
                ", Subtasks=" + mgr.getSubtasks().size());
    }
}
