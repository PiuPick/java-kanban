package manager;

import task.Epic;
import task.Subtask;
import task.Task;

import java.util.List;

public interface TaskManager {
    List<Subtask> getSubtasks(int idEpic);

    void updateTask(Task task) throws ManagerSaveException;

    void createTask(Task task) throws ManagerSaveException;

    void createEpic(Epic epic) throws ManagerSaveException;

    void createSubtask(Subtask subtask) throws ManagerSaveException;

    List<Task> getTasks();

    List<Epic> getEpics();

    List<Subtask> getSubtasks();

    void clearTasks() throws ManagerSaveException;

    void clearEpics() throws ManagerSaveException;

    void clearSubtasks() throws ManagerSaveException;

    Task getTaskById(int id);

    Epic getEpicById(int id);

    Subtask getSubtaskById(int id);

    void deleteTaskById(int id) throws ManagerSaveException;

    void deleteEpicById(int id) throws ManagerSaveException;

    void deleteSubtaskById(int id) throws ManagerSaveException;

    List<Task> getHistory();
}
