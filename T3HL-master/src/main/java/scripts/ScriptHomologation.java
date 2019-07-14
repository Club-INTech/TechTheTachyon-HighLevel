package scripts;

import locomotion.UnableToMoveException;
import utils.Container;
import utils.math.Vec2;
import utils.math.VectCartesian;

public class ScriptHomologation extends Script {

    private static final int DISTANCE_INTERPALET = 300;

    private int xEntry = 1500-191-65+20;//1350;
    private int yEntry = 430;

    public ScriptHomologation(Container container) {
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
            robot.moveLengthwise(DISTANCE_INTERPALET*2, false);
            turn(Math.PI);
            robot.moveLengthwise(DISTANCE_INTERPALET*2, false);
            turn(-Math.PI/2);
            robot.moveLengthwise(DISTANCE_INTERPALET, false);
            turn(0);
            robot.moveLengthwise(DISTANCE_INTERPALET*2, false);


        } catch (UnableToMoveException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public Vec2 entryPosition(int version) {
        return new VectCartesian(xEntry, yEntry);
    }

    @Override
    public void finalize(Exception e) {
    }
}
