package server;

import com.google.gson.Gson;
import manager.InMemoryTaskManager;
import manager.ManagerSaveException;
import manager.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Status;
import task.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TasksServerTest {
    private final TaskManager manager = new InMemoryTaskManager();
    private final HttpTaskServer server = new HttpTaskServer(manager);
    private final Gson gson = HttpTaskServer.getGson();
    private final URI url = URI.create("http://localhost:8080/tasks");
    private HttpClient client;

    public TasksServerTest() throws IOException {
    }

    @BeforeEach
    public void setUp() throws ManagerSaveException {
        manager.clearTasks();
        manager.clearSubtasks();
        manager.clearEpics();
        server.start();
        client = HttpClient.newHttpClient();
    }

    @AfterEach
    void shutDown() {
        server.stop();
    }

    @Test
    public void testAddTaskMethodPostSuccessfully() throws IOException, InterruptedException {
        Task task = new Task("Test 1", "Testing task 1");
        task.setStatus(Status.NEW);
        task.setDuration(Duration.ofMinutes(5));
        task.setStartTime(LocalDateTime.now());

        String taskJson = gson.toJson(task);

        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

        List<Task> tasksFromManager = manager.getTasks();
        assertNotNull(tasksFromManager);

        assertEquals(201, response.statusCode());
        assertEquals(1, tasksFromManager.size());
        assertEquals("Test 1", tasksFromManager.getFirst().getName());
    }

    @Test
    public void testUpdateTaskMethodPostSuccessfully() throws IOException, InterruptedException {
        Task task = new Task("Old", "Desc");
        manager.createTask(task);

        task.setName("Updated task");
        String json = gson.toJson(task);

        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
        assertEquals("Updated task", manager.getTaskById(task.getId()).getName());
    }

    @Test
    public void testGetAllTasksSuccessfully() throws IOException, InterruptedException {
        Task t1 = new Task("T1", "D1");
        Task t2 = new Task("T2", "D2");

        manager.createTask(t1);
        manager.createTask(t2);

        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        Task[] result = gson.fromJson(response.body(), Task[].class);
        assertEquals(2, result.length);
    }

    @Test
    public void testGetTaskByIdSuccessfully() throws IOException, InterruptedException {
        Task task = new Task("Test", "Desc");
        manager.createTask(task);

        URI getUrl = URI.create("http://localhost:8080/tasks/" + task.getId());

        HttpRequest request = HttpRequest.newBuilder().uri(getUrl).GET().build();
        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        Task result = gson.fromJson(response.body(), Task.class);
        assertEquals(task.getName(), result.getName());
    }

    @Test
    public void testGetTaskByIdNotFound() throws IOException, InterruptedException {
        URI getUrl = URI.create("http://localhost:8080/tasks/999");

        HttpRequest request = HttpRequest.newBuilder().uri(getUrl).GET().build();
        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
    }

    @Test
    public void testDeleteTaskByIdSuccessfully() throws IOException, InterruptedException {
        Task task = new Task("Delete me", "Test");
        manager.createTask(task);

        URI delUrl = URI.create("http://localhost:8080/tasks/" + task.getId());

        HttpRequest request = HttpRequest.newBuilder().uri(delUrl).DELETE().build();
        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
        assertEquals(0, manager.getTasks().size());
    }

    @Test
    public void testDeleteTaskByIdNotFound() throws IOException, InterruptedException {
        URI delUrl = URI.create("http://localhost:8080/tasks/555");

        HttpRequest request = HttpRequest.newBuilder().uri(delUrl).DELETE().build();
        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
    }
}
