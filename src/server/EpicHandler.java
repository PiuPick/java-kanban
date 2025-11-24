package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.ManagerSaveException;
import manager.NotFoundException;
import manager.TaskManager;
import task.Epic;

import java.io.IOException;

public class EpicHandler extends BaseHttpHandler implements HttpHandler {

    private final TaskManager manager;
    private final Gson gson;

    public EpicHandler(TaskManager manager, Gson gson) {
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
        if (path.equals("/epics")) {
            sendText(h, gson.toJson(manager.getEpics()), 200);
            return;
        }

        String[] parts = path.split("/");
        if (parts.length == 3) {
            try {
                int id = Integer.parseInt(parts[2]);
                Epic epic = manager.getEpicById(id);
                if (epic == null) {
                    sendNotFound(h, "Epic with id " + id + " not found");
                    return;
                }
                sendText(h, gson.toJson(epic), 200);
            } catch (NotFoundException e) {
                sendNotFound(h, e.getMessage());
            }
            return;
        }

        if (parts.length == 4 && "subtasks".equals(parts[3])) {
            try {
                int id = Integer.parseInt(parts[2]);
                sendText(h, gson.toJson(manager.getSubtasks(id)), 200);
            } catch (NotFoundException e) {
                sendNotFound(h, e.getMessage());
            }
            return;
        }

        sendNotFound(h, "Invalid path");
    }

    private void handlePost(HttpExchange h) throws IOException {
        String body = readBody(h);
        Epic epic = gson.fromJson(body, Epic.class);

        try {
            if (epic.getId() == 0) {
                manager.createEpic(epic);
            } else {
                manager.updateTask(epic);
            }
        } catch (ManagerSaveException e) {
            sendHasInteractions(h, "Epic cannot be created/updated: " + e.getMessage());
            return;
        }

        sendText(h, "", 201);
    }

    private void handleDelete(HttpExchange h, String path) throws IOException {
        String[] parts = path.split("/");
        if (parts.length == 3) {
            try {
                int id = Integer.parseInt(parts[2]);
                manager.deleteEpicById(id);
                sendText(h, "", 200);
            } catch (NotFoundException e) {
                sendNotFound(h, e.getMessage());
            }
        } else {
            sendNotFound(h, "Invalid path");
        }
    }
}
