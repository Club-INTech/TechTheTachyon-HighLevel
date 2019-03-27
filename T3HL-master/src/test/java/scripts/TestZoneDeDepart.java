package scripts;

import utils.Container;

public class TestZoneDeDepart extends TestScriptBase {

    @Override
    public void initState(Container container) {
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
}
