import manager.TaskManager;
import task.*;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");

        TaskManager taskManager = new TaskManager();

        // 1) Создание двух независимых тасков:
        Task movingTask = new Task("Переезд", "Упаковать вещи");
        Task emailTask = new Task("Проверка почты", "Ответить на письма");
        taskManager.createTask(movingTask);
        taskManager.createTask(emailTask);

        // 2) Один эпик с двумя подзадачами:
        Epic holidayTask = new Epic("Организовать праздник", "Большой семейный банкет");
        taskManager.createEpic(holidayTask);
        Subtask productsBuyTask = new Subtask(holidayTask, "Закупить продукты", "Составить список блюд");
        Subtask cakeBuyTask = new Subtask(holidayTask, "Заказать торт", "Выбрать дизайн");
        taskManager.createSubtask(productsBuyTask);
        taskManager.createSubtask(cakeBuyTask);

        // 3) Второй эпик с одной подзадачей:
        Epic houseBuyTask = new Epic("Купить квартиру", "Найти варианты");
        taskManager.createEpic(houseBuyTask);
        Subtask searchRealtorTask = new Subtask(houseBuyTask, "Найти риелтора", "Согласовать условия");
        taskManager.createSubtask(searchRealtorTask);

        // 4) Вывести все списки:
        System.out.println("Tasks:    " + taskManager.getTasks());
        System.out.println("Epics:    " + taskManager.getEpics());
        System.out.println("Subtasks: " + taskManager.getSubtasks() + '\n');

        // 5) Проверить статус первого эпика до и после выполнения одной из сабтасок
        System.out.println("holidayTask status befoe one DONE: " + holidayTask.getStatus());
        productsBuyTask.setStatus(Status.DONE);
        taskManager.updateTask(productsBuyTask);
        System.out.println("holidayTask status after one DONE: " + holidayTask.getStatus() + '\n');

        // 6) Проверить статус второго эпика до и после выполнения единственной сабтаски:
        System.out.println("houseBuyTask status before: " + houseBuyTask.getStatus());
        searchRealtorTask.setStatus(Status.DONE);
        taskManager.updateTask(searchRealtorTask);
        System.out.println("houseBuyTask status after DONE: " + houseBuyTask.getStatus() + '\n');

        // 7) Удалить одну независимую задачу и первый эпик:
        System.out.println("До удаления: Tasks=" + taskManager.getTasks().size() +
                ", Epics=" + taskManager.getEpics().size() +
                ", Subtasks=" + taskManager.getSubtasks().size());

        taskManager.deleteTaskById(emailTask.getId());
        taskManager.deleteEpicById(holidayTask.getId());

        System.out.println("После удаления: Tasks=" + taskManager.getTasks().size() +
                ", Epics=" + taskManager.getEpics().size() +
                ", Subtasks=" + taskManager.getSubtasks().size() + '\n');

        // 7) Удалить единственную сабтаску эпика и посмотреть на изенение статуса эпика:
        taskManager.deleteSubtaskById(searchRealtorTask.getId());
        System.out.println("После удаления: Tasks=" + taskManager.getTasks().size() +
                ", Epics=" + taskManager.getEpics().size() +
                ", Subtasks=" + taskManager.getSubtasks().size() + '\n' +
                "houseBuyTask status after delete subtask: " + houseBuyTask.getStatus());
    }
}
