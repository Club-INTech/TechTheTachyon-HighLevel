package data.controlers;

import data.*;
import pfg.config.Config;
import utils.ConfigData;
import utils.Log;
import utils.container.Service;
import utils.math.Calculs;
import utils.math.Vec2;
import utils.math.VectCartesian;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
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
    private ConcurrentLinkedQueue<String> buddyPathQueue;
    private ConcurrentLinkedQueue<String> buddyScriptOrderQueue;
    private ConcurrentLinkedQueue<String> buddyPaletsQueue;
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
        this.buddyPathQueue = new ConcurrentLinkedQueue<>();
        this.buddyScriptOrderQueue = new ConcurrentLinkedQueue<>();
        this.buddyPaletsQueue = new ConcurrentLinkedQueue<>();
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
        listener.addQueue(Channel.BUDDY_PATH, buddyPathQueue);
        listener.addQueue(Channel.BUDDY_SCRIPT_ORDER, buddyScriptOrderQueue);
        listener.addQueue(Channel.BUDDY_PALETS, buddyPaletsQueue);
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
            if (!robotPosQueue.isEmpty()){
                handleRobotPos();
            }
            if (!sickData.isEmpty()){
                handleSick();
            }
            if (!eventData.isEmpty()){
                handleEvent();
            }
            if (!couleurPalet.isEmpty()){
                handleEvent();
            }
            if (!buddyPosQueue.isEmpty()){
                handleBuddyPos();
            }
            if (!buddyPathQueue.isEmpty()){
                handleBuddyPath();
            }
            if (!buddyPaletsQueue.isEmpty()){
                handleBuddyPalets();
            }
            if (!buddyScriptOrderQueue.isEmpty()){
                handleBuddyScriptOrder();
            }
        }
    }

    /**
     * POSITION
     */
    private void handleRobotPos() {
        String[] coordonates = robotPosQueue.poll().split(ARGUMENTS_SEPARATOR);
        int x = Math.round(Float.parseFloat(coordonates[0]));
        int y = Math.round(Float.parseFloat(coordonates[1]));
        double o = Double.parseDouble(coordonates[2]);
        if (symetrie) {
            x = -x;
            o = Calculs.modulo(Math.PI - o, Math.PI);
        }
       // System.out.println("LL: "+XYO.getRobotInstance());
        XYO.getRobotInstance().update(x, y, o);
    }

    /**
     * EVENT
     */
    private void handleEvent() {
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

    /**
     * SICK
     */
    private void handleSick(){
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
                System.out.println(orien);
                teta = Math.atan(-rapport);
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
                xCalcule = (int) ((sickMeasurements[significantSicks[0].getIndex()]+vectsick.getX()) * Math.cos(teta)) - 1500;
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
        double orien = XYO.getRobotInstance().getOrientation();
        if (-Math.PI/2 < orien && orien < Math.PI/2) {
            newOrientation -= Math.PI;
        }
        XYO newXYO = new XYO(newPosition, newOrientation);
        Sick.setNewXYO(newXYO);
    }

    /**
     * COULEUR_PALETS
     */
    private void handleCouleurPalet(){
        String couleur = couleurPalet.poll();
        CouleurPalet.setCouleurPalRecu(couleur);
    }

    /**
     * BUDDY : position
     */
    private void handleBuddyPos(){
        String[] coordonates = buddyPosQueue.poll().split(ARGUMENTS_SEPARATOR);
        XYO.getBuddyInstance().update(Integer.parseInt(coordonates[0]), Integer.parseInt(coordonates[1]), Double.parseDouble(coordonates[2]));
    }

    /**
     * BUDDY : chemin du buddy
     */
    private void handleBuddyPath(){
        String[] pathString = buddyPathQueue.poll().split(ARGUMENTS_SEPARATOR);
        ArrayList<Vec2> path = new ArrayList<Vec2>();
        for(int i=0; i < pathString.length; i+=2){
            path.add(new VectCartesian(Integer.parseInt(pathString[i]), Integer.parseInt(pathString[i+1])));
        }
        //TODO : metter à jour une variable permettant de savoir quel chemin utilise buddy
    }

    /**
     * BUDDY : palets pris par le buddy
     */
    private void handleBuddyPalets(){
        String[] paletsString = buddyPaletsQueue.poll().split(ARGUMENTS_SEPARATOR);
        Palet palet = Palet.getPaletById(Integer.parseInt(paletsString[0]));
        if (palet != null) {
            palet.setPaletPris(true);
        }
    }

    /**
     * BUDDY : ordres donnés par le principal
     */
    private void handleBuddyScriptOrder(){
        String[] scriptString = buddyScriptOrderQueue.poll().split(ARGUMENTS_SEPARATOR);
        String script = scriptString[0];
        int version = Integer.parseInt(scriptString[1]);
        //TODO : metter à jour une variable permettant de savoir quel script nous est ordonné (de préférence une liste)
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
