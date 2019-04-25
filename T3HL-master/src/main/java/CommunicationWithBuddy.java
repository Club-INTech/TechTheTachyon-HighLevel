import connection.Connection;
import data.Palet;
import data.XYO;
import data.controlers.Channel;
import pfg.config.Config;
import scripts.ScriptNamesMaster;
import utils.ConfigData;
import utils.Container;
import utils.Log;
import utils.communication.CommunicationException;
import utils.container.Service;
import utils.math.Vec2;

import java.util.List;
import java.util.Locale;

public class CommunicationWithBuddy implements Service {

    /**
     * Boolean de symétrie
     */
    private boolean symetry;
    /**
     * Boolean de simulation
     */
    private boolean simulation;
    /**
     * Connection avec buddy
     */
    private Connection buddyConnection;
    /**
     * Container
     */
    private Container container;

    /**
     * Constructeur
     */
    public CommunicationWithBuddy(Container container){
        this.container=container;
    }

    /**
     * Envoie un message au robot coéquipier. NE REESSAYE PAS EN CAS D'ERREUR POUR EVITER UN BLOCAGE
     * @param message message à envoyer tel quel
     */
    private void sendString(String message) {
        try {
            buddyConnection.send(message);
            Log.ORDERS.debug("Sent to BUDDY: "+message);
        } catch (CommunicationException e) {
            e.printStackTrace();
        }
    }

    /**
     * Envoie notre position à l'autre robot
     */
    public void sendPosition(){
        Vec2 currentPos = XYO.getRobotInstance().getPosition();
        this.sendString(String.format(Locale.US, "%s %d %d", Channel.BUDDY_POSITION, currentPos.getX(), currentPos.getY()));
    }

    /**
     * Envoie un ordre de script au secondaire, le secondaire va ensuite executer ce script
     * @param script script du secondaire à executer
     * @param version version du script à executer
     */
    public void sendScriptOrder(ScriptNamesMaster script, int version){
        this.sendString(String.format(Locale.US, "%s %s %d", Channel.BUDDY_SCRIPT_ORDER, script.getName(), version));
    }

    /**
     * Envoie une confirmation de palet pris au robot coéquipier
     * @param palet quel palet a été pris
     */
    public void sendPaletPris(Palet palet){
        this.sendString(String.format(Locale.US, "%s %d", Channel.BUDDY_PALETS, palet.getId()));
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
    }

    @Override
    public void updateConfig(Config config) {
        // On est du côté violet par défaut , le HL pense en violet
        this.symetry = config.getString(ConfigData.COULEUR).equals("violet");
        this.simulation = config.getBoolean(ConfigData.SIMULATION);
        if (this.simulation) {
            this.buddyConnection = Connection.SLAVE_SIMULATEUR;
        } else {
            this.buddyConnection = Connection.SLAVE;
        }
    }
}
