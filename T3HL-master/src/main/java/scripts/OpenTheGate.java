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
        double width = 1500;
        double height = 500;
        Vec2 lowerLeft = new VectCartesian(-width/2, 250);
        Vec2 upperLeft = new VectCartesian(-width/2, 250+height);
        Vec2 lowerRight = new VectCartesian(width/2, 250);
        Vec2 upperRight = new VectCartesian(width/2, 250+height);

        robot.setPositionAndOrientation(upperLeft.clone(), 0.0);
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
