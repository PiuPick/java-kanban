import manager.TaskManager;
import task.*;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!\n");

        TaskManager taskManager = new TaskManager();

        System.out.println("1) Создание двух независимых задач");
        Task movingTask = new Task("Переезд", "Упаковать вещи");
        Task emailTask = new Task("Проверка почты", "Ответить на письма");
        taskManager.createTask(movingTask);
        taskManager.createTask(emailTask);

        System.out.println("2) Создание большого эпика с двумя подзадачами");
        Epic holidayTask = new Epic("Организовать праздник", "Большой семейный банкет");
        taskManager.createEpic(holidayTask);
        Subtask productsBuyTask = new Subtask(holidayTask, "Закупить продукты", "Составить список блюд");
        Subtask cakeBuyTask = new Subtask(holidayTask, "Заказать торт", "Выбрать дизайн");
        taskManager.createSubtask(productsBuyTask);
        taskManager.createSubtask(cakeBuyTask);

        System.out.println("3) Создание второго эпика с одной подзадачей\n");
        Epic houseBuyTask = new Epic("Купить квартиру", "Найти варианты");
        taskManager.createEpic(houseBuyTask);
        Subtask searchRealtorTask = new Subtask(houseBuyTask, "Найти риелтора", "Согласовать условия");
        taskManager.createSubtask(searchRealtorTask);

        System.out.println("4) Вывод всех списков задач");
        System.out.println("Tasks:    " + taskManager.getTasks());
        System.out.println("Epics:    " + taskManager.getEpics());
        System.out.println("Subtasks: " + taskManager.getSubtasks() + '\n');

        System.out.println("5) Проверка статуса первого эпика до и после выполнения одной из подзадач");
        System.out.println("holidayTask status befoe one DONE: " + holidayTask.getStatus());
        productsBuyTask.setStatus(Status.DONE);
        taskManager.updateTask(productsBuyTask);
        System.out.println("holidayTask status after one DONE: " + holidayTask.getStatus() + '\n');

        System.out.println("6) Проверка статуса второго эпика до и после выполнения единственной подзадачи");
        System.out.println("houseBuyTask status before: " + houseBuyTask.getStatus());
        searchRealtorTask.setStatus(Status.DONE);
        taskManager.updateTask(searchRealtorTask);
        System.out.println("houseBuyTask status after DONE: " + houseBuyTask.getStatus() + '\n');

        System.out.println("7) Удаление одной независимой задачи и большого эпика");
        System.out.println("До удаления: Tasks=" + taskManager.getTasks().size() +
                ", Epics=" + taskManager.getEpics().size() +
                ", Subtasks=" + taskManager.getSubtasks().size());

        taskManager.deleteTaskById(emailTask.getId());
        taskManager.deleteEpicById(holidayTask.getId());

        System.out.println("После удаления: Tasks=" + taskManager.getTasks().size() +
                ", Epics=" + taskManager.getEpics().size() +
                ", Subtasks=" + taskManager.getSubtasks().size() + '\n');

        System.out.println("8) Удаление единственной подзадачи эпика и просмотр изменения статуса эпика");
        taskManager.deleteSubtaskById(searchRealtorTask.getId());
        System.out.println("После удаления: Tasks=" + taskManager.getTasks().size() +
                ", Epics=" + taskManager.getEpics().size() +
                ", Subtasks=" + taskManager.getSubtasks().size() + '\n' +
                "houseBuyTask status after delete subtask: " + houseBuyTask.getStatus() + '\n');

        System.out.println("9) Заполнение раннее удаленных задач, эпиков и подзадач\n");
        taskManager.createTask(emailTask);
        taskManager.createEpic(holidayTask);
        taskManager.createSubtask(productsBuyTask);
        taskManager.createSubtask(cakeBuyTask);
        taskManager.createSubtask(searchRealtorTask);

        System.out.println("10) Получение задач по ID");
        System.out.println("emailTask         " + taskManager.getTaskById(emailTask.getId()));
        System.out.println("holidayTask       " + taskManager.getEpicById(holidayTask.getId()));
        System.out.println("searchRealtorTask " + taskManager.getSubtaskById(searchRealtorTask.getId()) + '\n');

        System.out.println("11) Удаление различных типов задач");
        taskManager.clearTasks();
        System.out.println("После удаления TASK: Tasks=" + taskManager.getTasks().size() +
                ", Epics=" + taskManager.getEpics().size() +
                ", Subtasks=" + taskManager.getSubtasks().size());
        taskManager.clearSubtasks();
        System.out.println("После удаления SUBTASK: Tasks=" + taskManager.getTasks().size() +
                ", Epics=" + taskManager.getEpics().size() +
                ", Subtasks=" + taskManager.getSubtasks().size());
        System.out.println("Заполним удаленные подзадачи для демонстрации работы метода clearEpics");
        taskManager.createSubtask(productsBuyTask);
        taskManager.createSubtask(cakeBuyTask);
        taskManager.createSubtask(searchRealtorTask);
        System.out.println("До удаления EPIC: Tasks=" + taskManager.getTasks().size() +
                ", Epics=" + taskManager.getEpics().size() +
                ", Subtasks=" + taskManager.getSubtasks().size());
        taskManager.clearEpics();
        System.out.println("После удаления EPIC: Tasks=" + taskManager.getTasks().size() +
                ", Epics=" + taskManager.getEpics().size() +
                ", Subtasks=" + taskManager.getSubtasks().size() + '\n');

        System.out.println("12) Вывод всех списков задач");
        System.out.println("Tasks:    " + taskManager.getTasks());
        System.out.println("Epics:    " + taskManager.getEpics());
        System.out.println("Subtasks: " + taskManager.getSubtasks() + '\n');
    }
}
