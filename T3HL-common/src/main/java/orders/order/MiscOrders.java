package orders.order;

import lowlevel.order.Order;
import lowlevel.order.OrderBuilder;

/**
 * Ordres "autres" qui ne rentrent pas dans les autres listes
 *
 * @author jglrxavpok
 */
public final class MiscOrders {

    public static final Order Test = OrderBuilder.createSimple("test");
    public static final Order Ping = OrderBuilder.createSimple("ping");

    private MiscOrders() {}
}
