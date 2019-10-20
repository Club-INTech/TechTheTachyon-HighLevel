package orders.order;

import lowlevel.order.Order;
import lowlevel.order.OrderBuilder;
import lowlevel.order.OrderWithArgument;
import utils.communication.Formatting;

/**
 * Tous les ordres concernant la vitesse
 * @author jglrxavpok
 */
public class SpeedOrders {

    public static final OrderWithArgument SetTranslationSpeed = OrderBuilder.createWithArgs("maxtr", Formatting.FLOAT5);
    public static final OrderWithArgument SetRotationalSpeed = OrderBuilder.createWithArgs("maxro", Formatting.FLOAT5);
    public static final OrderWithArgument SetSpeed = OrderBuilder.createWithArgs("maxtrro", Formatting.FLOAT5);

    private SpeedOrders() {}
}
