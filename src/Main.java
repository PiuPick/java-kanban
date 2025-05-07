import manager.TaskManager;
import task.*;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");

        TaskManager mgr = new TaskManager();

        // 1) Создание двух независимых тасков:
        Task t1 = mgr.createTask("Переезд", "Упаковать вещи");
        Task t2 = mgr.createTask("Проверка почты", "Ответить на письма");

        // 2) Один эпик с двумя подзадачами:
        Epic epic1 = mgr.createEpic("Организовать праздник", "Большой семейный банкет");
        Subtask s1 = mgr.createSubtask(epic1, "Закупить продукты", "Составить список блюд");
        Subtask s2 = mgr.createSubtask(epic1, "Заказать торт", "Выбрать дизайн");

        // 3) Вывести все списки:
        System.out.println("Tasks: " + mgr.getTasks().values());
        System.out.println("Epics: " + mgr.getEpics().values());
        System.out.println("Subtasks: " + mgr.getSubtasks().values());

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
