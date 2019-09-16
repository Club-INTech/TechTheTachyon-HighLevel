package scripts;

import locomotion.UnableToMoveException;
import utils.HLInstance;
import utils.math.Vec2;
import utils.math.InternalVectCartesian;

public class ScriptHomologation extends Script {

    private static final int DISTANCE_INTERPALET = 300;

    private int xEntry = 1500-191-65+20;//1350;
    private int yEntry = 430;

    public ScriptHomologation(HLInstance hl) {
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
            moveLengthwise(DISTANCE_INTERPALET*2, false);
            turn(Math.PI);
            moveLengthwise(DISTANCE_INTERPALET*2, false);
            turn(-Math.PI/2);
            moveLengthwise(DISTANCE_INTERPALET, false);
            turn(0);
            moveLengthwise(DISTANCE_INTERPALET*2, false);


        } catch (UnableToMoveException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public Vec2 entryPosition(int version) {
        return new InternalVectCartesian(xEntry, yEntry);
    }

    @Override
    public void finalize(Exception e) {
    }
}
