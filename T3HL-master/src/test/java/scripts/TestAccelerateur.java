package scripts;

import data.CouleurPalet;
import robot.Master;
import utils.Container;
import utils.container.ContainerException;

public class TestAccelerateur extends TestScriptBase {

    @Override
    public void initState(Container container) throws ContainerException {
        Master master = container.getService(Master.class);
        for (int i = 0; i < 5; i++) {
            master.pushPaletDroit(CouleurPalet.ROUGE);
            master.pushPaletGauche(CouleurPalet.ROUGE);
        }
    }

    @Override
    public ScriptNamesMaster getScript() {
        return ScriptNamesMaster.ACCELERATEUR;
    }

    @Override
    public int[] versionsToTest() {
        return new int[] {0};
    }
}
