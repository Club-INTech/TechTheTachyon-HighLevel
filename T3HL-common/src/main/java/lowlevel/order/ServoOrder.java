package lowlevel.order;

import lowlevel.Servo;

public interface ServoOrder extends Order {

    Servo servo();

    float angle();
}
