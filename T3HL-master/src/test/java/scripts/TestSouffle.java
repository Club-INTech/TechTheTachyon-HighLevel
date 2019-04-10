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
        orderWrapper.useActuator(ActuatorsOrder.DESACTIVE_ELECTROVANNE_DROITE, true);
        orderWrapper.sendString("suck right");
        orderWrapper.sendString("stock right");
        TimeUnit.SECONDS.sleep(1);
        orderWrapper.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_ACCELERATEUR, true);
        TimeUnit.SECONDS.sleep(1);
        orderWrapper.useActuator(ActuatorsOrder.POUSSE_LE_PALET_BRAS_DROIT, true);
        TimeUnit.SECONDS.sleep(1);
        robot.moveLengthwise(-30, false);

        orderWrapper.sendString("suck left");
        orderWrapper.sendString("unsuck right");
        orderWrapper.useActuator(ActuatorsOrder.ACTIVE_ELECTROVANNE_DROITE, true);

        TimeUnit.SECONDS.sleep(1);
        orderWrapper.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_RECULE, true);
        TimeUnit.SECONDS.sleep(1);
        robot.moveLengthwise(30, false);
        TimeUnit.SECONDS.sleep(1);
    }
}
