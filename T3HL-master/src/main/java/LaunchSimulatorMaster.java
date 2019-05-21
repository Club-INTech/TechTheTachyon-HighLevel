import utils.ConfigData;
import utils.Container;
import utils.container.ContainerException;

/**
 * C'est shlag mais ça marche
 * @author jglrxavpok
 */
public class LaunchSimulatorMaster {

    public static void main(String[] args) throws ContainerException {
        Container container = Container.getInstance("Master");
        container.getConfig().override(ConfigData.COULEUR, "violet");
        container.getConfig().override(ConfigData.SIMULATION, true);
        container.getConfig().override(ConfigData.VISUALISATION, false);
      //  container.getConfig().override(ConfigData.USING_LIDAR, true);
        MainMaster.main(args);
    }
}
