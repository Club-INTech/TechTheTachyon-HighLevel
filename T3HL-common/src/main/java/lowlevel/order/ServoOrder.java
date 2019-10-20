package lowlevel.order;

import lowlevel.actuators.Servo;

/**
 * Ordre affectant un servomoteur
 *
 * @author jglrxavpok
 */
public interface ServoOrder extends Order {

    /**
     * Le servomoteur à bouger
     * @return
     */
    Servo servo();

    /**
     * L'angle (en degré) que le servomoteur doit atteindre
     * @return
     */
    float angle();
}
