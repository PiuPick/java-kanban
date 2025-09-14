package manager;

import task.*;

import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;
    public FileBackedTaskManager(File dataFile) {
        this.file = dataFile;
    }

    public static FileBackedTaskManager loadFromFile(File file) throws ManagerSaveException {
        FileBackedTaskManager fileManager = new FileBackedTaskManager(file);
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            bufferedReader.readLine();
            while (bufferedReader.ready()) {
                String line = bufferedReader.readLine();
                Task task = fileManager.fromString(line);
                switch (task.getType()) {
                    case TaskType.TASK:
                        fileManager.createTask(task);
                        break;
                    case TaskType.EPIC:
                        fileManager.createEpic((Epic) task);
                        break;
                    case TaskType.SUBTASK:
                        fileManager.createSubtask((Subtask) task);
                        break;
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException(e);
        }
        return fileManager;
    }

    private String toString(Task task) {
        String res = "%d,%s,%s,%s,%s,%s,%s,".formatted(task.getId(), task.getType(), task.getName(),
                task.getStatus(), task.getDescription(), task.getStartTime(), task.getDuration());

        if (task.getType() == TaskType.SUBTASK) {
            Subtask subtask = (Subtask) task;
            res += subtask.getEpic().getId();
        }

        return res;
    }

    private Task fromString(String value) throws ManagerSaveException {
        String[] specifications = value.split(",");

        int id = Integer.parseInt(specifications[0]);
        TaskType type = TaskType.valueOf(specifications[1]);
        String name = specifications[2];
        Status status = Status.valueOf(specifications[3]);
        String description = specifications[4];

        LocalDateTime startTime = null;
        if (!specifications[5].equals("null")) {
            startTime = LocalDateTime.parse(specifications[5]);
        }

        Duration duration = null;
        if (!specifications[6].equals("null")) {
            duration = Duration.parse(specifications[6]);
        }

        switch (type) {
            case TaskType.EPIC:
                Epic epic = new Epic(name, description);
                epic.setId(id);
                epic.setStatus(status);
                return epic;
            case TaskType.SUBTASK:
                int idEpic = Integer.parseInt(specifications[7]);
                Epic epicForSubtask = super.getEpics()
                        .stream()
                        .filter(epicTemp -> epicTemp.getId() == idEpic)
                        .findFirst()
                        .orElseThrow(() -> new IllegalArgumentException("Epic с ID " + idEpic + " не найден"));
                Subtask subtask = new Subtask(epicForSubtask, name, description, status);
                subtask.setId(id);
                subtask.setStartTime(startTime);
                subtask.setDuration(duration);
                return subtask;
            case TaskType.TASK:
                Task task = new Task(name, description);
                task.setId(id);
                task.setStatus(status);
                task.setStartTime(startTime);
                task.setDuration(duration);
                return task;
        }
        return null;
    }

    private void save() throws ManagerSaveException {
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file))) {
            bufferedWriter.write("id,type,name,status,description,startTime,duration,epic\n");

            List<Task> tasks = super.getTasks();
            tasks.addAll(super.getEpics());
            tasks.addAll(super.getSubtasks());

            for (Task task : tasks) {
                bufferedWriter.write(toString(task) + '\n');
            }
        } catch (IOException e) {
            throw new ManagerSaveException(e);
        }
    }

    @Override
    public void updateTask(Task task) throws ManagerSaveException {
        super.updateTask(task);
        save();
    }

    @Override
    public void createTask(Task task) throws ManagerSaveException {
        super.createTask(task);
        save();
    }

    @Override
    public void createEpic(Epic epic) throws ManagerSaveException {
        super.createEpic(epic);
        save();
    }

    @Override
    public void createSubtask(Subtask subtask) throws ManagerSaveException {
        super.createSubtask(subtask);
        save();
    }

    @Override
    public void clearTasks() throws ManagerSaveException {
        super.clearTasks();
        save();
    }

    @Override
    public void clearEpics() throws ManagerSaveException {
        super.clearEpics();
        save();
    }

    @Override
    public void clearSubtasks() throws ManagerSaveException {
        super.clearSubtasks();
        save();
    }

    @Override
    public void deleteTaskById(int id) throws ManagerSaveException {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void deleteEpicById(int id) throws ManagerSaveException {
        super.deleteEpicById(id);
        save();
    }

    @Override
    public void deleteSubtaskById(int id) throws ManagerSaveException {
        super.deleteSubtaskById(id);
        save();
    }
}
