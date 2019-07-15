package data.synchronization;

import connection.Connection;
import data.Palet;
import data.XYO;
import data.controlers.Channel;
import pfg.config.Configurable;
import utils.HLInstance;
import utils.Log;
import utils.communication.CommunicationException;
import utils.container.Module;
import utils.math.Vec2;

import java.util.List;
import java.util.Locale;

public abstract class SynchronizationCommon implements Module {

    /**
     * Boolean de symétrie
     */
    @Configurable
    protected boolean symetry;

    /**
     * Boolean de simulation
     */
    @Configurable
    protected boolean simulation;

    /**
     * Boolean de pour savoir si on est master
     */
    @Configurable
    protected boolean master;

    /**
     * Connection avec qui envoyer
     */
    protected Connection connection;

    /**
     * Container
     */
    protected HLInstance hl;

    /**
     * Constructeur
     */
    protected SynchronizationCommon(HLInstance hl){
        this.hl = hl;
    }

    /**
     * Envoie un message. NE REESSAYE PAS EN CAS D'ERREUR POUR EVITER UN BLOCAGE
     * @param message message à envoyer tel quel
     */
    protected void sendString(String message) {
        try {
            if(connection.isInitiated()) {
                this.connection.send(message);
                Log.ORDERS.debug("Sent to BUDDY: "+message);
            } else {
                Log.ORDERS.critical("Wanted to send to BUDDY: '"+message+"' but no connection!");
            }
        } catch (CommunicationException e) {
            e.printStackTrace();
        }
    }

    /**
     * Envoie notre position
     */
    public void sendPosition() {
        XYO xyo = XYO.getRobotInstance();
        Vec2 currentPos = xyo.getPosition();
        this.sendString(String.format(Locale.US, "%s%d %d %f", Channel.BUDDY_POSITION.getHeaders(), currentPos.getX(), currentPos.getY(), xyo.getOrientation()));
    }

    /**
     * Envoie la liste des positions composant le chemin actuel du robot coéquipier
     * @param path liste de Vec2 à envoyer
     */
    public void sendPath(List<Vec2> path){
        StringBuilder serializePath = new StringBuilder();
        for (Vec2 vec2 : path){
            serializePath.append(vec2.getX());
            serializePath.append(" ");
            serializePath.append(vec2.getY());
            serializePath.append(" ");
        }
        this.sendString(serializePath.toString());
        this.sendString(String.format(Locale.US, "%s%s", Channel.BUDDY_PATH.getHeaders(), serializePath.toString()));
    }

    /**
     * Envoie une confirmation de palet pris
     * @param palet quel palet a été pris
     * @param pris True si le palet a été pris, False sinon
     */
    public void sendPaletPris(Palet palet, boolean pris){
        this.sendString(String.format(Locale.US, "%s%d %s", Channel.UPDATE_PALETS.getHeaders(), palet.getId(), pris));
    }

    /**
     * Envoie l'état du goldenium
     * @param accessible True si le goldenium est accessible, False sinon
     */
    public void sendGoldeniumState(boolean accessible){
        this.sendString(String.format(Locale.US, "%s%d %s", Channel.UPDATE_PALETS.getHeaders(), Palet.GOLDENIUM.getId(), accessible));
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

}
