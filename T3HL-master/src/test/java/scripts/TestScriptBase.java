package scripts;

import utils.math.Vec2;

public abstract class TestScriptBase extends TestBaseHL {

    @Override
    public void action() {
        ScriptNamesMaster.reInit();
        Script script = getScript().getScript();

        for(int version : versionsToTest()) {
            script.goToThenExecute(version);
        }
    }

    public abstract ScriptNamesMaster getScript();

    public abstract int[] versionsToTest();
}
