package server;

import com.google.gson.Gson;
import manager.InMemoryTaskManager;
import manager.ManagerSaveException;
import manager.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Status;
import task.Subtask;

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

public class SubtasksServerTest {
    private final TaskManager manager = new InMemoryTaskManager();
    private final HttpTaskServer server = new HttpTaskServer(manager);
    private final Gson gson = HttpTaskServer.getGson();
    private final URI url = URI.create("http://localhost:8080/subtasks");
    private HttpClient client;

    public SubtasksServerTest() throws IOException {
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
    public void testAddSubtaskMethodPostSuccessfully() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 1", "Epic for subtasks");
        manager.createEpic(epic);

        Subtask subtask = new Subtask(epic, "Subtask 1", "Testing subtask");
        subtask.setStatus(Status.NEW);
        subtask.setDuration(Duration.ofMinutes(10));
        subtask.setStartTime(LocalDateTime.now());

        String json = gson.toJson(subtask);

        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        List<Subtask> subtasks = manager.getSubtasks();

        assertNotNull(subtasks);
        assertEquals(201, response.statusCode());
        assertEquals(1, subtasks.size());
        assertEquals("Subtask 1", subtasks.getFirst().getName());
    }

    @Test
    public void testGetAllSubtasksSuccessfully() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic test", "desc");
        manager.createEpic(epic);

        Subtask s1 = new Subtask(epic, "S1", "sub1");
        manager.createSubtask(s1);

        Subtask s2 = new Subtask(epic, "S2", "sub2");
        manager.createSubtask(s2);

        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        Subtask[] result = gson.fromJson(response.body(), Subtask[].class);

        assertEquals(2, result.length);
    }

    @Test
    public void testGetSubtaskByIdSuccessfully() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic test", "desc");
        manager.createEpic(epic);

        Subtask s = new Subtask(epic, "TestSub", "desc");
        manager.createSubtask(s);

        URI getUrl = URI.create("http://localhost:8080/subtasks/" + s.getId());

        HttpRequest request = HttpRequest.newBuilder().uri(getUrl).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        Subtask result = gson.fromJson(response.body(), Subtask.class);
        assertEquals(s.getName(), result.getName());
    }

    @Test
    public void testGetSubtaskByIdNotFound() throws IOException, InterruptedException {
        URI getUrl = URI.create("http://localhost:8080/subtasks/999");

        HttpRequest request = HttpRequest.newBuilder().uri(getUrl).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
    }

    @Test
    public void testDeleteSubtaskByIdSuccessfully() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "Desc");
        manager.createEpic(epic);

        Subtask s = new Subtask(epic, "ToDelete", "desc");
        manager.createSubtask(s);

        URI delUrl = URI.create("http://localhost:8080/subtasks/" + s.getId());

        HttpRequest request = HttpRequest.newBuilder().uri(delUrl).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals(0, manager.getSubtasks().size());
    }
}
