package lowlevel.actuators;

import data.SensorState;
import orders.OrderWrapper;
import orders.order.ActuatorsOrder;
import orders.order.ActuatorsOrders;
import utils.container.Module;

/**
 * Module contenant la liste des actuateurs disponibles
 *
 * @author jglrxavpok
 */
public class ActuatorsModule implements Module {

    private OrderWrapper wrapper;

    public final OnOffActuator leftPump;
    public final OnOffActuator rightPump;
    public final OnOffActuator leftValve;
    public final OnOffActuator rightValve;
    public final ElevatorActuator leftElevator;
    public final ElevatorActuator rightElevator;

    public ActuatorsModule(OrderWrapper wrapper) {
        this.wrapper = wrapper;

        // Initialisation des différents actuateurs
        leftPump = new OnOffActuator(wrapper, ActuatorsOrders.ActivateLeftPump, ActuatorsOrders.DeactivateLeftPump);
        rightPump = new OnOffActuator(wrapper, ActuatorsOrders.ActivateRightPump, ActuatorsOrders.DeactivateRightPump);
        leftValve = new OnOffActuator(wrapper, ActuatorsOrders.ActivateLeftValve, ActuatorsOrders.DeactivateLeftValve);
        rightValve = new OnOffActuator(wrapper, ActuatorsOrders.ActivateRightValve, ActuatorsOrders.DeactivateRightValve);

        leftElevator = new ElevatorActuator(wrapper, ActuatorsOrder.MONTE_ASCENCEUR_GAUCHE_DE_UN_PALET, ActuatorsOrder.DESCEND_ASCENSEUR_GAUCHE_DE_UN_PALET,
                ActuatorsOrder.MONTE_DESCEND_ASCENCEUR_GAUCHE_DE_UN_PALET, ActuatorsOrder.DESCEND_MONTE_ASCENCEUR_GAUCHE_DE_UN_PALET, SensorState.LEFT_ELEVATOR_MOVING);
        rightElevator = new ElevatorActuator(wrapper, ActuatorsOrder.MONTE_ASCENCEUR_DROIT_DE_UN_PALET, ActuatorsOrder.DESCEND_ASCENSEUR_DROIT_DE_UN_PALET,
                ActuatorsOrder.MONTE_DESCEND_ASCENCEUR_DROIT_DE_UN_PALET, ActuatorsOrder.DESCEND_MONTE_ASCENCEUR_DROIT_DE_UN_PALET, SensorState.RIGHT_ELEVATOR_MOVING);
    }
}
