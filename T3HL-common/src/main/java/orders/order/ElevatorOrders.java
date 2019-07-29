package orders.order;

import lowlevel.order.OrderBuilder;
import lowlevel.order.SidedElevatorOrderWrapper;
import lowlevel.order.SidedOrder;
import utils.RobotSide;

public final class ElevatorOrders {

    private static final String ELEVATORS = "elevator";

    public static SidedOrder RaiseRightElevator = new SidedElevatorOrderWrapper(OrderBuilder
            .create(ELEVATORS)
            .side(RobotSide.RIGHT)
            .terminal("up"));
    public static SidedOrder RaiseLeftElevator = RaiseRightElevator.symetrize();

    public static SidedOrder LowerRightElevator = new SidedElevatorOrderWrapper(OrderBuilder
            .create(ELEVATORS)
            .side(RobotSide.RIGHT)
            .terminal("down"));
    public static SidedOrder LowerLeftElevator = LowerRightElevator.symetrize();

    public static SidedOrder RaiseThenLowerRightElevator = new SidedElevatorOrderWrapper(OrderBuilder
            .create(ELEVATORS)
            .side(RobotSide.RIGHT)
            .terminal("updown"));
    public static SidedOrder RaiseThenLowerLeftElevator = RaiseThenLowerRightElevator.symetrize();

    public static SidedOrder LowerThenRaiseRightElevator = new SidedElevatorOrderWrapper(OrderBuilder.
            create(ELEVATORS)
            .side(RobotSide.RIGHT)
            .terminal("downup"));
    public static SidedOrder LowerThenRaiseLeftElevator = LowerThenRaiseRightElevator.symetrize();
}
