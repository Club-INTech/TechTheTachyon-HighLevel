package scripts;

import data.CouleurPalet;
import data.XYO;
import robot.Master;
import utils.Container;
import utils.container.ContainerException;
import utils.math.Vec2;
import utils.math.VectCartesian;

public class TestAccelerateur extends TestScriptBase {

    @Override
    public void initState(Container container) throws ContainerException {
        for (int i = 0; i < 5; i++) {
            robot.pushPaletDroit(CouleurPalet.ROUGE);
            robot.pushPaletGauche(CouleurPalet.ROUGE);
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

    @Override
    public Vec2 startPosition() {
        return new VectCartesian(-730, 442);
    }
}
