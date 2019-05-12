package scripts;

import data.Table;
import data.XYO;
import robot.Robot;
import utils.math.Circle;
import utils.math.Shape;

public class ScriptTestPositions extends Script {
    /**
     * Construit un script
     *
     * @param robot le robot
     * @param table
     */
    protected ScriptTestPositions(Robot robot, Table table) {
        super(robot, table);
    }

    @Override
    public void execute(Integer version) {
        while(true) {
            try {
                robot.turn(Math.PI);
                robot.moveLengthwise(1000, false);
                robot.turn(0);
                robot.moveLengthwise(1000, false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public Shape entryPosition(Integer version) {
        return new Circle(XYO.getRobotInstance().getPosition(), 100);
    }

    @Override
    public void finalize(Exception e) {

    }
}
