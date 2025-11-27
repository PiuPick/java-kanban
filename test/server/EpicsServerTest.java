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

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class EpicsServerTest {
    private final TaskManager manager = new InMemoryTaskManager();
    private final HttpTaskServer server = new HttpTaskServer(manager);
    private final Gson gson = HttpTaskServer.getGson();
    private final URI url = URI.create("http://localhost:8080/epics");
    private HttpClient client;

    public EpicsServerTest() throws IOException {
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
    public void testAddEpicMethodPostSuccessfully() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 1", "Epic description");
        String json = gson.toJson(epic);

        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        List<Epic> epics = manager.getEpics();

        assertNotNull(epics);
        assertEquals(201, response.statusCode());
        assertEquals(1, epics.size());
        assertEquals("Epic 1", epics.getFirst().getName());
    }

    @Test
    public void testGetAllEpicsSuccessfully() throws IOException, InterruptedException {
        Epic e1 = new Epic("Epic A", "desc A");
        Epic e2 = new Epic("Epic B", "desc B");
        manager.createEpic(e1);
        manager.createEpic(e2);

        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        Epic[] result = gson.fromJson(response.body(), Epic[].class);

        assertEquals(2, result.length);
    }

    @Test
    public void testGetEpicByIdSuccessfully() throws IOException, InterruptedException {
        Epic epic = new Epic("Single Epic", "desc");
        manager.createEpic(epic);

        URI getUrl = URI.create("http://localhost:8080/epics/" + epic.getId());

        HttpRequest request = HttpRequest.newBuilder().uri(getUrl).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        Epic result = gson.fromJson(response.body(), Epic.class);
        assertEquals(epic.getName(), result.getName());
    }

    @Test
    public void testGetEpicByIdNotFound() throws IOException, InterruptedException {
        URI getUrl = URI.create("http://localhost:8080/epics/9999");

        HttpRequest request = HttpRequest.newBuilder().uri(getUrl).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
    }

    @Test
    public void testGetEpicSubtasksSuccessfully() throws IOException, InterruptedException {
        Epic epic = new Epic("EpicWithSubs", "has subtasks");
        manager.createEpic(epic);

        Subtask s1 = new Subtask(epic, "Sub1", "s1");
        Subtask s2 = new Subtask(epic, "Sub2", "s2");
        manager.createSubtask(s1);
        manager.createSubtask(s2);

        URI getUrl = URI.create("http://localhost:8080/epics/" + epic.getId() + "/subtasks");

        HttpRequest request = HttpRequest.newBuilder().uri(getUrl).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        Subtask[] result = gson.fromJson(response.body(), Subtask[].class);
        assertEquals(2, result.length);
    }

    @Test
    public void testDeleteEpicByIdSuccessfully() throws IOException, InterruptedException {
        Epic epic = new Epic("ToDeleteEpic", "desc");
        manager.createEpic(epic);

        URI delUrl = URI.create("http://localhost:8080/epics/" + epic.getId());

        HttpRequest request = HttpRequest.newBuilder().uri(delUrl).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
        assertEquals(0, manager.getEpics().size());
    }
}
