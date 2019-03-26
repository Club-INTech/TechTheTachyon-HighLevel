package scripts;

import org.junit.After;
import org.junit.Before;

public class TestZoneDeDepart extends TestScriptBase {

    @Override
    public ScriptNamesMaster getScript() {
        return ScriptNamesMaster.PALETS_ZONE_DEPART;
    }

    @Override
    public int[] versionsToTest() {
        return new int[] {0};
    }
}
