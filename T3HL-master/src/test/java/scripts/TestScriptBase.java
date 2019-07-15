package scripts;

import utils.container.ContainerException;

public abstract class TestScriptBase extends TestBaseHL {

    @Override
    public void action() throws ContainerException {
        Script script = getScript().createScript(hl);

        for(int version : versionsToTest()) {
            script.goToThenExecute(version);
        }
    }

    public abstract ScriptNamesMaster getScript();

    public abstract int[] versionsToTest();
}
