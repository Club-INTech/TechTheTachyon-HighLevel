package scripts;

import orders.order.ActuatorsOrder;
import orders.order.ElevatorOrders;
import utils.HLInstance;
import utils.container.ContainerException;
import utils.math.Vec2;
import utils.math.InternalVectCartesian;

public class TestAscenseurs extends TestBaseHL {
    @Override
    public void initState(HLInstance hl) throws ContainerException {

    }

    @Override
    public void action() {
        //robot.computeNewPositionAndOrientation(Sick.LOWER_LEFT_CORNER_TOWARDS_PI);
       // robot.useActuator(ActuatorsOrder.DESCEND_ASCENSEUR_DROIT_DE_UN_PALET);
        while(true) {
            robot.perform(ElevatorOrders.LowerRightElevator);
            robot.waitForRightElevator();
            robot.perform(ElevatorOrders.RaiseRightElevator);
            robot.waitForRightElevator();
            robot.perform(ElevatorOrders.RaiseRightElevator);
            robot.waitForRightElevator();
            //robot.useActuator(ActuatorsOrder.MONTE_ASCENCEUR_DROIT_DE_UN_PALET);

            //robot.useActuator(ActuatorsOrder.DESCEND_ASCENSEUR_GAUCHE_DE_UN_PALET);
           // robot.useActuator(ActuatorsOrder.DESCEND_ASCENSEUR_GAUCHE_DE_UN_PALET);
            //robot.waitForLeftElevator();
            //robot.useActuator(ActuatorsOrder.MONTE_ASCENCEUR_GAUCHE_DE_UN_PALET);
            //robot.waitForLeftElevator();
        }
    }

    @Override
    public double startOrientation() {
        return Math.PI;
    }

    @Override
    public Vec2 startPosition() {
        return new InternalVectCartesian(0, 0);
    }

}
