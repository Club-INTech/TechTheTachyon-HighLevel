package scripts;

import locomotion.UnableToMoveException;
import utils.Container;
import utils.math.Vec2;

public class ScriptHomologationSlave extends Script {
    private int xEntry = 1500 -297;
    private int yEntry = 400;
    private static final int DISTANCE_INTERPALET = 300;

    public ScriptHomologationSlave(Container container) {
        super(container);
    }
    @Override
    public void execute(int version) {
        try {
            turn(Math.PI/2);
            robot.moveLengthwise(DISTANCE_INTERPALET*2, false);
            turn(Math.PI);
            robot.moveLengthwise(DISTANCE_INTERPALET*3, false);
            turn(-Math.PI/2);
            robot.moveLengthwise(DISTANCE_INTERPALET, false);
            turn(0);
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
