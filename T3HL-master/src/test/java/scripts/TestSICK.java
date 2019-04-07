package scripts;

import data.Sick;
import data.XYO;
import utils.ConfigData;
import utils.Container;
import utils.container.ContainerException;
import utils.math.Vec2;
import utils.math.VectCartesian;

import java.util.concurrent.TimeUnit;

import static java.lang.Thread.sleep;

/**
 * Test du recalage avec les SICK
 */
public class TestSICK extends TestBaseHL {

    @Override
    protected void setup(boolean simulationMode) {
        super.setup(simulationMode);
    }

    @Override
    public void initState(Container container) throws ContainerException {

    }

    @Override
    public double startOrientation() {
        return Math.PI;
    }

    @Override
    public Vec2 startPosition() {
        // on s'en fiche de la position de départ, tout le but de ce test est de vérifier qu'on reçoit des données
        return new VectCartesian(0,500);
    }

    @Override
    public void action() throws InterruptedException {
        if((boolean)ConfigData.SIMULATION.getDefaultValue()) {
            // impossible de tester en mode simulation
        } else {
            // test avec tous les SICK
            for (int i=0;i < 100;i++) {
            robot.computeNewPositionAndOrientation(Sick.LOWER_LEFT_CORNER_TOWARDS_PI);
            TimeUnit.SECONDS.sleep(1);
            System.out.println(XYO.getRobotInstance());
            }
        }
    }
}
