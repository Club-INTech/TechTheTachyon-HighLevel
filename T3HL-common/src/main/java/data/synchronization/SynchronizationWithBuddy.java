package data.synchronization;

import connection.Connection;

import data.GameState;
import data.SensorState;
import data.controlers.Channel;
import pfg.config.Config;
import utils.ConfigData;
import utils.Container;

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
        if (this.simulationActive) {
            this.connection = Connection.SLAVE_SIMULATEUR;
        } else {
            this.connection = Connection.SLAVE;
        }
    }

    /**
     * Permet de prévenir le secondaire que le distributeur x6 est libre
     */
    public void sendPaletX6Free() {
        sendString(String.format("%spaletsx6free", Channel.BUDDY_EVENT.getHeaders()));
    }

    public void waitForFreePaletX6() {
        waitWhileTrue(() -> !((boolean)GameState.PALETS_X6_FREE.getData()));
    }
}
