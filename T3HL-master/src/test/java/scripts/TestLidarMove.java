package scripts;

import data.Sick;
import locomotion.UnableToMoveException;
import utils.ConfigData;
import utils.Container;
import utils.container.ContainerException;
import utils.math.Vec2;
import utils.math.VectCartesian;

import java.util.concurrent.TimeUnit;

public class TestLidarMove extends TestBaseHL {

    @Override
    protected void setup(boolean simulationMode) {
        ConfigData.USING_LIDAR.setDefaultValue(true);
        super.setup(simulationMode);
    }

    @Override
    public void initState(Container container) throws ContainerException {
        table.removeFixedObstacleNoReInit(table.getPaletRougeGauche());
        table.removeFixedObstacleNoReInit(table.getPaletVertGauche());
        table.removeFixedObstacleNoReInit(table.getPaletBleuGauche());
        table.updateTableAfterFixedObstaclesChanges();
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
        return new VectCartesian(-750,500);
    }

    @Override
    public void action() throws Exception {
        robot.computeNewPositionAndOrientation(Sick.LOWER_LEFT_CORNER_TOWARDS_0);
        while(true) {
            try {
                robot.followPathTo(new VectCartesian(-750,500));
                TimeUnit.MILLISECONDS.sleep(500);
                robot.followPathTo(new VectCartesian(750,500));
                TimeUnit.MILLISECONDS.sleep(500);
            } catch (UnableToMoveException e) {
                e.printStackTrace();
            }
        }
    }
}
