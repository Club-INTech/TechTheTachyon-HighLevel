import robot.Robots;
import utils.ConfigData;
import utils.HLInstance;
import utils.container.ContainerException;

/**
 * C'est shlag mais ça marche
 * @author jglrxavpok
 */
public class LaunchRobotSlave {

    public static void main(String[] args) throws ContainerException {
        HLInstance hl = HLInstance.get(Robots.SECONDARY);
        hl.getConfig().override(ConfigData.SIMULATION, false);
        hl.getConfig().override(ConfigData.VISUALISATION, true);
        MainSlave.main(args);
    }
}
