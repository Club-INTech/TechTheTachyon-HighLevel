package scripts;

import orders.SymmetrizedActuatorOrderMap;
import orders.order.ActuatorsOrder;
import utils.HLInstance;
import utils.container.ContainerException;
import utils.math.Vec2;
import utils.math.VectCartesian;

import java.util.concurrent.TimeUnit;

/**
 * Test du recalage avec les SICK
 */
public class TestPositions extends TestBaseHL {

    @Override
    protected void setup(boolean simulationMode) {
        super.setup(simulationMode);
    }

    @Override
    public void initState(HLInstance hl) throws ContainerException {

    }

    @Override
    public double startOrientation() {
        return Math.PI;
    }

    @Override
    public Vec2 startPosition() {
        // on s'en fiche de la position de départ
        return new VectCartesian(0,0);
    }

    @Override
    public void action() throws InterruptedException, ContainerException {
        SymmetrizedActuatorOrderMap symetry = hl.module(SymmetrizedActuatorOrderMap.class);
        for (int i = 0; i < 1000; i++) {
            ActuatorsOrder order = ActuatorsOrder.ARM_ORDERS[(int)(Math.random()*(ActuatorsOrder.ARM_ORDERS.length-1))];
            //robot.useActuator(order);
            int nbr = (int) (Math.random()*3+4);
            orderWrapper.sendString("XLm "+nbr+" 180");
            TimeUnit.MILLISECONDS.sleep(250);
            orderWrapper.sendString("XLm "+nbr+" 200");
            TimeUnit.MILLISECONDS.sleep(250);
//            robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_ASCENSEUR);
            //robot.useActuator((ActuatorsOrder) symetry.getSymmetrizedActuatorOrder(order), true);
        }
    }
}
