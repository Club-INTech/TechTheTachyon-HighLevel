package scripts;

public class TestPaletsX6 extends TestScriptBase {

    @Override
    public ScriptNamesMaster getScript() {
        return ScriptNamesMaster.PALETS6;
    }

    @Override
    public int[] versionsToTest() {
        return new int[] {0,1,2,3,4,5};
    }
}
