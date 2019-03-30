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
    public Vec2 startPosition() {
        return new VectCartesian(0,500);
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
                // Attente de 1s avant de redonner les positions
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
