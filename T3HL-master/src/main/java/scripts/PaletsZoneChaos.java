package scripts;

import data.Table;
import locomotion.UnableToMoveException;
import orders.Speed;
import orders.order.ActuatorsOrder;
import pfg.config.Config;
import robot.Master;
import robot.Robot;
import utils.ConfigData;
import utils.math.Circle;
import utils.math.Shape;
import utils.math.VectCartesian;

// TODO


public class PaletsZoneChaos extends Script{

    private int xEntry = 1375;
    private int yEntry = 1800 +  (int) ConfigData.ROBOT_RAY.getDefaultValue() ;

    public PaletsZoneChaos(Master robot, Table table) {
        super(robot, table);
    }

    @Override
    public void execute(Integer version) {

    }

    @Override
    public Shape entryPosition(Integer version) {
        return new Circle(new VectCartesian(xEntry, yEntry), 5);
    }

    @Override
    public void finalize(Exception e) { }

    @Override
    public void updateConfig(Config config) { }
}
