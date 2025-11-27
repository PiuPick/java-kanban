package server;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class BaseHttpHandler {

    protected void sendText(HttpExchange httpExchange, String text, int code) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        httpExchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        httpExchange.sendResponseHeaders(code, resp.length);
        httpExchange.getResponseBody().write(resp);
        httpExchange.close();
    }

    protected void sendNotFound(HttpExchange httpExchange, String message) throws IOException {
        sendText(httpExchange, "Ошибка: " + message, 404);
    }

    protected void sendHasInteractions(HttpExchange httpExchange, String message) throws IOException {
        sendText(httpExchange, "Ошибка: " + message, 406);
    }

    protected void sendServerError(HttpExchange httpExchange, String message) throws IOException {
        sendText(httpExchange, "Ошибка: " + message, 500);
    }

    protected String readBody(HttpExchange httpExchange) throws IOException {
        return new String(httpExchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
    }
}