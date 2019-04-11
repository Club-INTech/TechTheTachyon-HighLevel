package data.controlers;

import data.CouleurPalet;
import data.SensorState;
import data.Sick;
import data.XYO;
import pfg.config.Config;
import utils.ConfigData;
import utils.Log;
import utils.container.Service;
import utils.math.Calculs;
import utils.math.VectCartesian;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
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

    /**
     * True si master
     */
    private boolean isMaster;

    /**
     * Variables pour éviter de faire des new
     */

    private int[] sickMeasurements = new int[Sick.values().length];

    private PrintWriter sickWriter;

    private int measureIndex = 0;

    private int offsetSick= 6;

    /**
     * Construit un gestionnaire de capteur
     * @param listener
     *              le listener
     */
    public SensorControler(Listener listener) throws FileNotFoundException, UnsupportedEncodingException {
        this.listener = listener;
        this.robotPosQueue = new ConcurrentLinkedQueue<>();
        this.buddyPosQueue = new ConcurrentLinkedQueue<>();
        this.eventData=new ConcurrentLinkedQueue<>();
        this.sickData=new ConcurrentLinkedQueue<>();
        this.couleurPalet =new ConcurrentLinkedQueue<>();
        this.sickWriter = new PrintWriter("./sick-"+System.currentTimeMillis()+".csv", StandardCharsets.UTF_16.name());
        sickWriter.print("Indice");
        for (Sick sick : Sick.values()) {
            sickWriter.print("\t"+sick.name());
        }
        sickWriter.println();
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

        while (!Thread.currentThread().isInterrupted()) {
            while (robotPosQueue.peek() == null && buddyPosQueue.peek() == null && sickData.peek()==null && eventData.peek()==null && couleurPalet.peek()==null) {
                try {
                    Thread.sleep(TIME_LOOP);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            handleBuddyPos();
            handleRobotPos();
            handleEvent();
            handleSick();
            handleCouleurPalet();
        }
    }

    private void handleRobotPos() {
        if (robotPosQueue.peek() == null) {
            return;
        }
        String[] coordonates;
        int x;
        int y;
        double o;
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

    private void handleBuddyPos(){
        if (buddyPosQueue.peek() == null) {
            return;
        }
        String[] coordonates;
        int x;
        int y;
        double o;
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

    private void handleEvent() {
        if (eventData.peek() == null) {
            return;
        }
        String[] event;
        String data = eventData.poll();
        if (!data.equals("pong")) { // ne log pas les pongs
            Log.COMMUNICATION.debug("Got event from LL: " + data);
        }
        event = data.split(ARGUMENTS_SEPARATOR);
        switch (event[0]) {
            case "pong":
                SensorState.LAST_PONG.setData(System.currentTimeMillis());
                break;

            case "stoppedMoving":
                SensorState.MOVING.setData(false);
                break;

            case "leftElevatorStopped":
                if (symetrie) {
                    SensorState.RIGHT_ELEVATOR_MOVING.setData(false);
                } else {
                    SensorState.LEFT_ELEVATOR_MOVING.setData(false);
                }
                break;

            case "rightElevatorStopped":
                if (symetrie) {
                    SensorState.LEFT_ELEVATOR_MOVING.setData(false);
                } else {
                    SensorState.RIGHT_ELEVATOR_MOVING.setData(false);
                }
                break;

            case "confirmOrder":
                if (event.length >= 2) {
                    Log.COMMUNICATION.debug("Received confirmation for order (" + event[1] + ")");
                    SensorState.ACTUATOR_ACTUATING.setData(false);
                } else {
                    Log.COMMUNICATION.critical("Erreur dans l'event 'confirmOrder', il manque l'ordre!");
                }
                break;
        }
    }

    private void handleSick(){
        if (sickData.peek() == null) {
            return;
        }
        String[] sickMeasurementsStr = sickData.poll().split(ARGUMENTS_SEPARATOR);
        System.out.println("=== SICK ===");
        for(int i = 0; i < sickMeasurementsStr.length; i++) {
            // permet d'éviter de réextraire les valeurs du String qu'on reçoie
            sickMeasurements[i] = Integer.parseInt(sickMeasurementsStr[i]);
            System.out.print(sickMeasurementsStr[i]+" ");

        }
        System.out.println();
        sickWriter.println(String.format("%d\t%d\t%d\t%d\t%d\t%d\t%d", measureIndex++, sickMeasurements[0], sickMeasurements[1], sickMeasurements[2], sickMeasurements[3], sickMeasurements[4], sickMeasurements[5]));
        sickWriter.flush();
        System.out.println("============");
        Sick[] significantSicks = Sick.getSignificantSicks();
        int dsick;
        int esick = sickMeasurements[significantSicks[1].getIndex()] - sickMeasurements[significantSicks[2].getIndex()];
        int xCalcule;
        int yCalcule;
        double teta;

        if (isMaster) {
            dsick = 173;
            double rapport = ((double)esick) / dsick;
            VectCartesian vectsick = new VectCartesian(101,113); //Vecteur qui place les sick par rapport à l'origine du robot
            double orien= XYO.getRobotInstance().getOrientation();

            // TODO: TESTME
            if (symetrie) {
                // On différencie les cas où le robot est orienté vers la gauche et la droite
                teta = Math.atan(rapport);
                xCalcule = (int) Math.round((1500 - (sickMeasurements[significantSicks[0].getIndex()]+ vectsick.getX()+offsetSick) * Math.cos(teta)));
                if (-Math.PI/2 < orien && orien < Math.PI/2) { //modifier car arctan est toujours inférieur à PI
                    if (significantSicks[1] == Sick.SICK_ARRIERE_DROIT || significantSicks[1] == Sick.SICK_AVANT_DROIT) {
                        yCalcule = (int) Math.round((2000 - (sickMeasurements[significantSicks[2].getIndex()]+ vectsick.getY()+offsetSick) * Math.cos(teta)));
                    } else {
                        yCalcule = (int) Math.round(((sickMeasurements[significantSicks[2].getIndex()]+vectsick.getY()+offsetSick) * Math.cos(teta)));
                    }
                } else {
                    if (significantSicks[1] == Sick.SICK_ARRIERE_DROIT || significantSicks[1] == Sick.SICK_AVANT_DROIT) {
                        yCalcule = (int) Math.round((sickMeasurements[significantSicks[2].getIndex()]+vectsick.getY()+offsetSick) * Math.cos(teta));
                    } else {
                        yCalcule = (int) Math.round(2000 - (sickMeasurements[significantSicks[2].getIndex()]+vectsick.getY()+offsetSick) * Math.cos(teta));
                    }

                }
            } else {
                teta = Math.atan(rapport);
                xCalcule = (int) ((sickMeasurements[significantSicks[0].getIndex()]+vectsick.getX()) * Math.cos(teta)) - 1500;
                System.out.println(orien);
                if (-Math.PI/2 < orien && orien < Math.PI/2) {
                    if (significantSicks[1] == Sick.SICK_AVANT_GAUCHE || significantSicks[1] == Sick.SICK_ARRIERE_GAUCHE) {
                        yCalcule = (int) Math.round(2000 - (sickMeasurements[significantSicks[2].getIndex()]+vectsick.getY()+offsetSick) * Math.cos(teta));
                    } else {
                        yCalcule = (int) Math.round((sickMeasurements[significantSicks[2].getIndex()]+vectsick.getY()+offsetSick) * Math.cos(teta));
                    }
                } else {
                    if (significantSicks[1] == Sick.SICK_AVANT_GAUCHE || significantSicks[1] == Sick.SICK_ARRIERE_GAUCHE) {
                        yCalcule = (int) Math.round((sickMeasurements[significantSicks[2].getIndex()]+vectsick.getY()+offsetSick) * Math.cos(teta));
                    } else {
                        yCalcule = (int) Math.round(2000 - (sickMeasurements[significantSicks[2].getIndex()]+vectsick.getY()+offsetSick) * Math.cos(teta));
                    }
                }
            }
        }
        else {
            dsick = 50;
            double rapport = ((double)esick) / dsick;
            if (symetrie) {
                // On différencie les cas où le robot est orienté vers la gauche et la droite

                teta = Math.atan(rapport);
                xCalcule = (int) Math.round(1500 - sickMeasurements[1] * Math.cos(teta));
                yCalcule = (int) Math.round(sickMeasurements[0] * Math.cos(teta));
            } else {
                teta = Math.PI-Math.atan(rapport);
                xCalcule = (int) Math.round(sickMeasurements[0] * Math.cos(teta)) - 1500;
                yCalcule = (int) Math.round(2000 - sickMeasurements[2] * Math.cos(teta));
            }

        }
        VectCartesian newPosition = new VectCartesian(xCalcule, yCalcule);
        double newOrientation = teta + Math.PI;
        XYO newXYO = new XYO(newPosition, newOrientation);
        Sick.setNewXYO(newXYO);
    }

    private void handleCouleurPalet(){
        if(couleurPalet.peek()==null) {
            return;
        }
        String couleur = couleurPalet.poll();
        CouleurPalet.setCouleurPalRecu(couleur);
    }

    @Override
    public void updateConfig(Config config) {
        this.isMaster = config.getBoolean(ConfigData.MASTER);
        this.symetrie = config.getString(ConfigData.COULEUR).equals("jaune");
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        sickWriter.close();
    }
}
