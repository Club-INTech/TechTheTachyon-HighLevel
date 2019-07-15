package data.synchronization;

import connection.Connection;
import data.CouleurPalet;
import data.controlers.Channel;
import data.table.MobileCircularObstacle;
import pfg.config.Config;
import robot.Robot;
import utils.HLInstance;
import utils.container.ContainerException;
import utils.math.Vec2;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Stack;

public class SynchronizationWithIA extends SynchronizationCommon {

    /**
     * Constructeur
     */
    public SynchronizationWithIA(HLInstance hl) {
        super(hl);
        this.hl = hl;
    }

    /**
     * Envoie une confirmation de réalisation d'un script
     * @param scriptStr string avec le nom du script
     * @param version version du script
     */
    public void sendScriptDone(String scriptStr, int version){
        this.sendString(String.format(Locale.US, "%s %s %d done", Channel.SCRIPTS.getHeaders(), scriptStr, version));
    }

    /**
     * Envoie un obstacle mobile (aka ennemi)
     * @param obstacle vec2 de la position à envoyer
     */
    public void sendMobileObstacle(Vec2 obstacle){
        this.sendString(String.format(Locale.US, "%s %s %s", Channel.OBSTACLES.getHeaders(), obstacle.getX(), obstacle.getY()));
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
        this.sendString(String.format(Locale.US, "%s KRAKATOA", Channel.EVENTS.getHeaders()));
    }

    /**
     * Envoie le contenu de l'ascenseur droit à l'IA
     */
    public void sendPaletsAscenseurDroit(){
        Robot robot = null;
        try {
            robot = this.hl.module(Robot.class);
        } catch (ContainerException e) {
            e.printStackTrace();
        }
        Stack<CouleurPalet> ascenseurDroit = robot.getRightElevatorOrNull();
        if (ascenseurDroit != null) {
            StringBuilder toSend = new StringBuilder();
            for (CouleurPalet couleur : ascenseurDroit) {
                toSend.append(couleur.getNom());
                toSend.append(" ");
            }
            this.sendString(String.format(Locale.US, "%s droit %s", Channel.PALETS_ASCENSEUR.getHeaders(), toSend.toString()));
        }
    }

    /**
     * Envoie le contenu de l'ascenseur gauche à l'IA
     */
    public void sendPaletsAscenseurGauche(){
        Robot robot = null;
        try {
            robot = this.hl.module(Robot.class);
        } catch (ContainerException e) {
            e.printStackTrace();
        }
        Stack<CouleurPalet> ascenseurGauche = robot.getLeftElevatorOrNull();
        if (ascenseurGauche != null) {
            StringBuilder toSend = new StringBuilder();
            for (CouleurPalet couleur : ascenseurGauche) {
                toSend.append(couleur.getNom());
                toSend.append(" ");
            }
            this.sendString(String.format(Locale.US, "%s gauche %s", Channel.PALETS_ASCENSEUR.getHeaders(), toSend.toString()));
        }
    }

    /**
     * Envoie le contenu de l'ascenseur du secondaire (droit) à l'IA
     */
    public void sendPaletsAscenseurSecondaire(){
        this.sendPaletsAscenseurDroit();
    }

    @Override
    public void updateConfig(Config config) {
        if (this.simulation) {
            this.connection = Connection.BALISE_IA;
        } else {
            //TODO : Connexion simulée pour la balise
            this.connection = Connection.BALISE_IA;
        }
    }
}
