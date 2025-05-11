package manager;

import task.Task;

import java.util.LinkedList;

public interface HistoryManager {
    void add(Task task);

    LinkedList<Task> getHistory();
}
