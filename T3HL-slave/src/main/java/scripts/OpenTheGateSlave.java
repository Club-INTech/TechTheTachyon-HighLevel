package scripts;

import data.XYO;
import locomotion.UnableToMoveException;
import utils.HLInstance;
import utils.Offsets;
import utils.math.Vec2;
import utils.math.VectCartesian;

import static utils.Offsets.ZDD_X_JAUNE;
import static utils.Offsets.ZDD_X_VIOLET;

public class OpenTheGateSlave extends Script {

    /**
     * Construit un script
     */
    protected OpenTheGateSlave(HLInstance hl) {
        super(hl);
    }

    @Override
    public void execute(int version) {
        double offX;
        if (symetry){
            offX = Offsets.get(ZDD_X_VIOLET);
        } else {
            offX = Offsets.get(ZDD_X_JAUNE);
        }
        Vec2 zoneDep = new VectCartesian(1500-191-65+offX-20,1040-300);
        Vec2 lowerLeft = new VectCartesian(650, 300);
        Vec2 lowerRight = new VectCartesian(zoneDep.getX(), 300);
        Vec2 upperLeft = new VectCartesian(650, 1300);
        Vec2 upperRight = new VectCartesian(zoneDep.getX(), 1300);

        while (true) {
            try {
                followPathTo(upperRight, 0);
            } catch (UnableToMoveException e) {
                e.printStackTrace();
            }
            try {
                followPathTo(upperLeft, 0);
            } catch (UnableToMoveException e) {
                e.printStackTrace();
            }
            try {
                followPathTo(lowerLeft, 0);
            } catch (UnableToMoveException e) {
                e.printStackTrace();
            }
            try {
                followPathTo(lowerRight, 0);
            } catch (UnableToMoveException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public Vec2 entryPosition(int version) {
        return XYO.getRobotInstance().getPosition();
    }

    @Override
    public void finalize(Exception e) {

    }
}
