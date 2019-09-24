import robot.Robots;
import utils.ConfigData;
import utils.HLInstance;
import utils.container.ContainerException;

/**
 * C'est shlag mais ça marche
 * @author jglrxavpok
 */
public class LaunchRobotMaster {

    public static void main(String[] args) throws ContainerException {
        HLInstance hl = HLInstance.get(Robots.MAIN);
        hl.getConfig().override(ConfigData.SIMULATION, false);
        hl.getConfig().override(ConfigData.VISUALISATION, false);
        MainMaster.main(args);
    }
}
