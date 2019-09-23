package orders.order;

import lowlevel.order.Order;
import lowlevel.order.OrderBuilder;

/**
 * Ordres utilisés pour Montlhery et pour tester l'asservissement.
 */
public class MontlheryOrders {

    public static final Order Montlhery = OrderBuilder.createSimple("montlhery");
    public static final Order GoForward = OrderBuilder.createSimple("av");
    public static final Order GoBackwards = OrderBuilder.createSimple("rc");
    public static final Order Left = OrderBuilder.createSimple("tg");
    public static final Order Right = OrderBuilder.createSimple("td");
    public static final Order Stop = OrderBuilder.createSimple("sstop");
    public static final Order StopTranslation = OrderBuilder.createSimple("trstop");
    public static final Order StopRotation = OrderBuilder.createSimple("rostop");
    public static final Order MaxTranslationSpeed = OrderBuilder.createSimple("maxtr");
    public static final Order MaxRotationSpeed = OrderBuilder.createSimple("maxro");

    private MontlheryOrders() {}
}
