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
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();

            switch (method) {
                case "GET":
                    handleGet(exchange, path);
                    break;
                case "POST":
                    handlePost(exchange);
                    break;
                case "DELETE":
                    handleDelete(exchange, path);
                    break;
                default:
                    sendServerError(exchange, "Метод не поддерживается");
            }
        } catch (Exception e) {
            sendServerError(exchange, e.getMessage());
        }
    }

    private void handleGet(HttpExchange h, String path) throws IOException {
        if (path.equals("/tasks")) {
            sendText(h, gson.toJson(manager.getTasks()), 200);
            return;
        }

        String[] parts = path.split("/");
        if (parts.length == 3) {
            try {
                int id = Integer.parseInt(parts[2]);
                Task task = manager.getTaskById(id);
                if (task == null) {
                    sendNotFound(h, "Task with id " + id + " not found");
                    return;
                }
                sendText(h, gson.toJson(task), 200);
            } catch (NotFoundException e) {
                sendNotFound(h, e.getMessage());
            }
        } else {
            sendNotFound(h, "Invalid path");
        }
    }

    private void handlePost(HttpExchange h) throws IOException {
        String body = readBody(h);
        Task task = gson.fromJson(body, Task.class);

        try {
            if (task.getId() == 0) {
                manager.createTask(task);
            } else {
                manager.updateTask(task);
            }
        } catch (ManagerSaveException e) {
            sendHasInteractions(h, "Task overlaps with existing tasks");
            return;
        }

        sendText(h, "", 201);
    }

    private void handleDelete(HttpExchange h, String path) throws IOException {
        String[] parts = path.split("/");
        if (parts.length == 3) {
            try {
                int id = Integer.parseInt(parts[2]);
                manager.deleteTaskById(id);
                sendText(h, "", 200);
            } catch (NotFoundException e) {
                sendNotFound(h, e.getMessage());
            }
        } else {
            sendNotFound(h, "Invalid path");
        }
    }
}