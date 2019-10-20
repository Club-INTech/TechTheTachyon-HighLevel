package scripts;

import data.Sick;
import data.XYO;
import utils.ConfigData;
import utils.HLInstance;
import utils.container.ContainerException;
import utils.math.Vec2;
import utils.math.InternalVectCartesian;

import java.util.concurrent.TimeUnit;

/**
 * Test du recalage avec les SICK
 */
public class TestSICK extends TestBaseHL {

    @Override
    protected void setup(boolean simulationMode) {
        super.setup(simulationMode);
    }

    @Override
    public void initState(HLInstance hl) throws ContainerException {

    }

    @Override
    public double startOrientation() {
        return 0;
    }

    @Override
    public Vec2 startPosition() {
        // on s'en fiche de la position de départ, tout le but de ce test est de vérifier qu'on reçoit des données
        return new InternalVectCartesian(0,500);
    }

    @Override
    public void action() throws InterruptedException {
        if(ConfigData.SIMULATION.getDefaultValue()) {
            // impossible de tester en mode simulation
        } else {
            // test avec tous les SICK
            for (int i=0; i < 100000;i++) {
                robot.computeNewPositionAndOrientation(Sick.LOWER_RIGHT_CORNER_TOWARDS_0);
                TimeUnit.MILLISECONDS.sleep(200);
                System.out.println(XYO.getRobotInstance());
            }
            System.out.println(XYO.getRobotInstance());
        }
    }
}
