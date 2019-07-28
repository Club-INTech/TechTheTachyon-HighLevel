package lowlevel.order;

import lowlevel.ServoGroup;

public interface ServoGroupOrder extends Order {

    ServoGroup group();

    float[] angles();
}
