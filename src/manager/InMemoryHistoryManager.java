package manager;

import task.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    private final Map<Integer, Node<Task>> hashMapTasksHistory = new HashMap<>();
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

        removeNode(task);
        hashMapTasksHistory.put(task.getId(), linkLast(task));
    }

    @Override
    public void remove(int id) {
        removeNode(hashMapTasksHistory.get(id).getData());
    }

    private void removeNode(Task task) {
        if (hashMapTasksHistory.containsKey(task.getId())) {
            Node<Task> node = hashMapTasksHistory.get(task.getId());

            if (node.getPrev() != null) {
                Node<Task> prev = node.getPrev();
                prev.setNext(node.getNext());
            } else {
                headList = node.getNext();
            }
            if (node.getNext() != null) {
                Node<Task> next = node.getNext();
                next.setPrev(node.getPrev());
            }

            hashMapTasksHistory.remove(node.getData().getId());
            --sizeList;
        }
    }

    private Node<Task> linkLast(Task task) {
        Node<Task> newNode = new Node<>(task);

        if (sizeList == 0) {
            headList = tailList = newNode;
            newNode.setPrev(null);
        } else {
            tailList.setNext(newNode);
            newNode.setPrev(tailList);
            tailList = newNode;
        }

        ++sizeList;

        return newNode;
    }

    private List<Task> getTasks() {
        List<Task> tasksArrayList = new ArrayList<>();
        Node<Task> node = headList;

        do {
            tasksArrayList.add(node.getData());
            node = node.getNext();
        } while (node != null);

        return tasksArrayList;
    }
}
