package scripts;

import utils.ConfigData;
import utils.Container;
import utils.container.ContainerException;
import utils.math.Vec2;
import utils.math.VectCartesian;

public class TestLidarShow extends TestBaseHL {

    @Override
    protected void setup(boolean simulationMode) {
        ConfigData.USING_LIDAR.setDefaultValue(true);
        super.setup(simulationMode);
    }

    @Override
    public void initState(Container container) throws ContainerException {
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
        Thread.sleep(1600000);
    }
}
