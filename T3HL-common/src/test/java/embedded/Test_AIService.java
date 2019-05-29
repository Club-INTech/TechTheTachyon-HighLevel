package embedded;

import ai.AIService;
import data.controlers.Listener;
import org.junit.*;
import utils.Container;

public class Test_AIService {

    private Container container;

    private AIService ai;

    @Ignore
    @Before
    public void setUp() throws Exception {
        container = Container.getInstance("Master");
        ai = container.getService(AIService.class);
    }

    @Ignore
    @After
    public void tearDown() {
        container = null;
        ai = null;
        Container.resetInstance();
    }

    @Test
    public void init() throws Exception {
        ai.start();
        Thread.sleep(1000);
        ai.interrupt();
    }
}
