package lowlevel.order;

import lowlevel.actuators.ServoGroup;

/**
 * Ordre affectant un groupe de servomoteurs
 *
 * @author jglrxavpok
 */
public interface ServoGroupOrder extends Order {

    /**
     * Le groupe de servomoteurs affecté
     * @return
     */
    ServoGroup group();

    /**
     * Les angles (en degrés) que les servomoteurs doivent atteindre
     * @return
     */
    float[] angles();
}
