package embedded;

import ai.AIModule;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import utils.Container;

public class Test_AIModule {

    private Container container;

    private AIModule ai;

    @Ignore
    @Before
    public void setUp() throws Exception {
        container = Container.getInstance("Master");
        ai = container.module(AIModule.class);
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
