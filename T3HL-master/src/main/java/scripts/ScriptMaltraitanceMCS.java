package scripts;

import data.SensorState;
import data.Table;
import data.XYO;
import orders.OrderWrapper;
import orders.order.MotionOrder;
import utils.Container;
import utils.container.Module;
import utils.math.Vec2;

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
    protected ScriptMaltraitanceMCS(Container container, Table table) {
        super(container);
        this.orderWrapper = orderWrapper;
    }

    @Override
    public void execute(int version) {
        Vec2 target = XYO.getRobotInstance().getPosition();
        while (true) {
            float randX = (float) (Math.random()*2-1)*0.1f;
            float randY = (float) (Math.random()*2-1)*0.1f;
            SensorState.MOVING.setData(true);
            orderWrapper.sendString(String.format(Locale.US, "%s %f %f", MotionOrder.MOVE_TO_POINT.getOrderStr(), target.getX()+randX, target.getY()+randY));
            Module.waitWhileTrue(SensorState.MOVING::getData);
        }
    }

    @Override
    public Vec2 entryPosition(int version) {
        return XYO.getRobotInstance().getPosition();
    }

    @Override
    public void finalize(Exception e) {

    }
}
