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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HistoryServerTest {
    private final TaskManager manager = new InMemoryTaskManager();
    private final HttpTaskServer server = new HttpTaskServer(manager);
    private final Gson gson = HttpTaskServer.getGson();
    private final URI url = URI.create("http://localhost:8080/history");
    private HttpClient client;

    public HistoryServerTest() throws IOException {
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
    public void testGetHistoryEmptySuccessfully() throws IOException, InterruptedException {
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
    public void testGetHistoryOrderSuccessfully() throws IOException, InterruptedException {
        Task task1 = new Task("Task 1", "desc1");
        Task task2 = new Task("Task 2", "desc2");
        Epic epic = new Epic("Epic", "epic desc");
        Subtask sub1 = new Subtask(epic, "Sub1", "s1");
        Subtask sub2 = new Subtask(epic, "Sub2", "s2");

        manager.createTask(task1);
        manager.createTask(task2);
        manager.createEpic(epic);
        manager.createSubtask(sub1);
        manager.createSubtask(sub2);

        manager.getSubtaskById(sub2.getId());
        manager.getSubtaskById(sub1.getId());
        manager.getTaskById(task2.getId());
        manager.getTaskById(task1.getId());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        Task[] history = gson.fromJson(response.body(), Task[].class);

        assertEquals(4, history.length);
        assertEquals(sub2.getName(), history[0].getName());
        assertEquals(sub1.getName(), history[1].getName());
        assertEquals(task2.getName(), history[2].getName());
        assertEquals(task1.getName(), history[3].getName());
    }

    @Test
    public void testHistoryAfterDeletionsSuccessfully() throws IOException, InterruptedException {
        Task task1 = new Task("Task 1", "desc1");
        Task task2 = new Task("Task 2", "desc2");
        Epic epic = new Epic("Epic", "epic desc");
        Subtask sub1 = new Subtask(epic, "Sub1", "s1");
        Subtask sub2 = new Subtask(epic, "Sub2", "s2");

        manager.createTask(task1);
        manager.createTask(task2);
        manager.createEpic(epic);
        manager.createSubtask(sub1);
        manager.createSubtask(sub2);

        manager.getTaskById(task1.getId());
        manager.getTaskById(task2.getId());
        manager.getEpicById(epic.getId());

        manager.deleteTaskById(task2.getId());

        HttpRequest request1 = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response1.statusCode());
        Task[] history1 = gson.fromJson(response1.body(), Task[].class);

        assertEquals(2, history1.length);
        assertEquals(task1.getName(), history1[0].getName());
        assertEquals(epic.getName(), history1[1].getName());

        manager.getSubtaskById(sub1.getId());
        manager.deleteSubtaskById(sub1.getId());

        HttpRequest request2 = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response2.statusCode());
        Task[] history2 = gson.fromJson(response2.body(), Task[].class);

        assertEquals(2, history2.length);
        assertEquals(task1.getName(), history2[0].getName());
        assertEquals(epic.getName(), history2[1].getName());

        manager.getSubtaskById(sub2.getId());
        manager.deleteTaskById(task1.getId());

        HttpRequest request3 = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response3 = client.send(request3, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response3.statusCode());
        Task[] history3 = gson.fromJson(response3.body(), Task[].class);

        assertEquals(2, history3.length);
        assertEquals(epic.getName(), history3[0].getName());
        assertEquals(sub2.getName(), history3[1].getName());
    }

    @Test
    public void testInvalidPathReturnsNotFound() throws IOException, InterruptedException {
        URI invalid = URI.create("http://localhost:8080/history/1");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(invalid)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
    }
}
