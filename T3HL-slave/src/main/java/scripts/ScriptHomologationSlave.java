package scripts;

import data.Table;
import locomotion.UnableToMoveException;
import robot.Slave;
import utils.math.Vec2;

public class ScriptHomologationSlave extends Script {
    private int xEntry = 1500 -297;
    private int yEntry = 400;
    private static final int DISTANCE_INTERPALET = 300;

    public ScriptHomologationSlave(Slave robot, Table table){
        super(robot, table);
    }
    @Override
    public void execute(int version) {
        try {
            robot.turn(Math.PI/2);
            robot.moveLengthwise(DISTANCE_INTERPALET*2, false);
            robot.turn(Math.PI);
            robot.moveLengthwise(DISTANCE_INTERPALET*3, false);
            robot.turn(-Math.PI/2);
            robot.moveLengthwise(DISTANCE_INTERPALET, false);
            robot.turn(0);
            robot.moveLengthwise(DISTANCE_INTERPALET*2+150, false);
        } catch (UnableToMoveException e) {
            e.printStackTrace();
        }


    }

    @Override
    public Vec2 entryPosition(int version) {
        return null;
    }

    @Override
    public void finalize(Exception e) {

    }

}
