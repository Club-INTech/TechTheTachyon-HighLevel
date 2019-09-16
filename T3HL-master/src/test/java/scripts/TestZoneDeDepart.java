package scripts;

import utils.HLInstance;
import utils.math.Vec2;
import utils.math.InternalVectCartesian;

public class TestZoneDeDepart extends TestScriptBase {

    @Override
    public void initState(HLInstance hl) {
        // NOP
    }

    @Override
    public ScriptNamesMaster getScript() {
        return ScriptNamesMaster.PALETS_ZONE_DEPART;
    }

    @Override
    public int[] versionsToTest() {
        return new int[] {0};
    }

    @Override
    public Vec2 startPosition() {
        return new InternalVectCartesian(730, 470);
    }

}
