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

import java.util.List;
import java.util.Locale;

public abstract class SynchronizationCommon implements Service {

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
    protected SynchronizationCommon(Container container){
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
     * Envoie la liste des positions composant le chemin actuel du robot coéquipier
     * @param path liste de Vec2 à envoyer
     */
    protected void sendPath(List<Vec2> path){
        StringBuilder serializePath = new StringBuilder();
        for (Vec2 vec2 : path){
            serializePath.append(vec2.getX());
            serializePath.append(" ");
            serializePath.append(vec2.getY());
            serializePath.append(" ");
        }
        this.sendString(serializePath.toString());
        this.sendString(String.format(Locale.US, "%s %s", Channel.BUDDY_PATH, serializePath.toString()));
    }

    /**
     * Envoie une confirmation de palet pris
     * @param palet quel palet a été pris
     * @param pris True si le palet a été pris, False sinon
     */
    protected void sendPaletPris(Palet palet, boolean pris){
        this.sendString(String.format(Locale.US, "%s %d %s", Channel.UPDATE_PALETS, palet.getId(), pris));
    }

    /**
     * Envoie l'état du goldenium
     * @param accessible True si le goldenium est accessible, False sinon
     */
    protected void sendGoldeniumState(boolean accessible){
        this.sendString(String.format(Locale.US, "%s %d %s", Channel.UPDATE_PALETS, Palet.GOLDENIUM.getId(), accessible));
    }


    @Override
    public abstract void updateConfig(Config config);
}