package embedded;

import ai.AIModule;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import utils.HLInstance;

public class Test_AIModule {

    private HLInstance hl;

    private AIModule ai;

    @Ignore
    @Before
    public void setUp() throws Exception {
        hl = HLInstance.getInstance("Master");
        ai = hl.module(AIModule.class);
    }

    @Ignore
    @After
    public void tearDown() {
        hl = null;
        ai = null;
        HLInstance.resetInstance();
    }

    @Test
    public void init() throws Exception {
        ai.start();
        Thread.sleep(1000);
        ai.interrupt();
    }
}
