package lowlevel.order;

import lowlevel.actuators.ServoGroup;

/**
 * Instance de {@link ServoGroupOrder} créée par un {@link OrderBuilder}
 *
 * @author jglrxavpok
 */
class BuiltServoGroupOrder implements ServoGroupOrder {

    private final String system;
    private final ServoGroup group;
    private final float[] angles;

    BuiltServoGroupOrder(String system, ServoGroup group, float[] angles) {
        this.system = system;
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
    public String toLL() {
        StringBuilder angleStr = new StringBuilder();
        for(float angle : angles) {
            angleStr.append(angle).append(' ');
        }
        return system+" "+group.id()+" "+angleStr;
    }
}
