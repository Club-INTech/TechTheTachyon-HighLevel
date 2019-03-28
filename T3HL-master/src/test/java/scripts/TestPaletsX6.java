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
    }

    @Override
    public Vec2 startPosition() {
        return new VectCartesian(-730, 470);
    }

    @Override
    public ScriptNamesMaster getScript() {
        return ScriptNamesMaster.PALETS6;
    }

    @Override
    public int[] versionsToTest() {
        return new int[] {0,1,2,3,4,5};
    }
}
