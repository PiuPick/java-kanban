package manager;

import task.Task;

import java.util.LinkedList;

public class InMemoryHistoryManager implements HistoryManager {
    private final LinkedList<Task> tasksHistory = new LinkedList<>();

    @Override
    public LinkedList<Task> getHistory() {
        return new LinkedList<>(tasksHistory);
    }

    @Override
    public void add(Task task) {
        if (tasksHistory.size() == 10) {
            tasksHistory.removeFirst();
        }
        Task cloneTask = new Task(task.getName(), task.getDescription());
        cloneTask.setId(task.getId());

        tasksHistory.add(cloneTask);
    }
}
