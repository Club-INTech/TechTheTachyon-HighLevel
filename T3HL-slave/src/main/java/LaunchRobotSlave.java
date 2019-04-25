import utils.ConfigData;
import utils.Container;
import utils.container.ContainerException;

/**
 * C'est shlag mais ça marche
 * @author jglrxavpok
 */
public class LaunchRobotSlave {

    public static void main(String[] args) throws ContainerException {
        Container container = Container.getInstance("Slave");
        container.getConfig().override(ConfigData.SIMULATION, false);
        container.getConfig().override(ConfigData.VISUALISATION, true);
        MainSlave.main(args);
    }
}
