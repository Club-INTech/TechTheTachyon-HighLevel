package data.synchronization;

import connection.Connection;
import data.GameState;
import data.controlers.Channel;
import pfg.config.Config;
import utils.ConfigData;
import utils.Container;
import utils.Log;

/**
 * Classe de communication avec l'autre robot
 */
public class SynchronizationWithBuddy extends SynchronizationCommon {

    /**
     * Constructeur
     */
    public SynchronizationWithBuddy(Container container) {
        super(container);
        this.container=container;
    }


    @Override
    public void updateConfig(Config config) {
        // On est du côté jaune par défaut , le HL pense en jaune
        this.symetry = config.getString(ConfigData.COULEUR).equals("violet");
        this.isMaster = config.getBoolean(ConfigData.MASTER);
        this.simulationActive = config.getBoolean(ConfigData.SIMULATION);

        if(isMaster) {
            if (this.simulationActive) {
                this.connection = Connection.SLAVE_SIMULATEUR;
            } else {
                this.connection = Connection.SLAVE;
            }
        } else {
            if (this.simulationActive) {
                this.connection = Connection.SLAVE_SIMULATEUR; // TODO
            } else {
                this.connection = Connection.MASTER;
            }
        }
    }

    /**
     * Permet de prévenir le secondaire que le distributeur x6 est libre
     */
    public void sendBalanceFree() {
        sendString(String.format("%sbalancefree", Channel.BUDDY_EVENT.getHeaders()));
    }

    public void waitForFreeBalance() {
        if(connection.isInitiated()) {
            waitWhileTrue(() -> !((boolean)GameState.BALANCE_FREE.getData()));
        } else {
            Log.STRATEGY.critical("Pas de connexion au buddy, on prend l'hypothèse que la balance est libre");
        }
    }

    /**
     * Permet d'incrémenter les points sur le principal quand appelé par le secondaire
     * @param points nombre de points à incrémenter
     */
    public void increaseScore(int points){
        if(connection.isInitiated()) {
            sendString(String.format("%sincreaseScore %d", Channel.BUDDY_EVENT.getHeaders(), points));
        }else {
            Log.STRATEGY.critical("Pas de connexion au buddy, on ne peut pas incrémenter les points");
        }
    }
}
