package manager;

import task.Epic;
import task.Subtask;
import task.Task;

import java.util.LinkedList;
import java.util.List;

public interface TaskManager {
    List<Subtask> getSubtasks(int idEpic);

    void updateTask(Task task);

    void createTask(Task task);

    void createEpic(Epic epic);

    void createSubtask(Subtask subtask);

    List<Task> getTasks();

    List<Epic> getEpics();

    List<Subtask> getSubtasks();

    void clearTasks();

    void clearEpics();

    void clearSubtasks();

    Task getTaskById(int id);

    Epic getEpicById(int id);

    Subtask getSubtaskById(int id);

    void deleteTaskById(int id);

    void deleteEpicById(int id);

    void deleteSubtaskById(int id);

    LinkedList<Task> getHistory();
}
