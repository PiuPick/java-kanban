package manager;

import task.Task;

import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final int MAX_HISTORY_SIZE = 10;
    private final List<Task> tasksHistory = new LinkedList<>();

    @Override
    public List<Task> getHistory() {
        return new LinkedList<>(tasksHistory);
    }

    @Override
    public void add(Task task) {
        if (task == null) return;

        if (tasksHistory.size() == MAX_HISTORY_SIZE) {
            tasksHistory.removeFirst();
        }

        tasksHistory.add(task.cloneTask());
    }
}
