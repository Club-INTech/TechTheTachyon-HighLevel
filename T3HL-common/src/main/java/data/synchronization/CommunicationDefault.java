package data.synchronization;

import connection.Connection;
import data.Palet;
import data.XYO;
import data.controlers.Channel;
import pfg.config.Config;
import utils.Container;
import utils.Log;
import utils.communication.CommunicationException;
import utils.container.Service;
import utils.math.Vec2;

import java.util.Locale;

public abstract class CommunicationDefault implements Service {

    /**
     * Boolean de symétrie
     */
    protected boolean symetry;

    /**
     * Boolean de simulation
     */
    protected boolean simulationActive;

    /**
     * Boolean de pour savoir si on est master
     */
    protected boolean isMaster;

    /**
     * Connection avec qui envoyer
     */
    protected Connection connection;

    /**
     * Container
     */
    protected Container container;

    /**
     * Constructeur
     */
    protected CommunicationDefault(Container container){
        this.container=container;
    }

    /**
     * Envoie un message. NE REESSAYE PAS EN CAS D'ERREUR POUR EVITER UN BLOCAGE
     * @param message message à envoyer tel quel
     */
    protected void sendString(String message) {
        try {
            this.connection.send(message);
            Log.ORDERS.debug("Sent to BUDDY: "+message);
        } catch (CommunicationException e) {
            e.printStackTrace();
        }
    }

    /**
     * Envoie notre position
     */
    protected void sendPosition(){
        Vec2 currentPos = XYO.getRobotInstance().getPosition();
        this.sendString(String.format(Locale.US, "%s %d %d", Channel.BUDDY_POSITION, currentPos.getX(), currentPos.getY()));
    }

    /**
     * Envoie une confirmation de palet pris
     * @param palet quel palet a été pris
     */
    protected void sendPaletPris(Palet palet){
        this.sendString(String.format(Locale.US, "%s %d", Channel.UPDATE_PALETS, palet.getId()));
    }

    @Override
    public abstract void updateConfig(Config config);
}
