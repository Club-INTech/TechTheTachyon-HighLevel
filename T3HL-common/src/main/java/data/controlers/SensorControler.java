package data.controlers;

import data.*;
import pfg.config.Config;
import utils.ConfigData;
import utils.Log;
import utils.container.Service;
import utils.math.Calculs;
import utils.math.Vec2;
import utils.math.VectCartesian;

import java.util.ArrayList;
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
    private static final String ARGUMENTS_SEPARATOR = " ";

    /**
     * Listener
     */
    private Listener listener;

    /**
     * Files de communication avec le Listener
     */
    private ConcurrentLinkedQueue<String> robotPosQueue;
    private ConcurrentLinkedQueue<String> buddyPosQueue;
    private ConcurrentLinkedQueue<String> eventData;
    private ConcurrentLinkedQueue<String> sickData;
    private ConcurrentLinkedQueue<String> couleurPalet;

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
        this.eventData=new ConcurrentLinkedQueue<>();
        this.sickData=new ConcurrentLinkedQueue<>();
        this.couleurPalet =new ConcurrentLinkedQueue<>();

        listener.addQueue(Channel.ROBOT_POSITION, robotPosQueue);
        listener.addQueue(Channel.BUDDY_POSITION, buddyPosQueue);
        listener.addQueue(Channel.EVENT, eventData);
        listener.addQueue(Channel.SICK, sickData);
        listener.addQueue(Channel.COULEUR_PALET_PRIS, couleurPalet);
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
        String[] event;
        int x;
        int y;
        double o;
        while (!Thread.currentThread().isInterrupted()) {
            while (robotPosQueue.peek() == null && buddyPosQueue.peek() == null && sickData.peek()==null && eventData.peek()==null && couleurPalet.peek()==null) {
                try {
                    Thread.sleep(TIME_LOOP);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (robotPosQueue.peek() != null) {
                coordonates = robotPosQueue.poll().split(ARGUMENTS_SEPARATOR);
                x = Math.round(Float.parseFloat(coordonates[0]));
                y = Math.round(Float.parseFloat(coordonates[1]));
                o = Double.parseDouble(coordonates[2]);
                if (symetrie) {
                    x = -x;
                    o = Calculs.modulo(Math.PI - o, Math.PI);
                }
                XYO.getRobotInstance().update(x, y, o);
            }
            if (buddyPosQueue.peek() != null) {
                coordonates = buddyPosQueue.poll().split(ARGUMENTS_SEPARATOR);
                x = Integer.parseInt(coordonates[0]);
                y = Integer.parseInt(coordonates[1]);
                o = Double.parseDouble(coordonates[2]);
                if (symetrie) {
                    x = -x;
                    o = Calculs.modulo(Math.PI - o, Math.PI);
                }
                XYO.getBuddyInstance().update(x, y, o);
            }


            if (eventData.peek() != null) {
                String data = eventData.poll();
                Log.COMMUNICATION.debug("Got event from LL: "+data);
                event = data.split(ARGUMENTS_SEPARATOR);
                switch(event[0]) {
                    case "stoppedMoving":
                        SensorState.MOVING.setData(false);
                        break;

                    case "leftElevatorStopped":
                        if(symetrie) {
                            SensorState.RIGHT_ELEVATOR_MOVING.setData(false);
                        } else {
                            SensorState.LEFT_ELEVATOR_MOVING.setData(false);
                        }
                        break;

                    case "rightElevatorStopped":
                        if(symetrie) {
                            SensorState.LEFT_ELEVATOR_MOVING.setData(false);
                        } else {
                            SensorState.RIGHT_ELEVATOR_MOVING.setData(false);
                        }
                        break;

                    case "confirmOrder":
                        if(event.length >= 2) {
                            Log.COMMUNICATION.debug("Received confirmation for order ("+event[1]+")");
                            SensorState.ACTUATOR_ACTUATING.setData(false);
                        } else {
                            Log.COMMUNICATION.critical("Erreur dans l'event 'actuatorFinished', il manque l'ordre!");
                        }
                        break;

                }
            }
            if (sickData.peek() != null) {
                if (isMaster) {
                    sickMeasurements = sickData.poll().split(ARGUMENTS_SEPARATOR);
                    System.out.println("=== SICK ===");
                    for(String s : sickMeasurements) {
                        System.out.print(s+" ");
                    }
                    System.out.println();
                    System.out.println("============");
                    int[] significantSicks = Sick.getSignificantSicks();
                    VectCartesian vectsick = new VectCartesian(104,87); //Vecteur qui place les sick par rapport à l'origine du robot

                    int dsick = 173;
                    int esick = Integer.parseInt(sickMeasurements[significantSicks[1]]) - Integer.parseInt(sickMeasurements[significantSicks[2]]);
                    double rapport = 1.0* esick / dsick;
                    int xCalcule;
                    int yCalcule;
                    double teta;

                    // FIXME
                    if (ConfigData.COULEUR.toString().equals("jaune")) {
                        // On différencie les cas où le robot est orienté vers la gauche et la droite
                        double orien= XYO.getRobotInstance().getOrientation();

                        teta = Math.atan(rapport);
                        xCalcule = (int) Math.round((1500 - (Integer.parseInt(sickMeasurements[significantSicks[0]])+ vectsick.getX()) * Math.cos(teta)));
                        if (-Math.PI/2 < orien && orien < Math.PI/2) { //modifier car arctan est toujours inférieur à PI
                            if (significantSicks[1] == 4 || significantSicks[1] == 5) {
                                yCalcule = (int) Math.round((2000 - (Integer.parseInt(sickMeasurements[significantSicks[2]])+ vectsick.getY()) * Math.cos(teta)));
                            } else {
                                yCalcule = (int) Math.round(((Integer.parseInt(sickMeasurements[significantSicks[2]])+vectsick.getY()) * Math.cos(teta)));
                            }
                        } else {
                            if (significantSicks[1] == 4 || significantSicks[1] == 5) {
                                yCalcule = (int) Math.round((Integer.parseInt(sickMeasurements[significantSicks[2]])+vectsick.getY()) * Math.cos(teta));
                            } else {
                                yCalcule = (int) Math.round(2000 - (Integer.parseInt(sickMeasurements[significantSicks[2]])+vectsick.getY()) * Math.cos(teta));
                            }

                        }
                    } else {
                        double orien= XYO.getRobotInstance().getOrientation();
                        teta = Math.PI - Math.atan(rapport);
                        xCalcule = (int) ((Integer.parseInt(sickMeasurements[significantSicks[0]])+vectsick.getX()) * Math.cos(teta)) - 1500;
                        if (-Math.PI/2 < orien && orien < Math.PI/2) {
                            if (significantSicks[1] == 1 || significantSicks[1] == 2) {
                                yCalcule = (int) Math.round(2000 - (Integer.parseInt(sickMeasurements[significantSicks[2]])+vectsick.getY()) * Math.cos(teta));
                            } else {
                                yCalcule = (int) Math.round((Integer.parseInt(sickMeasurements[significantSicks[2]])+vectsick.getY()) * Math.cos(teta));
                            }
                        } else {
                            if (significantSicks[1] == 4 || significantSicks[1] == 5) {
                                yCalcule = (int) Math.round((Integer.parseInt(sickMeasurements[significantSicks[2]])+vectsick.getY()) * Math.cos(teta));
                            } else {
                                yCalcule = (int) Math.round(2000 - (Integer.parseInt(sickMeasurements[significantSicks[2]])+vectsick.getY()) * Math.cos(teta));
                            }
                        }
                    }
                    VectCartesian newPosition = new VectCartesian(xCalcule, yCalcule);
                    double newOrientation = teta + Math.PI;
                    XYO newXYO = new XYO(newPosition, newOrientation);
                    Sick.setNewXYO(newXYO);
                }
                else {
                    sickMeasurements = sickData.poll().split(ARGUMENTS_SEPARATOR);
                    int dsick = 50;
                    int esick = Integer.parseInt(sickMeasurements[1]) - Integer.parseInt(sickMeasurements[2]);
                    double rapport = 1.0 * esick / dsick;
                    int xCalcule;
                    int yCalcule;
                    double teta;

                    if (ConfigData.COULEUR.toString().equals("jaune")) {
                        // On différencie les cas où le robot est orienté vers la gauche et la droite

                        teta = Math.atan(rapport);
                        xCalcule = (int) Math.round(1500 - Integer.parseInt(sickMeasurements[1]) * Math.cos(teta));
                        yCalcule = (int) Math.round(Integer.parseInt(sickMeasurements[0]) * Math.cos(teta));
                    } else {
                        teta = Math.PI - Math.atan(rapport);
                        xCalcule = (int) Math.round(Integer.parseInt(sickMeasurements[0]) * Math.cos(teta)) - 1500;
                        yCalcule = (int) Math.round(2000 - Integer.parseInt(sickMeasurements[2]) * Math.cos(teta));
                    }

                    VectCartesian newPosition = new VectCartesian(xCalcule, yCalcule);
                    double newOrientation = teta + Math.PI;
                    XYO newXYO = new XYO(newPosition, newOrientation);
                    Sick.setNewXYO(newXYO);
                }

            }

            if(couleurPalet.peek()!=null){
                String couleur = couleurPalet.poll();
                CouleurPalet.setCouleurPalRecu(couleur);
            }
        }
    }

    @Override

    public void updateConfig(Config config) {
        this.isMaster = config.getBoolean(ConfigData.MASTER);
        this.symetrie = config.getString(ConfigData.COULEUR).equals("jaune");

    }
}
