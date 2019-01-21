package data.controlers;

import data.SensorState;
import data.Sick;
import data.XYO;
import pfg.config.Config;
import utils.ConfigData;
import utils.Log;
import utils.container.Service;
import utils.math.Calculs;
import utils.math.Vec2;
import utils.math.VectCartesian;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Future;

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
    private ConcurrentLinkedQueue<String> sickData;

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
        this.sickData=new ConcurrentLinkedQueue<>();
        listener.addQueue(Channel.ROBOT_POSITION, robotPosQueue);
        listener.addQueue(Channel.BUDDY_POSITION, buddyPosQueue);
        listener.addQueue(Channel.SICK,sickData);
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
        String[] sickMeasurements;
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
            if (sickData.peek() !=null) {
                 sickMeasurements = sickData.poll().split(COORDONATE_SEPARATOR);
                //TODO : implémenter calculs : on aura donc une nouvelle position et une nouvelle orientation FAIT

                //TODO : Voir quelles sont les valeurs des sicks qui sont signifiantes via la méthode Sick.getSignificantSicks et lire les valeurs correspondantes dans sickMeasurements
                int[] significantSicks = Sick.getSignificantSicks();

                int dsick = 173;
                int xtheo = 0;
                int ytheo = 0;
                int esick = Integer.parseInt(sickMeasurements[significantSicks[0]]) - Integer.parseInt(sickMeasurements[significantSicks[1]]);
                double rapport= esick/dsick;
                double teta = Math.atan(rapport);
                int xCalcule = (int) ((Integer.parseInt(sickMeasurements[significantSicks[0]]) - xtheo) * Math.cos(teta));
                int yCalcule = (int) ((Integer.parseInt(sickMeasurements[significantSicks[2]])- ytheo) * Math.cos(teta));

                VectCartesian newPosition = new VectCartesian(xCalcule,yCalcule);
                double newOrientation = teta;
                XYO newXYO = new XYO(newPosition, newOrientation);
                Sick.setNewXYO(newXYO);

            }
        }
    }

    @Override
    public void updateConfig(Config config) {
        this.symetrie = config.getString(ConfigData.COULEUR).equals("jaune");
    }
}
