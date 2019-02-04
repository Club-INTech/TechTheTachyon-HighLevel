package scripts;

import data.Table;
import pfg.config.Config;
import robot.Robot;
import utils.math.Shape;

public class Goldenium extends Script {

    /**
     * Construit un script
     *
     * @param robot le robot
     * @param table
     */
    protected Goldenium(Robot robot, Table table) {
        super(robot, table);
    }

    @Override
    public void execute(Integer version) {

    }

    @Override
    public Shape entryPosition(Integer version) {
        return null;
    }

    @Override
    public void finalize(Exception e) {

    }

    @Override
    public void updateConfig(Config config) {

    }
}
