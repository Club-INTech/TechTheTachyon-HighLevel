package scripts;

import data.Sick;
import locomotion.UnableToMoveException;
import utils.ConfigData;
import utils.HLInstance;
import utils.container.ContainerException;
import utils.math.Vec2;
import utils.math.InternalVectCartesian;

import java.util.concurrent.TimeUnit;

public class TestLidarMove extends TestBaseHL {

    @Override
    protected void setup(boolean simulationMode) {
        hl.getConfig().override(ConfigData.USING_LIDAR, true);
        super.setup(simulationMode);
    }

    @Override
    public void initState(HLInstance hl) throws ContainerException {
        table.removeTemporaryObstacle(table.getPaletRougeGauche());
        table.removeTemporaryObstacle(table.getPaletVertGauche());
        table.removeTemporaryObstacle(table.getPaletBleuGauche());
        table.removeAllChaosObstacles();
    }

    @Override
    public double startOrientation() {
        // coin droit de la table
        //return Math.PI;
        return 0.0;
    }

    @Override
    public Vec2 startPosition() {
        // coin bas-droit de la table
//        return new VectCartesian(table.getLength()/2-215,1000);
        return new InternalVectCartesian(-750,500);
    }

    @Override
    public void action() throws Exception {
        robot.computeNewPositionAndOrientation(Sick.LOWER_LEFT_CORNER_TOWARDS_0);
        while(true) {
            try {
                robot.followPathTo(new InternalVectCartesian(-750,500));
                TimeUnit.MILLISECONDS.sleep(500);
                robot.followPathTo(new InternalVectCartesian(750,500));
                TimeUnit.MILLISECONDS.sleep(500);
            } catch (UnableToMoveException e) {
                e.printStackTrace();
            }
        }
    }
}
