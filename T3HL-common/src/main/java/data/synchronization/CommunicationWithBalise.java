package data.synchronization;

import connection.Connection;
import data.controlers.Channel;
import pfg.config.Config;
import utils.ConfigData;
import utils.Container;

import java.util.Locale;

public class CommunicationWithBalise extends CommunicationDefault {

    /**
     * Constructeur
     */
    public CommunicationWithBalise(Container container) {
        super(container);
        this.container = container;
    }

    /**
     * Envoie une confirmation de réalisation d'un script
     * @param scriptStr string avec le nom du script
     * @param version version du script
     */
    public void sendScriptDone(String scriptStr, int version){
        this.sendString(String.format(Locale.US, "%s %s %d done", Channel.SCRIPTS, scriptStr, version));
    }

    @Override
    public void updateConfig(Config config) {
        // On est du côté jaune par défaut , le HL pense en jaune
        this.symetry = config.getString(ConfigData.COULEUR).equals("violet");
        this.isMaster = config.getBoolean(ConfigData.MASTER);
        this.simulationActive = config.getBoolean(ConfigData.SIMULATION);
        if (this.simulationActive) {
            this.connection = Connection.BALISE;
        } else {
            //TODO : Connexion simulée pour la balise
            this.connection = Connection.BALISE;
        }
    }
}
