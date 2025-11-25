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
        if (path.equals("/epics")) {
            sendText(httpExchange, gson.toJson(manager.getEpics()), 200);
            return;
        }

        String[] parts = path.split("/");
        if (parts.length == 3) {
            try {
                int id = Integer.parseInt(parts[2]);
                Epic epic = manager.getEpicById(id);
                if (epic == null) {
                    sendNotFound(httpExchange, "Epic with id " + id + " not found");
                    return;
                }
                sendText(httpExchange, gson.toJson(epic), 200);
            } catch (NotFoundException e) {
                sendNotFound(httpExchange, e.getMessage());
            }
            return;
        }

        if (parts.length == 4 && "subtasks".equals(parts[3])) {
            try {
                int id = Integer.parseInt(parts[2]);
                sendText(httpExchange, gson.toJson(manager.getSubtasks(id)), 200);
            } catch (NotFoundException e) {
                sendNotFound(httpExchange, e.getMessage());
            }
            return;
        }

        sendNotFound(httpExchange, "Invalid path");
    }

    private void handlePost(HttpExchange httpExchange) throws IOException {
        String body = readBody(httpExchange);
        Epic epic = gson.fromJson(body, Epic.class);

        try {
            if (epic.getId() == 0) {
                manager.createEpic(epic);
            } else {
                manager.updateTask(epic);
            }
        } catch (ManagerSaveException e) {
            sendHasInteractions(httpExchange, "Epic cannot be created/updated: " + e.getMessage());
            return;
        }

        sendText(httpExchange, "", 201);
    }

    private void handleDelete(HttpExchange httpExchange, String path) throws IOException {
        String[] parts = path.split("/");
        if (parts.length == 3) {
            try {
                int id = Integer.parseInt(parts[2]);
                manager.deleteEpicById(id);
                sendText(httpExchange, "", 201);
            } catch (NotFoundException e) {
                sendNotFound(httpExchange, e.getMessage());
            }
        } else {
            sendNotFound(httpExchange, "Invalid path");
        }
    }
}
