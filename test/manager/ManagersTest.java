package manager;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class ManagersTest {
    @Test
    public void returnedObjectCannotNull() throws IOException {
        assertNotNull(Managers.getDefault());
        assertNotNull(Managers.getDefaultHistory());
        assertNotNull(Managers.getDefaultManagerFile(File.createTempFile("test", ".csv")));
    }
}