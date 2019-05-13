package scripts;

import data.Table;
import data.XYO;
import robot.Master;
import utils.math.Vec2;
import utils.math.VectCartesian;

public class ScriptTestPositions extends Script {
    /**
     * Construit un script
     *
     * @param robot le robot
     * @param table
     */
    protected ScriptTestPositions(Master robot, Table table) {
        super(robot, table);
    }

    @Override
    public void execute(Integer version) {
        Vec2 pointA = new VectCartesian(-500, 500);
        Vec2 pointB = new VectCartesian(500, 500);
        while(true) {
            try {
                robot.followPathTo(pointA);
                robot.followPathTo(pointB);
            } catch (Exception e) {
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
