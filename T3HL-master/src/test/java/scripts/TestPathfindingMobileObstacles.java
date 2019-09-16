package scripts;

import locomotion.UnableToMoveException;
import utils.HLInstance;
import utils.container.ContainerException;
import utils.math.Vec2;
import utils.math.InternalVectCartesian;

public class TestPathfindingMobileObstacles extends TestBaseHL {
    @Override
    public void initState(HLInstance hl) throws ContainerException {
    }

    @Override
    public Vec2 startPosition() {
        return new InternalVectCartesian(1300, 500);
    }

    @Override
    public void action() throws Exception {
        Vec2 pointA = new InternalVectCartesian(1300, 500);
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
