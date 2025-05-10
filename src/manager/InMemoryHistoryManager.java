package manager;

import task.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private List<Task> tasksHistory = new ArrayList<>(10);

    @Override
    public List<Task> getHistory() {
        return tasksHistory;
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
