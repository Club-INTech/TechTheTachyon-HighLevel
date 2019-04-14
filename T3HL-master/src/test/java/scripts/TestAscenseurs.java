package scripts;

import orders.order.ActuatorsOrder;
import utils.Container;
import utils.container.ContainerException;
import utils.math.Vec2;
import utils.math.VectCartesian;

public class TestAscenseurs extends TestBaseHL {
    @Override
    public void initState(Container container) throws ContainerException {

    }

    @Override
    public void action() {

        while(true) {
            //robot.useActuator(ActuatorsOrder.DESCEND_ASCENSEUR_DROIT_DE_UN_PALET);
            //robot.useActuator(ActuatorsOrder.MONTE_ASCENCEUR_DROIT_DE_UN_PALET);
            //robot.useActuator(ActuatorsOrder.DESCEND_ASCENSEUR_GAUCHE_DE_UN_PALET);
            robot.useActuator(ActuatorsOrder.DESCEND_ASCENSEUR_GAUCHE_DE_UN_PALET);
            robot.waitForLeftElevator();
            robot.useActuator(ActuatorsOrder.MONTE_ASCENCEUR_GAUCHE_DE_UN_PALET);
            robot.waitForLeftElevator();
        }
    }

    @Override
    public Vec2 startPosition() {
        return new VectCartesian(0, 0);
    }

}
