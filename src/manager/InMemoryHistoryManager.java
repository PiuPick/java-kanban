package manager;

import task.Task;

import java.util.LinkedList;

public class InMemoryHistoryManager implements HistoryManager {
    private final byte MAX_HISTORY_SIZE = 10;
    private final LinkedList<Task> tasksHistory = new LinkedList<>();

    @Override
    public LinkedList<Task> getHistory() {
        return new LinkedList<>(tasksHistory);
    }

    @Override
    public void add(Task task) {
        if (tasksHistory.size() == MAX_HISTORY_SIZE) {
            tasksHistory.removeFirst();
        }
        tasksHistory.add(task.cloneTask());
    }
}
