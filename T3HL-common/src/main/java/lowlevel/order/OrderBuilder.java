package lowlevel.order;

import lowlevel.ServoGroup;
import utils.RobotSide;

/**
 * Classe utilitaire pour créer un ordre
 *
 * @author jglrxavpok
 */
@SuppressWarnings("unchecked cast")
public class OrderBuilder<T extends Order> {

    /**
     * Système auquel cet ordre appartient
     */
    protected String system;

    /**
     * Cet ordre est-il symétrisable?
     */
    private boolean isSided;

    /**
     * Côté impliqué par cet ordre, ou 'null' si aucun
     */
    private RobotSide side;

    /**
     * Initialise la procédure de création d'un ordre
     * @param system le système auquel l'ordre appartient
     * @return une instance d'{@link OrderBuilder} pour créer l'ordre
     */
    public static OrderBuilder<Order> create(String system) {
        OrderBuilder instance = new OrderBuilder();
        instance.system = system;
        return instance;
    }

    /**
     * Côté du robot impliqué
     * @param side Côté du robot
     * @return cet {@link OrderBuilder} pour les enchaînements
     */
    public OrderBuilder<SidedOrder> side(RobotSide side) {
        isSided = true;
        this.side = side;
        return (OrderBuilder<SidedOrder>)this; // ne faites pas ça chez vous les enfants!
    }

    /**
     *
     * @return
     */
    public T on() {
        return terminal("on");
    }

    public T off() {
        return terminal("off");
    }

    public T terminal(String terminal) {
        if(isSided) {
            BuiltSidedOrder original = new BuiltSidedOrder(system, side, terminal);
            BuiltSidedOrder symetrized = new BuiltSidedOrder(system, side.opposite(), terminal);
            original.setSymetrized(symetrized);
            symetrized.setSymetrized(original);
            return (T)original;
        } else {
            return (T)new Order() {
                @Override
                public String toString() {
                    return toLL();
                }

                @Override
                public String toLL() {
                    return system+" "+terminal;
                }
            };
        }
    }

    private OrderBuilder() {}

    public ServoGroupOrder moveServoGroup(ServoGroup servoGroup, float... angles) {
        if(angles.length != servoGroup.count()) {
            throw new IllegalArgumentException("Il faut autant d'angles que de servos dans le bras!");
        }
        if(isSided) {
            BuiltSidedServoGroupOrder original = new BuiltSidedServoGroupOrder(system, side, servoGroup, angles);
            original.symetrized = original.createSymetrized();
            return original;
        } else {
            throw new UnsupportedOperationException("TODO");
        }
    }
}
