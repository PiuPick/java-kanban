package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import manager.Managers;
import manager.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskServer {

    private static final int PORT = 8080;
    private final HttpServer server;
    private final TaskManager manager;
    private final Gson gson;

    public HttpTaskServer(TaskManager manager) throws IOException {
        this.manager = manager;
        this.server = HttpServer.create(new InetSocketAddress(PORT), 0);
        this.gson = createGson();
        initContexts();
    }

    public static Gson createGson() {
        return new GsonBuilder()
                .serializeNulls()
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
    }

    public static Gson getGson() {
        return createGson();
    }

    public static void main(String[] args) throws IOException {
        TaskManager manager = Managers.getDefault();

        HttpTaskServer httpServer = new HttpTaskServer(manager);
        httpServer.start();
        System.out.println("Нажмите Enter для остановки сервера");
        System.in.read();
        httpServer.stop();
    }

    private void initContexts() {
        server.createContext("/tasks", new TaskHandler(manager, gson));
        server.createContext("/subtasks", new SubtaskHandler(manager, gson));
        server.createContext("/epics", new EpicHandler(manager, gson));
        server.createContext("/history", new HistoryHandler(manager, gson));
        server.createContext("/prioritized", new PrioritizedHandler(manager, gson));
    }

    public void start() {
        server.start();
    }

    public void stop() {
        server.stop(1);
    }
}
