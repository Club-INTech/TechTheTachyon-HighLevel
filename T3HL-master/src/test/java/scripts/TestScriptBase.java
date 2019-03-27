package scripts;

import robot.Master;
import robot.Robot;
import utils.Container;
import utils.container.ContainerException;

public abstract class TestScriptBase extends TestBaseHL {

    @Override
    public void action() {
        ScriptNamesMaster.reInit();
        Script script = getScript().getScript();

        for(int version : versionsToTest()) {
            script.goToThenExecute(version);
        }
    }

    private Robot getRobot() throws ContainerException {
        Container container = Container.getInstance("robot.Master");

        return container.getService(Master.class);
    }

    public abstract ScriptNamesMaster getScript();

    public abstract int[] versionsToTest();

}
