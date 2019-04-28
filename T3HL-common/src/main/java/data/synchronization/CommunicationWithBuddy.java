package data.synchronization;

import connection.Connection;

import data.XYO;
import data.controlers.Channel;
import pfg.config.Config;
import utils.ConfigData;
import utils.Container;
import utils.math.Vec2;

import java.util.List;
import java.util.Locale;

/**
 * Classe de communication avec l'autre robot
 */
public class CommunicationWithBuddy extends CommunicationDefault {

    /**
     * Constructeur
     */
    public CommunicationWithBuddy(Container container){
        super(container);
        this.container=container;
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
}
