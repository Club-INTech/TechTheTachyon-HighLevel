package lowlevel.actuators;

import lowlevel.order.Order;
import orders.OrderWrapper;

/**
 * Un actuator qui n'a que deux états: allumé ou éteint
 *
 * @author jglrxavpok
 */
public class OnOffActuator implements Actuator {
    private OrderWrapper wrapper;
    private Order activateOrder;
    private Order deactivateOrder;

    /**
     * Instancies l'actuateur à deux états
     * @param wrapper le module permettant de communiquer les ordres
     * @param activateOrder l'ordre d'activation
     * @param deactivateOrder l'ordre de désactivation
     */
    public OnOffActuator(OrderWrapper wrapper, Order activateOrder, Order deactivateOrder) {
        this.wrapper = wrapper;
        this.activateOrder = activateOrder;
        this.deactivateOrder = deactivateOrder;
    }

    /**
     * Active l'actuateur et retourne immédiatement sans l'attendre
     */
    public void activate() {
        activate(false);
    }

    /**
     * Active l'actuateur
     * @param waitForSync 'true' si le HL doit attendre la confirmation du LL que l'action est finie, 'false' pour retourner immédiatement la main à l'appelant
     */
    public void activate(boolean waitForSync) {
        wrapper.perform(activateOrder, waitForSync);
    }

    /**
     * Désactive l'actuateur et retourne immédiatement sans l'attendre
     */
    public void deactivate() {
        deactivate(false);
    }

    /**
     * Désactive l'actuateur
     * @param waitForSync 'true' si le HL doit attendre la confirmation du LL que l'action est finie, 'false' pour retourner immédiatement la main à l'appelant
     */
    public void deactivate(boolean waitForSync) {
        wrapper.perform(deactivateOrder, waitForSync);
    }
}
