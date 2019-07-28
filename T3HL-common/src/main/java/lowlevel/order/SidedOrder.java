package lowlevel.order;

import utils.RobotSide;

/**
 * Ordre qui renseigne le côté du robot affectué
 *
 * @author jglrxavpok
 */
public interface SidedOrder extends Order {

    /**
     * Le côté du robot sur lequel cet ordre s'applique
     * @return
     */
    RobotSide side();

    /**
     * Renvoie une version symétrisée de cet ordre. L'instance doit être la même à chaque appel.
     * @return l'ordre symétrisé
     */
    SidedOrder symetrize();

    /**
     * Symétrise cet ordre (gauche &lt;-&gt; droite). Ne devrait être appelée qu'une fois par ordre de préférence pour éviter de créer 3GB d'instances d'Order
     * @return une nouvelle instance de cet ordre, mais symétrisée
     */
    SidedOrder createSymetrized();
}
