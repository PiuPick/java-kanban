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
        if (path.equals("/subtasks")) {
            sendText(httpExchange, gson.toJson(manager.getSubtasks()), 200);
            return;
        }

        String[] parts = path.split("/");
        if (parts.length == 3) {
            try {
                int id = Integer.parseInt(parts[2]);

                Subtask subtask = manager.getSubtaskById(id);
                if (subtask == null) {
                    sendNotFound(httpExchange, "Subtask with id " + id + " not found");
                    return;
                }

                sendText(httpExchange, gson.toJson(subtask), 200);

            } catch (NotFoundException e) {
                sendNotFound(httpExchange, e.getMessage());
            }

        } else {
            sendNotFound(httpExchange, "Invalid path");
        }
    }

    private void handlePost(HttpExchange httpExchange) throws IOException {
        String body = readBody(httpExchange);
        Subtask subtask = gson.fromJson(body, Subtask.class);

        try {
            if (subtask.getId() == 0) {
                manager.createSubtask(subtask);
            } else {
                manager.updateTask(subtask);
            }
        } catch (ManagerSaveException e) {
            sendHasInteractions(httpExchange, "Subtask overlaps with existing tasks");
            return;
        }

        sendText(httpExchange, "", 201);
    }

    private void handleDelete(HttpExchange httpExchange, String path) throws IOException {
        String[] parts = path.split("/");
        if (parts.length == 3) {
            try {
                int id = Integer.parseInt(parts[2]);
                manager.deleteSubtaskById(id);
                sendText(httpExchange, "", 201);
            } catch (NotFoundException e) {
                sendNotFound(httpExchange, e.getMessage());
            }
        } else {
            sendNotFound(httpExchange, "Invalid path");
        }
    }
}
