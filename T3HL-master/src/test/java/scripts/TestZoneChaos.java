package scripts;

import utils.Container;
import utils.container.ContainerException;
import utils.math.Vec2;
import utils.math.VectCartesian;

public class TestZoneChaos extends TestScriptBase {
    @Override
    public ScriptNamesMaster getScript() {
        return ScriptNamesMaster.PALETS_ZONE_CHAOS;
    }

    @Override
    public int[] versionsToTest() {
        return new int[]{0};
    }

    @Override
    public void initState(Container container) {
        table.removeFixedObstacle(table.getPaletBleuGauche());
        table.removeFixedObstacle(table.getPaletRougeGauche());
        table.removeFixedObstacle(table.getPaletVertGauche());
    }

    @Override
    public Vec2 startPosition() {
        return new VectCartesian(-730,470);
    }
}
