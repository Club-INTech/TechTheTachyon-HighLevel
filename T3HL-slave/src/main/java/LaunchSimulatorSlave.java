import utils.ConfigData;
import utils.Container;
import utils.container.ContainerException;

/**
 * C'est shlag mais ça marche
 * @author jglrxavpok
 */
public class LaunchSimulatorSlave {

    public static void main(String[] args) throws ContainerException {
        Container container = Container.getInstance("Slave");
        container.getConfig().override(ConfigData.SIMULATION, true);
        container.getConfig().override(ConfigData.VISUALISATION, false);
        MainSlave.main(args);
    }
}
