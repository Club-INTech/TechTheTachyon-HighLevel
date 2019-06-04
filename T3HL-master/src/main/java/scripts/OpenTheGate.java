package scripts;

import data.Table;
import data.XYO;
import locomotion.UnableToMoveException;
import robot.Master;
import utils.math.Vec2;
import utils.math.VectCartesian;

public class OpenTheGate extends Script {
    /**
     * Construit un script
     *
     * @param robot le robot
     * @param table
     */
    protected OpenTheGate(Master robot, Table table) {
        super(robot, table);
    }

    @Override
    public void execute(Integer version) {
        Vec2 lowerLeft = new VectCartesian(450, 350);
        Vec2 lowerRight = new VectCartesian(1100, 350);
        Vec2 upperLeft = new VectCartesian(450, 1250);
        Vec2 upperRight = new VectCartesian(1100, 1250);

        while (true) {
            try {
                robot.followPathTo(lowerLeft);
                robot.followPathTo(lowerRight);
                robot.followPathTo(upperRight);
                robot.followPathTo(upperLeft);
            } catch (UnableToMoveException e) {
                e.printStackTrace();
            }
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
