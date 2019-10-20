import robot.Robots;
import utils.ConfigData;
import utils.HLInstance;
import utils.container.ContainerException;

/**
 * C'est shlag mais ça marche
 * @author jglrxavpok
 */
public class LaunchSimulatorMaster {

    public static void main(String[] args) throws ContainerException {
        HLInstance hl = HLInstance.get(Robots.MAIN);
        //hl.getConfig().override(ConfigData.COULEUR, "violet");
        hl.getConfig().override(ConfigData.SIMULATION, true);
        hl.getConfig().override(ConfigData.VISUALISATION, false);
        hl.getConfig().override(ConfigData.USING_LIDAR, false);
        MainMaster.main(args);
    }
}
