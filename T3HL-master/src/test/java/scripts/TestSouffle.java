package scripts;

import orders.order.ActuatorsOrder;
import utils.Container;
import utils.container.ContainerException;
import utils.math.Vec2;
import utils.math.VectCartesian;

import java.util.concurrent.TimeUnit;

public class TestSouffle extends TestBaseHL {
    @Override
    public void initState(Container container) throws ContainerException {

    }

    @Override
    public Vec2 startPosition() {
        return new VectCartesian(0,0);
    }

    @Override
    public void action() throws Exception {
        for (int i = 0; i < 4; i++) {
            robot.turn(Math.PI);
            TimeUnit.MILLISECONDS.sleep(5000);
            robot.turn(-Math.PI);
            TimeUnit.MILLISECONDS.sleep(5000);
        }
    }
}
