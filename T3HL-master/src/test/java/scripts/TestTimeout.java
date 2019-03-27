package scripts;

import utils.Container;
import utils.container.ContainerException;

public class TestTimeout extends TestBaseHL {
    @Override
    public void initState(Container container) throws ContainerException {

    }

    @Override
    public void action() {
        try {
            // débrancher pendant ce temps
            // un timeout devrait arriver
            Thread.sleep(20000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // TODO: test qui peut échouer
    }
}
