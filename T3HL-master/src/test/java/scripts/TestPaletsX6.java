package scripts;

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
    }

    @Override
    public Vec2 startPosition() {
        return new VectCartesian(-1309, 226);
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
