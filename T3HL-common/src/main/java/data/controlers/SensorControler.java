package data.controlers;

import data.*;
import pfg.config.Config;
import utils.ConfigData;
import utils.Log;
import utils.MatchTimer;
import utils.container.Service;
import utils.math.Calculs;
import utils.math.Vec2;
import utils.math.VectCartesian;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;
import java.util.function.Function;

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
    private MatchTimer timer;

    /**
     * Liste des ChannelHandlers
     */
    private ArrayList<ChannelHandler> channelHandlers = new ArrayList<ChannelHandler>();

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

   // private PrintWriter sickWriter;

    private int measureIndex = 0;

    private int offsetSick= 6;

    /**
     * Construit un gestionnaire de capteur
     * @param listener
     *              le listener
     */
    public SensorControler(Listener listener, MatchTimer timer) throws FileNotFoundException, UnsupportedEncodingException {
        this.listener = listener;
        this.timer = timer;
        /*
        this.sickWriter = new PrintWriter("./sick-"+System.currentTimeMillis()+".csv", StandardCharsets.UTF_16.name());
        sickWriter.print("Indice");
        for (Sick sick : Sick.values()) {
            sickWriter.print("\t"+sick.name());
        }
        sickWriter.println();*/
        registerChannelHandler(Channel.ROBOT_POSITION, this::handleRobotPos);
        registerChannelHandler(Channel.BUDDY_POSITION, this::handleBuddyPos);
        registerChannelHandler(Channel.BUDDY_PATH, this::handleBuddyPath);
        registerChannelHandler(Channel.BUDDY_PALETS, this::handleBuddyPalet);
        registerChannelHandler(Channel.BUDDY_SCRIPT_ORDER, this::handleBuddyScriptOrder);
        registerChannelHandler(Channel.EVENT, this::handleEvent);
        registerChannelHandler(Channel.SICK, this::handleSick);
        registerChannelHandler(Channel.COULEUR_PALET_PRIS, this::handleCouleurPalet);
    }

    /**
     * Fonction de liaison entre un channel et une fonction traitant les messages reçus
     * @param channel channel à lier
     * @param function fonction à lier
     */
    private void registerChannelHandler(Channel channel, Consumer<String> function){
        this.channelHandlers.add(new ChannelHandler(this.listener, channel, function));
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
            for (ChannelHandler channelHandler : this.channelHandlers){
                channelHandler.checkAndHandle();
            }
        }
    }

    /**
     * POSITION
     */
    private void handleRobotPos(String message) {
        String[] coordonates = message.split(ARGUMENTS_SEPARATOR);
        int x = Math.round(Float.parseFloat(coordonates[0]));
        int y = Math.round(Float.parseFloat(coordonates[1]));
        double o = Double.parseDouble(coordonates[2]);
     //   Log.COMMUNICATION.debug("Received pos from LL: "+x+" "+y+" "+o);
        if (symetrie) {
            x = -x;
            o = Math.PI - o;
        }
        o = Calculs.modulo(o, Math.PI);
        // System.out.println("LL: "+XYO.getRobotInstance());
        XYO.getRobotInstance().update(x, y, o);
//        Log.COMMUNICATION.debug("Updated pos from LL after computation: "+XYO.getRobotInstance());
    }

    /**
     * EVENT
     */
    private void handleEvent(String message) {
        if (!message.equals("pong")) { // ne log pas les pongs
            Log.COMMUNICATION.debug("Got event from LL: " + message);
        }
        String[] event = message.split(ARGUMENTS_SEPARATOR);
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

            case "electron_activated":
                SensorState.ELECTRON_ACTIVATED.setData(true);
                break;

            case "electron_arrived":
                SensorState.ELECTRON_ARRIVED.setData(true);
                break;

            case "gogogofast": {
                timer.resetTimer();
                SensorState.WAITING_JUMPER.setData(false);
                break;
            }
        }
    }

    /**
     * SICK
     */
    private void handleSick(String message){
        String[] sickMeasurementsStr = message.split(ARGUMENTS_SEPARATOR);
        System.out.println("=== SICK ===");
        for(int i = 0; i < sickMeasurementsStr.length; i++) {
            // permet d'éviter de réextraire les valeurs du String qu'on reçoie
            sickMeasurements[i] = Integer.parseInt(sickMeasurementsStr[i]);
            System.out.print(sickMeasurementsStr[i]+" ");

        }
        System.out.println();
     /*   sickWriter.println(String.format("%d\t%d\t%d\t%d\t%d\t%d\t%d", measureIndex++, sickMeasurements[0], sickMeasurements[1], sickMeasurements[2], sickMeasurements[3], sickMeasurements[4], sickMeasurements[5]));
        sickWriter.flush();*/
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

            if (symetrie) {
                // On différencie les cas où le robot est orienté vers la gauche et la droite
                if (-Math.PI/2 < orien && orien < Math.PI/2) {
                    if (significantSicks[1] == Sick.SICK_ARRIERE_DROIT || significantSicks[1] == Sick.SICK_AVANT_DROIT) {
                        teta = Math.atan(rapport);
                        yCalcule = (int) Math.round((2000 - (sickMeasurements[significantSicks[2].getIndex()]+ vectsick.getY()+offsetSick) * Math.cos(teta)));
                    } else {
                        teta = Math.atan(-rapport);
                        yCalcule = (int) Math.round(((sickMeasurements[significantSicks[2].getIndex()]+vectsick.getY()+offsetSick) * Math.cos(teta)));
                    }
                } else {
                    if (significantSicks[1] == Sick.SICK_ARRIERE_DROIT || significantSicks[1] == Sick.SICK_AVANT_DROIT) {
                        teta = Math.atan(rapport);
                        yCalcule = (int) Math.round((sickMeasurements[significantSicks[2].getIndex()]+vectsick.getY()+offsetSick) * Math.cos(teta));
                    } else {
                        teta = Math.atan(-rapport);
                        yCalcule = (int) Math.round(2000 - (sickMeasurements[significantSicks[2].getIndex()]+vectsick.getY()+offsetSick) * Math.cos(teta));
                    }


                }
                xCalcule = (int) Math.round(-(1500 - (sickMeasurements[significantSicks[0].getIndex()]+ vectsick.getX()+offsetSick) * Math.cos(teta)));
                teta = Math.PI-teta;
            } else {
                System.out.println(orien);
                if (-Math.PI/2 < orien && orien < Math.PI/2) {
                    if (significantSicks[1] == Sick.SICK_AVANT_GAUCHE || significantSicks[1] == Sick.SICK_ARRIERE_GAUCHE) {
                        teta = Math.atan(-rapport);
                        yCalcule = (int) Math.round(2000 - (sickMeasurements[significantSicks[2].getIndex()]+vectsick.getY()+offsetSick) * Math.cos(teta));
                    } else {
                        teta = Math.atan(rapport);
                        yCalcule = (int) Math.round((sickMeasurements[significantSicks[2].getIndex()]+vectsick.getY()+offsetSick) * Math.cos(teta));
                    }
                } else {
                    if (significantSicks[1] == Sick.SICK_AVANT_GAUCHE || significantSicks[1] == Sick.SICK_ARRIERE_GAUCHE) {
                        teta = Math.atan(-rapport);
                        yCalcule = (int) Math.round((sickMeasurements[significantSicks[2].getIndex()]+vectsick.getY()+offsetSick) * Math.cos(teta));
                    } else {
                        teta = Math.atan(rapport);
                        yCalcule = (int) Math.round(2000 - (sickMeasurements[significantSicks[2].getIndex()]+vectsick.getY()+offsetSick) * Math.cos(teta));
                    }
                }
                xCalcule = 1500 - (int) ((sickMeasurements[significantSicks[0].getIndex()]+vectsick.getX()) * Math.cos(teta));
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
        newOrientation=Calculs.modulo(newOrientation, Math.PI);
        XYO newXYO = new XYO(newPosition, newOrientation);
        Sick.setNewXYO(newXYO);
    }

    /**
     * COULEUR_PALETS
     */
    private void handleCouleurPalet(String message){
        CouleurPalet.setCouleurPalRecu(message);
    }

    /**
     * BUDDY : position
     */
    private void handleBuddyPos(String message){
        String[] coordonates = message.split(ARGUMENTS_SEPARATOR);
        XYO.getBuddyInstance().update(Integer.parseInt(coordonates[0]), Integer.parseInt(coordonates[1]), Double.parseDouble(coordonates[2]));
    }

    /**
     * BUDDY : chemin du buddy
     */
    private void handleBuddyPath(String message){
        String[] pathString = message.split(ARGUMENTS_SEPARATOR);
        ArrayList<Vec2> path = new ArrayList<Vec2>();
        for(int i=0; i < pathString.length; i+=2){
            path.add(new VectCartesian(Integer.parseInt(pathString[i]), Integer.parseInt(pathString[i+1])));
        }
        BuddyState.CHEMIN_BUDDY.setData(path);
    }

    /**
     * BUDDY : palets pris par le buddy
     */
    private void handleBuddyPalet(String message){
        String[] paletsString = message.split(ARGUMENTS_SEPARATOR);
        Palet palet = Palet.getPaletById(Integer.parseInt(paletsString[0]));
        if (palet != null) {
            palet.setPaletPris(true);
        }
    }

    /**
     * BUDDY : ordres donnés par le principal
     */
    private void handleBuddyScriptOrder(String message){
        String[] scriptString = message.split(ARGUMENTS_SEPARATOR);
        String script = scriptString[0];
        int version = Integer.parseInt(scriptString[1]);
        BuddyState.CURRENT_SCRIPT_NAME.setData(script);
        BuddyState.CURRENT_SCRIPT_VERSION.setData(version);
    }


    @Override
    public void updateConfig(Config config) {
        this.isMaster = config.getBoolean(ConfigData.MASTER);
        this.symetrie = config.getString(ConfigData.COULEUR).equals("violet");
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    //    sickWriter.close();
    }
}
