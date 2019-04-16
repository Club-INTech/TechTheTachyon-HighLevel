import utils.ConfigData;

/**
 * C'est shlag mais ça marche
 * @author jglrxavpok
 */
public class LaunchSimulator {

    public static void main(String[] args) {
        ConfigData.SIMULATION.setDefaultValue(true);
        ConfigData.COULEUR.setDefaultValue("jaune");
        Main.main(args);
    }
}
