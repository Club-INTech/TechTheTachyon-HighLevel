import robot.Robots;
import utils.ConfigData;
import utils.HLInstance;
import utils.container.ContainerException;

/**
 * C'est shlag mais ça marche
 * @author jglrxavpok
 */
public class LaunchSimulatorSlave {

    public static void main(String[] args) throws ContainerException {
        HLInstance hl = HLInstance.get(Robots.SECONDARY);
        hl.getConfig().override(ConfigData.USING_LIDAR, false);
        hl.getConfig().override(ConfigData.MASTER, false);
        hl.getConfig().override(ConfigData.USING_PANEL, false);
        hl.getConfig().override(ConfigData.SIMULATION, true);
        hl.getConfig().override(ConfigData.VISUALISATION, false);
        MainSlave.main(args);
    }
}
