package manager;

import task.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final HashMap<Integer, Node<Task>> hashMapTasksHistory = new HashMap<>();
    private Node<Task> headList;
    private Node<Task> tailList;
    private int sizeList = 0;

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    @Override
    public void add(Task task) {
        if (task == null) return;

        Node<Task> newTask = linkLast(task);
        removeNode(newTask);
        hashMapTasksHistory.put(task.getId(), newTask);
    }

    private void removeNode(Node<Task> node) {
        if (hashMapTasksHistory.containsKey(node.data.getId())) {
            if (node.prev != null) node.prev.next = node.next;
            if (node.next != null) node.next.prev = node.prev;

            hashMapTasksHistory.remove(node.data.getId());
            --sizeList;
        }
    }

    private Node<Task> linkLast(Task task) {
        Node<Task> newNode = new Node<>(task);

        if (sizeList == 0) {
            headList = tailList = newNode;
            newNode.prev = null;
        } else {
            newNode.prev = tailList;
            tailList.next = newNode;
        }
        newNode.next = null;

        ++sizeList;

        return newNode;
    }

    private ArrayList<Task> getTasks() {
        ArrayList<Task> tasksArrayList = new ArrayList<>();
        Node<Task> node = headList;

        do {
            tasksArrayList.add(node.data);
            node = node.next;
        } while (node != null);

        return tasksArrayList;
    }
}
