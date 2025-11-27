package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.TaskManager;

import java.io.IOException;

public class PrioritizedHandler extends BaseHttpHandler implements HttpHandler {

    private final TaskManager manager;
    private final Gson gson;

    public PrioritizedHandler(TaskManager manager, Gson gson) {
        this.manager = manager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            Method method = Method.valueOf(exchange.getRequestMethod());
            String path = exchange.getRequestURI().getPath();

            if (!"/prioritized".equals(path)) {
                sendNotFound(exchange, "Invalid path");
                return;
            }

            if (method == Method.GET)
                sendText(exchange, gson.toJson(manager.getPrioritizedTasks()), 200);
            else
                sendServerError(exchange, "Метод не поддерживается");
        } catch (Exception e) {
            sendServerError(exchange, e.getMessage() == null ? "Internal error" : e.getMessage());
        }
    }
}
