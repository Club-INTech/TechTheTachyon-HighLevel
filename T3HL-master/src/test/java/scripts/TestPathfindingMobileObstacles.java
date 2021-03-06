package scripts;

import data.XYO;
import locomotion.UnableToMoveException;
import utils.Container;
import utils.container.ContainerException;
import utils.math.Vec2;
import utils.math.VectCartesian;

public class TestPathfindingMobileObstacles extends TestBaseHL {
    @Override
    public void initState(Container container) throws ContainerException {
    }

    @Override
    public Vec2 startPosition() {
        return new VectCartesian(1300, 500);
    }

    @Override
    public void action() throws Exception {
        Vec2 pointA = new VectCartesian(1300, 500);
        Vec2 pointB = pointA.symetrizeVector();
        for (int i = 0; i < 100; i++) {
            try {
                robot.followPathTo(pointA);
                robot.followPathTo(pointB);
            } catch (UnableToMoveException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public double startOrientation() {
        return super.startOrientation();
    }
}
