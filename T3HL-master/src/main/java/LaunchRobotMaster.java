import utils.ConfigData;
import utils.Container;
import utils.container.ContainerException;

/**
 * C'est shlag mais ça marche
 * @author jglrxavpok
 */
public class LaunchRobotMaster {

    public static void main(String[] args) throws ContainerException {
        Container container = Container.getInstance("Master");
        container.getConfig().override(ConfigData.SIMULATION, false);
        container.getConfig().override(ConfigData.VISUALISATION, true);
        MainMaster.main(args);
    }
}
