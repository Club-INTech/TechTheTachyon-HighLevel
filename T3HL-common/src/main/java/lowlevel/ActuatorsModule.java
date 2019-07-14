package lowlevel;

import data.SensorState;
import orders.OrderWrapper;
import orders.order.ActuatorsOrder;
import utils.container.Module;

/**
 * Module contenant la liste des actuateurs disponibles
 *
 * @author jglrxavpok
 */
public class ActuatorsModule implements Module {

    private OrderWrapper wrapper;

    public final OnOffActuator LEFT_PUMP;
    public final OnOffActuator RIGHT_PUMP;
    public final OnOffActuator LEFT_VALVE;
    public final OnOffActuator RIGHT_VALVE;
    public final ElevatorActuator LEFT_ELEVATOR;
    public final ElevatorActuator RIGHT_ELEVATOR;

    public ActuatorsModule(OrderWrapper wrapper) {
        this.wrapper = wrapper;

        // Initialisation des différents actuateurs
        LEFT_PUMP = new OnOffActuator(wrapper, ActuatorsOrder.ACTIVE_LA_POMPE_GAUCHE, ActuatorsOrder.DESACTIVE_LA_POMPE_GAUCHE);
        RIGHT_PUMP = new OnOffActuator(wrapper, ActuatorsOrder.ACTIVE_LA_POMPE_DROITE, ActuatorsOrder.DESACTIVE_LA_POMPE_DROITE);
        LEFT_VALVE = new OnOffActuator(wrapper, ActuatorsOrder.ACTIVE_ELECTROVANNE_GAUCHE, ActuatorsOrder.DESACTIVE_ELECTROVANNE_GAUCHE);
        RIGHT_VALVE = new OnOffActuator(wrapper, ActuatorsOrder.ACTIVE_ELECTROVANNE_DROITE, ActuatorsOrder.DESACTIVE_ELECTROVANNE_DROITE);

        LEFT_ELEVATOR = new ElevatorActuator(wrapper, ActuatorsOrder.MONTE_ASCENCEUR_GAUCHE_DE_UN_PALET, ActuatorsOrder.DESCEND_ASCENSEUR_GAUCHE_DE_UN_PALET,
                ActuatorsOrder.MONTE_DESCEND_ASCENCEUR_GAUCHE_DE_UN_PALET, ActuatorsOrder.DESCEND_MONTE_ASCENCEUR_GAUCHE_DE_UN_PALET, SensorState.LEFT_ELEVATOR_MOVING);
        RIGHT_ELEVATOR = new ElevatorActuator(wrapper, ActuatorsOrder.MONTE_ASCENCEUR_DROIT_DE_UN_PALET, ActuatorsOrder.DESCEND_ASCENSEUR_DROIT_DE_UN_PALET,
                ActuatorsOrder.MONTE_DESCEND_ASCENCEUR_DROIT_DE_UN_PALET, ActuatorsOrder.DESCEND_MONTE_ASCENCEUR_DROIT_DE_UN_PALET, SensorState.RIGHT_ELEVATOR_MOVING);
    }
}
