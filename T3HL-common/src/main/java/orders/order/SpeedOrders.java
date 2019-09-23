package orders.order;

import lowlevel.order.Order;
import lowlevel.order.OrderBuilder;

/**
 * Tous les ordres concernant la vitesse
 * @author jglrxavpok
 */
public class SpeedOrders {

    public static final Order SetTranslationSpeed = OrderBuilder.createSimple("maxtr");
    public static final Order SetRotationalSpeed = OrderBuilder.createSimple("maxro");
    public static final Order SetSpeed = OrderBuilder.createSimple("maxtrro");

    private SpeedOrders() {}
}
