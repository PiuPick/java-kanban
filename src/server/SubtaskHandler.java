package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.ManagerSaveException;
import manager.NotFoundException;
import manager.TaskManager;
import task.Subtask;

import java.io.IOException;

public class SubtaskHandler extends BaseHttpHandler implements HttpHandler {

    private final TaskManager manager;
    private final Gson gson;

    public SubtaskHandler(TaskManager manager, Gson gson) {
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
        if (path.equals("/subtasks")) {
            sendText(h, gson.toJson(manager.getSubtasks()), 200);
            return;
        }

        String[] parts = path.split("/");
        if (parts.length == 3) {
            try {
                int id = Integer.parseInt(parts[2]);

                Subtask subtask = manager.getSubtaskById(id);
                if (subtask == null) {
                    sendNotFound(h, "Subtask with id " + id + " not found");
                    return;
                }

                sendText(h, gson.toJson(subtask), 200);

            } catch (NotFoundException e) {
                sendNotFound(h, e.getMessage());
            }

        } else {
            sendNotFound(h, "Invalid path");
        }
    }

    private void handlePost(HttpExchange h) throws IOException {
        String body = readBody(h);
        Subtask subtask = gson.fromJson(body, Subtask.class);

        try {
            if (subtask.getId() == 0) {
                manager.createSubtask(subtask);
            } else {
                manager.updateTask(subtask);
            }
        } catch (ManagerSaveException e) {
            sendHasInteractions(h, "Subtask overlaps with existing tasks");
            return;
        }

        sendText(h, "", 201);
    }

    private void handleDelete(HttpExchange h, String path) throws IOException {
        String[] parts = path.split("/");
        if (parts.length == 3) {
            try {
                int id = Integer.parseInt(parts[2]);
                manager.deleteSubtaskById(id);
                sendText(h, "", 200);
            } catch (NotFoundException e) {
                sendNotFound(h, e.getMessage());
            }
        } else {
            sendNotFound(h, "Invalid path");
        }
    }
}
