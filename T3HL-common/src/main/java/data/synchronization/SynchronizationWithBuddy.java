package data.synchronization;

import connection.Connection;
import data.GameState;
import data.controlers.Channel;
import pfg.config.Config;
import utils.HLInstance;
import utils.Log;
import utils.container.Module;

/**
 * Classe de communication avec l'autre robot
 */
public class SynchronizationWithBuddy extends SynchronizationCommon {

    /**
     * Constructeur
     */
    public SynchronizationWithBuddy(HLInstance hl) {
        super(hl);
        this.hl = hl;
    }


    @Override
    public void updateConfig(Config config) {
        // On est du côté jaune par défaut , le HL pense en jaune
        if(master) {
            if (this.simulation) {
                this.connection = Connection.SLAVE_SIMULATEUR;
            } else {
                this.connection = Connection.SLAVE;
            }
        } else {
            if (this.simulation) {
                this.connection = Connection.SLAVE_SIMULATEUR; // TODO
            } else {
                this.connection = Connection.MASTER;
            }
        }
    }

    /**
     * Permet de prévenir le secondaire que la balance est libre
     */
    public void sendBalanceFree() {
        sendString(String.format("%sbalancefree", Channel.BUDDY_EVENT.getHeaders()));
    }

    /**
     * Permet de prévenir le principal que le distributeur x6 est libre
     */
    public void sendAcceleratorFree() {
        sendString(String.format("%sacceleratorfree", Channel.BUDDY_EVENT.getHeaders()));
    }

    public void waitForFreeBalance() {
        if(connection.isInitiated()) {
            Module.waitWhileTrue(() -> !((boolean)GameState.BALANCE_FREE.getData()));
        } else {
            Log.STRATEGY.critical("Pas de connexion au buddy, on prend l'hypothèse que la balance est libre");
        }
    }

    public void waitForFreeAccelerator() {
        if(connection.isInitiated()) {
            Module.waitWhileTrue(() -> !((boolean)GameState.ACCELERATOR_FREE.getData()));
        } else {
            Log.STRATEGY.critical("Pas de connexion au buddy, on prend l'hypothèse que l'accélérateur est libre");
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
