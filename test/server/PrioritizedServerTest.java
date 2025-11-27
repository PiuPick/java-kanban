package server;

import com.google.gson.Gson;
import manager.InMemoryTaskManager;
import manager.ManagerSaveException;
import manager.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Subtask;
import task.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class PrioritizedServerTest {
    private final TaskManager manager = new InMemoryTaskManager();
    private final HttpTaskServer server = new HttpTaskServer(manager);
    private final Gson gson = HttpTaskServer.getGson();
    private final URI url = URI.create("http://localhost:8080/prioritized");
    private HttpClient client;

    public PrioritizedServerTest() throws IOException {
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
    public void testGetPrioritizedEmptySuccessfully() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        Task[] result = gson.fromJson(response.body(), Task[].class);
        assertNotNull(result);
        assertEquals(0, result.length);
    }

    @Test
    public void testGetPrioritizedOrderSuccessfully() throws IOException, InterruptedException {
        Task task1 = new Task("Task 1", "desc1");
        Task task2 = new Task("Task 2", "desc2");
        Epic epic = new Epic("Epic", "epic desc");
        Subtask sub1 = new Subtask(epic, "Sub1", "s1");

        manager.createTask(task1);
        manager.createTask(task2);
        manager.createEpic(epic);
        manager.createSubtask(sub1);

        task1.setStartTime(LocalDateTime.of(2025, 1, 15, 10, 0));
        task1.setDuration(Duration.ofHours(1));
        manager.updateTask(task1);

        task2.setStartTime(LocalDateTime.of(2025, 1, 10, 14, 30));
        task2.setDuration(Duration.ofHours(2));
        manager.updateTask(task2);

        sub1.setStartTime(LocalDateTime.of(2025, 1, 20, 9, 0));
        sub1.setDuration(Duration.ofMinutes(30));
        manager.updateTask(sub1);

        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        Task[] prioritized = gson.fromJson(response.body(), Task[].class);

        assertEquals(3, prioritized.length);
        assertEquals("Task 2", prioritized[0].getName());
        assertEquals("Task 1", prioritized[1].getName());
        assertEquals("Sub1", prioritized[2].getName());
    }

    @Test
    public void testTimeCollisionDetectionViaHttp() throws IOException, InterruptedException {
        Task t1 = new Task("Collision 1", "c1");
        Task t2 = new Task("Collision 2", "c2");

        manager.createTask(t1);
        manager.createTask(t2);

        t1.setStartTime(LocalDateTime.of(2024, 1, 15, 10, 0));
        t1.setDuration(Duration.ofHours(2));
        manager.updateTask(t1);

        t2.setStartTime(LocalDateTime.of(2024, 1, 15, 11, 0));
        t2.setDuration(Duration.ofHours(1));
        manager.updateTask(t2);

        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        Task[] prioritized = gson.fromJson(response.body(), Task[].class);

        assertEquals(1, prioritized.length);
    }

    @Test
    public void testPrioritizedUpdatesAfterDeletion() throws IOException, InterruptedException {
        Task t1 = new Task("ToKeep", "keep");
        Task t2 = new Task("ToDelete", "del");

        manager.createTask(t1);
        manager.createTask(t2);

        t1.setStartTime(LocalDateTime.of(2025, 2, 1, 9, 0));
        t1.setDuration(Duration.ofHours(1));
        manager.updateTask(t1);

        t2.setStartTime(LocalDateTime.of(2025, 2, 1, 10, 0));
        t2.setDuration(Duration.ofHours(1));
        manager.updateTask(t2);

        HttpRequest req1 = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> resp1 = client.send(req1, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, resp1.statusCode());
        Task[] initial = gson.fromJson(resp1.body(), Task[].class);
        assertEquals(2, initial.length);

        URI del = URI.create("http://localhost:8080/tasks/" + t2.getId());
        HttpRequest delReq = HttpRequest.newBuilder().uri(del).DELETE().build();
        HttpResponse<String> delResp = client.send(delReq, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, delResp.statusCode());

        HttpRequest req2 = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> resp2 = client.send(req2, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, resp2.statusCode());
        Task[] updated = gson.fromJson(resp2.body(), Task[].class);
        assertEquals(1, updated.length);
        assertEquals("ToKeep", updated[0].getName());
    }

    @Test
    public void testInvalidPathReturnsNotFound() throws IOException, InterruptedException {
        URI invalid = URI.create("http://localhost:8080/prioritized/1");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(invalid)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
    }
}
