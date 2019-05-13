package scripts;

import data.Table;
import data.XYO;
import orders.OrderWrapper;
import orders.order.MotionOrder;
import robot.Master;
import robot.Robot;
import utils.math.Vec2;
import utils.math.VectCartesian;

import java.util.Locale;

/**
 * Maltraite le MCS
 */
public class ScriptMaltraitanceMCS extends Script {

    private OrderWrapper orderWrapper;

    /**
     * Construit un script
     *
     * @param robot le robot
     * @param table
     */
    protected ScriptMaltraitanceMCS(Master robot, OrderWrapper orderWrapper, Table table) {
        super(robot, table);
        this.orderWrapper = orderWrapper;
    }

    @Override
    public void execute(Integer version) {
        Vec2 target = XYO.getRobotInstance().getPosition();
        while (true) {
            float randX = (float) (Math.random()*0.1);
            float randY = (float) (Math.random()*0.1);
            orderWrapper.sendString(String.format(Locale.US, "%s %f %f", MotionOrder.MOVE_TO_POINT.getOrderStr(), target.getX()+randX, target.getY()+randY));
        }
    }

    @Override
    public Vec2 entryPosition(Integer version) {
        return XYO.getRobotInstance().getPosition();
    }

    @Override
    public void finalize(Exception e) {

    }
}
