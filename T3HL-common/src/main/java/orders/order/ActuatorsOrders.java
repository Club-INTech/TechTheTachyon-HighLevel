package orders.order;

import lowlevel.actuators.ServoGroups;
import lowlevel.order.OrderBuilder;
import lowlevel.order.SidedOrder;
import lowlevel.order.SidedServoGroupOrder;
import utils.RobotSide;

/**
 * Liste des ordres liés aux actuateurs.
 *
 * Les noms sont en PascalCase juste par esthétisme.
 *
 * @author jglrxavpok
 */
public final class ActuatorsOrders {

    private static final String PUMPS = "pump";
    private static final String VALVES = "valve";
    private static final String SERVOGROUPS = "servogroup";

    public static final SidedOrder ActivateRightPump = OrderBuilder
            .create(PUMPS)
            .side(RobotSide.RIGHT)
            .on();
    public static final SidedOrder ActivateLeftPump = ActivateRightPump.symetrize();

    public static final SidedOrder DeactivateRightPump = OrderBuilder
            .create(PUMPS)
            .side(RobotSide.RIGHT)
            .off();
    public static final SidedOrder DeactivateLeftPump = DeactivateRightPump.symetrize();

    public static final SidedOrder ActivateRightValve = OrderBuilder
            .create(VALVES)
            .side(RobotSide.RIGHT)
            .on();
    public static final SidedOrder ActivateLeftValve = ActivateRightValve.symetrize();

    public static final SidedOrder DeactivateRightValve = OrderBuilder
            .create(VALVES)
            .side(RobotSide.RIGHT)
            .off();
    public static final SidedOrder DeactivateLeftValve = DeactivateRightValve.symetrize();

    public static final SidedServoGroupOrder BrasDroitToutDroit = (SidedServoGroupOrder) OrderBuilder
            .create(SERVOGROUPS)
            .side(RobotSide.RIGHT)
            .moveServoGroup(ServoGroups.RightArm, 180f, 180f, 180f);
    public static final SidedServoGroupOrder BrasGaucheToutDroit = (SidedServoGroupOrder) BrasDroitToutDroit.symetrize();

    private ActuatorsOrders(){}
}
