import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TestGUI {
    @Test
    public void TestCreateLogFile() {
        GUI gui = new GUI();
        gui.createLogFile("test");
        assertNotNull(gui);
    }
}
