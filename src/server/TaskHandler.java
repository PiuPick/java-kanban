package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.ManagerSaveException;
import manager.NotFoundException;
import manager.TaskManager;
import task.Task;

import java.io.IOException;

public class TaskHandler extends BaseHttpHandler implements HttpHandler {

    private final TaskManager manager;
    private final Gson gson;

    public TaskHandler(TaskManager manager, Gson gson) {
        this.manager = manager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        try {
            Method method = Method.valueOf(httpExchange.getRequestMethod());
            String path = httpExchange.getRequestURI().getPath();

            switch (method) {
                case GET:
                    handleGet(httpExchange, path);
                    break;
                case POST:
                    handlePost(httpExchange);
                    break;
                case DELETE:
                    handleDelete(httpExchange, path);
                    break;
            }
        } catch (IllegalArgumentException e) {
            sendServerError(httpExchange, "Метод не поддерживается");
        } catch (Exception e) {
            sendServerError(httpExchange, e.getMessage());
        }
    }

    private void handleGet(HttpExchange httpExchange, String path) throws IOException {
        if (path.equals("/tasks")) {
            sendText(httpExchange, gson.toJson(manager.getTasks()), 200);
            return;
        }

        String[] parts = path.split("/");
        if (parts.length == 3) {
            try {
                int id = Integer.parseInt(parts[2]);
                Task task = manager.getTaskById(id);
                if (task == null) {
                    sendNotFound(httpExchange, "Task with id " + id + " not found");
                    return;
                }
                sendText(httpExchange, gson.toJson(task), 200);
            } catch (NotFoundException e) {
                sendNotFound(httpExchange, e.getMessage());
            }
        } else {
            sendNotFound(httpExchange, "Invalid path");
        }
    }

    private void handlePost(HttpExchange httpExchange) throws IOException {
        String body = readBody(httpExchange);
        Task task = gson.fromJson(body, Task.class);

        try {
            if (task.getId() == 0) {
                manager.createTask(task);
            } else {
                manager.updateTask(task);
            }
        } catch (ManagerSaveException e) {
            sendHasInteractions(httpExchange, "Task overlaps with existing tasks");
            return;
        }

        sendText(httpExchange, "", 201);
    }

    private void handleDelete(HttpExchange httpExchange, String path) throws IOException {
        String[] parts = path.split("/");
        if (parts.length == 3) {
            try {
                int id = Integer.parseInt(parts[2]);
                manager.deleteTaskById(id);
                sendText(httpExchange, "", 201);
            } catch (NotFoundException e) {
                sendNotFound(httpExchange, e.getMessage());
            }
        } else {
            sendNotFound(httpExchange, "Invalid path");
        }
    }
}