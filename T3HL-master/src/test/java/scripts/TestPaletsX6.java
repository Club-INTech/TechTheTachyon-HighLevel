package scripts;

import data.Sick;
import data.Table;
import utils.Container;
import utils.container.ContainerException;
import utils.math.Vec2;
import utils.math.VectCartesian;

public class TestPaletsX6 extends TestScriptBase {

    @Override
    public void initState(Container container) throws ContainerException {
        Table table = container.getService(Table.class);
        table.removeAllChaosObstacles();
        table.removeFixedObstacle(table.getPaletBleuGauche());
        table.removeFixedObstacle(table.getPaletRougeGauche());
        table.removeFixedObstacle(table.getPaletVertGauche());

        table.removeFixedObstacle(table.getPaletBleuDroite());
        table.removeFixedObstacle(table.getPaletRougeDroite());
        table.removeFixedObstacle(table.getPaletVertDroite());
    }

    @Override
    public void action() {
        robot.computeNewPositionAndOrientation(Sick.LOWER_LEFT_CORNER_TOWARDS_0);
        super.action();
    }

    @Override
    public double startOrientation() {
        return 0.0;
    }

    @Override
    public Vec2 startPosition() {
        return new VectCartesian(1309, 226);
    }

    @Override
    public ScriptNamesMaster getScript() {
        return ScriptNamesMaster.PALETS6;
    }

    @Override
    public int[] versionsToTest() {
        return new int[] {0};
    }
}
