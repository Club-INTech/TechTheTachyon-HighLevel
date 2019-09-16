package scripts;

import utils.HLInstance;
import utils.container.ContainerException;
import utils.math.Vec2;
import utils.math.InternalVectCartesian;

public class TestTimeout extends TestBaseHL {
    @Override
    public void initState(HLInstance hl) throws ContainerException {

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

    @Override
    public Vec2 startPosition() {
        return new InternalVectCartesian(0, 0);
    }

}
