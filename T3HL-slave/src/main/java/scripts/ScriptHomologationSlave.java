package scripts;

import locomotion.UnableToMoveException;
import utils.HLInstance;
import utils.math.Vec2;
import utils.math.VectCartesian;

public class ScriptHomologationSlave extends Script {
    private int xEntry = 297;
    private int yEntry = 400;
    private static final int DISTANCE_INTERPALET = 300;

    public ScriptHomologationSlave(HLInstance hl) {
        super(hl);
    }
    @Override
    public void execute(int version) {
        try {
            turn(Math.PI/2);
            moveLengthwise(DISTANCE_INTERPALET*2, false);
            turn(Math.PI);
            moveLengthwise(DISTANCE_INTERPALET*3, false);
            turn(-Math.PI/2);
            moveLengthwise(DISTANCE_INTERPALET, false);
            turn(0);
            moveLengthwise(DISTANCE_INTERPALET*2+150, false);
        } catch (UnableToMoveException e) {
            e.printStackTrace();
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
