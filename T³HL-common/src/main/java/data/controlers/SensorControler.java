package data.controlers;

import data.XYO;
import pfg.config.Config;
import utils.ConfigData;
import utils.Log;
import utils.container.Service;
import utils.math.Calculs;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Gère les données de positions et de capteur ne nécessitant pas de traitement
 *
 * @author rem
 */
public class SensorControler extends Thread implements Service {

    /**
     * Temps d'attente entre deux vérification de la queue
     */
    private static final int TIME_LOOP                  = 5;

    /**
     * Separateur entre deux coordonnées d'un point
     */
    private static final String COORDONATE_SEPARATOR    = " ";

    /**
     * Listener
     */
    private Listener listener;

    /**
     * Files de communication avec le Listener
     */
    private ConcurrentLinkedQueue<String> robotPosQueue;
    private ConcurrentLinkedQueue<String> buddyPosQueue;

    /**
     * True si autre couleur
     */
    private boolean symetrie;

    /**
     * Construit un gestionnaire de capteur
     * @param listener
     *              le listener
     */
    public SensorControler(Listener listener) {
        this.listener = listener;
        this.robotPosQueue = new ConcurrentLinkedQueue<>();
        this.buddyPosQueue = new ConcurrentLinkedQueue<>();
        listener.addQueue(Channel.ROBOT_POSITION, robotPosQueue);
        listener.addQueue(Channel.BUDDY_POSITION, buddyPosQueue);
    }

    @Override
    public void run() {
        Log.DATA_HANDLER.debug("Controler lancé : en attente du listener...");
        while (!listener.isAlive()) {
            try {
                Thread.sleep(Listener.TIME_LOOP);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Log.DATA_HANDLER.debug("Controler opérationnel");

        String[] coordonates;
        int x;
        int y;
        double o;
        while (!Thread.currentThread().isInterrupted()) {
            while (robotPosQueue.peek() == null && buddyPosQueue.peek() == null) {
                try {
                    Thread.sleep(TIME_LOOP);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (robotPosQueue.peek() !=null) {
                coordonates = robotPosQueue.poll().split(COORDONATE_SEPARATOR);
                x = Integer.parseInt(coordonates[0]);
                y = Integer.parseInt(coordonates[1]);
                o = Double.parseDouble(coordonates[2]);
                if (symetrie) {
                    x = -x;
                    o = Calculs.modulo(Math.PI - o, 2*Math.PI);
                }
                XYO.getRobotInstance().update(x, y, o);
            }
            if (buddyPosQueue.peek() !=null) {
                coordonates = buddyPosQueue.poll().split(COORDONATE_SEPARATOR);
                x = Integer.parseInt(coordonates[0]);
                y = Integer.parseInt(coordonates[1]);
                o = Double.parseDouble(coordonates[2]);
                if (symetrie) {
                    x = -x;
                    o = Calculs.modulo(Math.PI - o, 2*Math.PI);
                }
                XYO.getBuddyInstance().update(x, y, o);
            }
        }
    }

    @Override
    public void updateConfig(Config config) {
        this.symetrie = config.getString(ConfigData.COULEUR).equals("jaune");
    }
}
