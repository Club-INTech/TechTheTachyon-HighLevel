package lowlevel.order;

import lowlevel.actuators.ServoGroup;
import utils.RobotSide;

/**
 * Instance de {@link ServoGroupOrder} créée par un {@link OrderBuilder}, qui peut être symétrisée
 *
 * @author jglrxavpok
 */
class BuiltSidedServoGroupOrder implements SidedServoGroupOrder {

    private final String system;
    private final RobotSide side;
    private final ServoGroup group;
    private final float[] angles;
    public SidedOrder symetrized;

    BuiltSidedServoGroupOrder(String system, RobotSide side, ServoGroup group, float[] angles) {
        this.system = system;
        this.side = side;
        this.group = group;
        this.angles = angles;
    }

    @Override
    public ServoGroup group() {
        return group;
    }

    @Override
    public float[] angles() {
        return angles;
    }

    @Override
    public RobotSide side() {
        return side;
    }

    @Override
    public SidedOrder createSymetrized() {
        return new BuiltSidedServoGroupOrder(system, side.opposite(), group.getSymetrized(), angles);
    }

    @Override
    public SidedOrder symetrize() {
        return symetrized;
    }

    @Override
    public String toLL() {
        StringBuilder angleStr = new StringBuilder();
        for(float angle : angles) {
            angleStr.append(angle).append(' ');
        }
        return system+" "+side.toString()+" "+group.id()+" "+angleStr;
    }
}
