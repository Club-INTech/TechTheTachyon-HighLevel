package lowlevel.actuators;

import data.SensorState;
import lowlevel.order.Order;
import orders.OrderWrapper;

/**
 * Représentes un ascenseur
 *
 * @author jglrxavpok
 */
public class ElevatorActuator implements AsyncActuator {

    /**
     * Le module de communication pour les ordres
     */
    private final OrderWrapper wrapper;
    /**
     * Ordre pour monter d'un étage
     */
    private final Order up;
    /**
     * Ordre pour descendre d'un étage
     */
    private final Order down;
    /**
     * Ordre pour monter puis descendre d'un étage (recalibration)
     */
    private final Order updown;
    /**
     * Ordre pour descendre puis monter d'un étage (recalibration)
     */
    private final Order downup;
    /**
     * Permet de déterminer si l'ascenseur est en train de bouger
     */
    private SensorState<Boolean> state;

    public ElevatorActuator(OrderWrapper wrapper, Order up, Order down, Order updown, Order downup, SensorState<Boolean> state) {
        this.wrapper = wrapper;
        this.up = up;
        this.down = down;
        this.updown = updown;
        this.downup = downup;
        this.state = state;
    }

    @Override
    public boolean isFinished() {
        return !state.getData();
    }

    // Méthodes de déplacement

    public void up() {
        up(false);
    }

    public void up(boolean wait) {
        wrapper.perform(up, wait);
    }

    public void down() {
        down(false);
    }

    public void down(boolean wait) {
        wrapper.perform(down, wait);
    }

    public void updown() {
        updown(false);
    }

    public void updown(boolean wait) {
        wrapper.perform(updown, wait);
    }

    public void downup() {
        downup(false);
    }

    public void downup(boolean wait) {
        wrapper.perform(downup, wait);
    }

}
