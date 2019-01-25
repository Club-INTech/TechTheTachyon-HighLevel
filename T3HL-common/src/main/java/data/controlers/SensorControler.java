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

    boolean isMaster;


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
                if (isMaster) {
                    sickMeasurements = sickData.poll().split(COORDONATE_SEPARATOR);
                    int[] significantSicks = Sick.getSignificantSicks();
                    int dsick = 173;
                    int esick = Integer.parseInt(sickMeasurements[significantSicks[1]]) - Integer.parseInt(sickMeasurements[significantSicks[2]]);
                    double rapport = esick / dsick;
                    int xCalcule;
                    int yCalcule;
                    double teta;

                    if (ConfigData.COULEUR.toString().equals("jaune")) {
                        // On différencie les cas où le robot est orienté vers la gauche et la droite

                        teta = Math.atan(rapport);
                        xCalcule = (int) (1500 - (Integer.parseInt(sickMeasurements[significantSicks[0]])) * Math.cos(teta));
                        if (0 < teta && teta < Math.PI) { //modifier car arctan est toujours inférieur à PI
                            if (significantSicks[1] == 4 || significantSicks[1] == 5) {
                                yCalcule = (int) (2000 - (Integer.parseInt(sickMeasurements[significantSicks[2]])) * Math.cos(teta));
                            } else {
                                yCalcule = (int) ((Integer.parseInt(sickMeasurements[significantSicks[2]])) * Math.cos(teta));
                            }
                        } else {
                            if (significantSicks[1] == 4 || significantSicks[1] == 5) {
                                yCalcule = (int) ((Integer.parseInt(sickMeasurements[significantSicks[2]])) * Math.cos(teta));
                            } else {
                                yCalcule = (int) (2000 - (Integer.parseInt(sickMeasurements[significantSicks[2]])) * Math.cos(teta));
                            }
                        }
                    } else {
                        teta = Math.PI - Math.atan(rapport);
                        xCalcule = (int) ((Integer.parseInt(sickMeasurements[significantSicks[0]])) * Math.cos(teta)) - 1500;
                        if (0 < teta && teta < Math.PI) {
                            if (significantSicks[1] == 1 || significantSicks[1] == 2) {
                                yCalcule = (int) (2000 - (Integer.parseInt(sickMeasurements[significantSicks[2]])) * Math.cos(teta));
                            } else {
                                yCalcule = (int) ((Integer.parseInt(sickMeasurements[significantSicks[2]])) * Math.cos(teta));
                            }
                        } else {
                            if (significantSicks[1] == 4 || significantSicks[1] == 5) {
                                yCalcule = (int) ((Integer.parseInt(sickMeasurements[significantSicks[2]])) * Math.cos(teta));
                            } else {
                                yCalcule = (int) (2000 - (Integer.parseInt(sickMeasurements[significantSicks[2]])) * Math.cos(teta));
                            }
                        }
                    }
                    VectCartesian newPosition = new VectCartesian(xCalcule, yCalcule);
                    double newOrientation = teta;
                    XYO newXYO = new XYO(newPosition, newOrientation + Math.PI);
                    Sick.setNewXYO(newXYO);
                }
                else {
                    sickMeasurements = sickData.poll().split(COORDONATE_SEPARATOR);
                    int dsick =50;
                    int esick = Integer.parseInt(sickMeasurements[1])- Integer.parseInt(sickMeasurements[2]);
                    double rapport = esick / dsick;
                    int xCalcule;
                    int yCalcule;
                    double teta;

                    if (ConfigData.COULEUR.toString().equals("jaune")) {
                        // On différencie les cas où le robot est orienté vers la gauche et la droite

                        teta = Math.atan(rapport);
                        xCalcule = (int) (1500 - Integer.parseInt(sickMeasurements[1]) * Math.cos(teta));
                        yCalcule = (int) (Integer.parseInt(sickMeasurements[0]) * Math.cos(teta));
                    }

                     else {
                        teta = Math.PI - Math.atan(rapport);
                        xCalcule = (int) (Integer.parseInt(sickMeasurements[0]) * Math.cos(teta)) - 1500;
                        yCalcule = (int) (2000 - Integer.parseInt(sickMeasurements[2]) * Math.cos(teta));
                     }

                    VectCartesian newPosition = new VectCartesian(xCalcule, yCalcule);
                    double newOrientation = teta;
                    XYO newXYO = new XYO(newPosition, newOrientation + Math.PI);
                    Sick.setNewXYO(newXYO);
                }


            }
        }
    }

    @Override
    public void updateConfig(Config config) {
        this.isMaster = config.getBoolean(ConfigData.MASTER);
        this.symetrie = config.getString(ConfigData.COULEUR).equals("jaune");
    }
}
