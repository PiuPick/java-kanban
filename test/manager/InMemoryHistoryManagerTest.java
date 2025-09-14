package manager;

import org.junit.jupiter.api.BeforeEach;

class InMemoryHistoryManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    private static TaskManager taskManager;

    @Override
    protected InMemoryTaskManager createTaskManager() {
        return new InMemoryTaskManager();
    }

    @BeforeEach
    public void beforeEach() {
        taskManager = createTaskManager();
    }
}