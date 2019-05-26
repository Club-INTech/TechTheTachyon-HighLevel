package scripts;

import data.Table;
import locomotion.UnableToMoveException;
import orders.Speed;
import robot.Master;
import utils.Container;
import utils.math.Vec2;
import utils.math.VectCartesian;

public class HomologationMaster extends Script{

    private final int xEntry = 0;
    private final int yEntry = 0;

    private final Container container;

    private boolean symetry = false;


    public HomologationMaster(Master robot, Table table, Container container){
        super(robot, table);
        this.container = container;
    }

    public void execute(Integer version) {
        try {
            robot.turn(Math.PI);
            robot.moveLengthwise(1000, false);
            robot.turn(Math.PI/2);
            robot.moveLengthwise(300, false);
            robot.turn(0);
            robot.moveLengthwise(1000, false);

        } catch (UnableToMoveException e) {
            e.printStackTrace();
        }

    }

    @Override
    public Vec2 entryPosition(Integer version) {
        return new VectCartesian(xEntry, yEntry);
    }

    @Override
    public void finalize(Exception e) {
        robot.setSpeed(Speed.DEFAULT_SPEED);
    }
}
