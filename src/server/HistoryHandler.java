package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.TaskManager;

import java.io.IOException;

public class HistoryHandler extends BaseHttpHandler implements HttpHandler {

    private final TaskManager manager;
    private final Gson gson;

    public HistoryHandler(TaskManager manager, Gson gson) {
        this.manager = manager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();

            if (!path.equals("/history")) {
                sendNotFound(exchange, "Invalid path");
                return;
            }

            if (method.equals("GET"))
                sendText(exchange, gson.toJson(manager.getHistory()), 200);
            else
                sendServerError(exchange, "Метод не поддерживается");
        } catch (Exception e) {
            sendServerError(exchange, e.getMessage());
        }
    }
}
