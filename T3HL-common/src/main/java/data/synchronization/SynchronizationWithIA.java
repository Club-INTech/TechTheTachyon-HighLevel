package data.synchronization;

import connection.Connection;
import data.controlers.Channel;
import data.table.MobileCircularObstacle;
import pfg.config.Config;
import utils.ConfigData;
import utils.Container;
import utils.math.Vec2;

import java.util.ArrayList;
import java.util.Locale;

public class SynchronizationWithIA extends SynchronizationCommon {

    /**
     * Constructeur
     */
    public SynchronizationWithIA(Container container) {
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

    /**
     * Envoie un obstacle mobile (aka ennemi)
     * @param obstacle vec2 de la position à envoyer
     */
    public void sendMobileObstacle(Vec2 obstacle){
        this.sendString(String.format(Locale.US, "%s %s %s", Channel.OBSTACLES, obstacle.getX(), obstacle.getY()));
    }

    /**
     * Envoie une liste d'obstacles mobiles (aka ennemis)
     * @param obstacles liste des obstacles
     */
    public void sendMobileObstacleList(ArrayList<MobileCircularObstacle> obstacles){
        for (MobileCircularObstacle obstacle : obstacles){
            this.sendMobileObstacle(obstacle.getPosition());
        }
    }

    /**
     * Envoie un message quand le jumper est sorti
     */
    public void sendJumperOut(){
        this.sendString(String.format(Locale.US, "%s KRAKATOA", Channel.EVENT));
    }

    @Override
    public void updateConfig(Config config) {
        // On est du côté jaune par défaut , le HL pense en jaune
        this.symetry = config.getString(ConfigData.COULEUR).equals("violet");
        this.isMaster = config.getBoolean(ConfigData.MASTER);
        this.simulationActive = config.getBoolean(ConfigData.SIMULATION);
        if (this.simulationActive) {
            this.connection = Connection.BALISE_IA;
        } else {
            //TODO : Connexion simulée pour la balise
            this.connection = Connection.BALISE_IA;
        }
    }
}
