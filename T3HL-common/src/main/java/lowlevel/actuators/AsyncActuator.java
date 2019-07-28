package lowlevel.actuators;

import utils.container.Module;

/**
 * Représentes un actuateur à action asynchrone (ie le LL doit faire une action en parallèle pour cet actuateur)
 *
 * @author jglrxavpok
 */
public interface AsyncActuator extends Actuator {

    /**
     * Est-ce que l'actuateur a fini?
     * @return
     */
    boolean isFinished();

    /**
     * Est-ce que l'actuateur est en train de bouger?
     * @return
     */
    default boolean isMoving() {
        return !isFinished();
    }

    /**
     * Attends que l'actuator ne bouge plus
     */
    default void waitFor() {
        Module.waitWhileTrue(this::isMoving);
    }
}
