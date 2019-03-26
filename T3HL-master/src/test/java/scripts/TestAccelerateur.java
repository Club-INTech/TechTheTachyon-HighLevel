package scripts;

public class TestAccelerateur extends TestScriptBase {

    @Override
    public ScriptNamesMaster getScript() {
        return ScriptNamesMaster.ACCELERATEUR;
    }

    @Override
    public int[] versionsToTest() {
        return new int[] {0};
    }
}
