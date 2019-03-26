import utils.ConfigData;

/**
 * C'est shlag mais ça marche
 * @author jglrxavpok
 */
public class LaunchRobot {

    public static void main(String[] args) {
        ConfigData.SIMULATION.setDefaultValue(false);
        Main.main(args);
    }
}
