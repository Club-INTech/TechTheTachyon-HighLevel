package scripts;

import data.Table;
import data.table.MobileCircularObstacle;
import utils.ConfigData;
import utils.Container;
import utils.container.ContainerException;
import utils.math.Vec2;
import utils.math.VectCartesian;

import java.util.List;

public class TestLidar extends TestBaseHL {

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
        return Math.PI/2;
    }

    @Override
    public Vec2 startPosition() {
        // coin bas-droit de la table
        return new VectCartesian(table.getLength(),0);
    }

    @Override
    public void action() throws Exception {
        final int testCount = 60;
        Table table = container.getService(Table.class);

        for (int i = 0; i < testCount; i++) {
            List<MobileCircularObstacle> mobileObstacles = table.getMobileObstacles();
            synchronized (mobileObstacles) {
                System.out.println("[=== Liste des obstacles ===]");
                for(MobileCircularObstacle mobileObstacle : mobileObstacles) {
                    System.out.println(mobileObstacle);
                }
                System.out.println("[=== === === === === === ===]");
            }
            try {
                // Attente de 5s avant de redonner les positions
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
