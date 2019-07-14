package scripts;

import data.SensorState;
import data.Sick;
import data.XYO;
import data.controlers.AudioPlayer;
import locomotion.UnableToMoveException;
import orders.Speed;
import orders.order.ActuatorsOrder;
import pfg.config.Configurable;
import robot.Robot;
import utils.Container;
import utils.Log;
import utils.Offsets;
import utils.container.Module;
import utils.math.Calculs;
import utils.math.Vec2;
import utils.math.VectCartesian;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class Accelerateur extends Script implements Offsets {

    /**
     * Boolean de symétrie
     */
    @Configurable
    private boolean symetry = false;

    float distanceToWall;

    /**
     * Offset avec la planche
     */
    private final int offsetRecalage = 36;

    /*
     * Offset pour corriger la mesure des sicks (différence réel - mesuré)
     */
    private final int offsetSick= 6;

    /**
     * Différence en Y et X entre le sick et le centre du robot
     */
    private final int ySickToRobotCenter=113;

    /**
     * Distance entre les sicks et rapport entre dsick et écart de valeures mesurées pour faire un recalage en rotation
     */

    private final int dsick = 173;
    double rapport ;
    double ecart_mesures_sicks;
    double teta;
    //variable pour le calcul du recalage
    int yEntryPostRecalage = 410-78+15-4-5;
    private CompletableFuture<Void> recalageLeft;
    private CompletableFuture<Void> recalageRight;


    private int distavance = 0;
    private int palet = 90;
    
    private AudioPlayer audioPlayer;

    final int decalageAccelerateur = -50;


    /**
     * Est-ce qu'on se recale à l'accélérateur?
     */
    @Configurable
    private boolean recalageAcc;
    @Configurable
    private boolean recalageMecaAcc;

    public Accelerateur(Container container) {
        super(container);
        versions = new ArrayList<>();
        versions.add(0);  //version initiale (7 palets)
        versions.add(1);  //version pour mettre 7 palets + le bleu initial
        this.audioPlayer = robot.getAudioPlayer();
    }

    @Override
    public void execute(int version) {
        try {
            int yEntryPostRecalageAvecSymetrie = yEntryPostRecalage;
            if (version == 1) {
                if (!symetry) {
                    yEntryPostRecalageAvecSymetrie += Offsets.get(ACCELERATEUR_Y_JAUNE);     // difference due aux positions des bras
                } else {
                    yEntryPostRecalageAvecSymetrie += Offsets.get(ACCELERATEUR_Y_VIOLET);
                }
            }
            if(recalageAcc) {
                recalageAccelerateur(yEntryPostRecalageAvecSymetrie);
            }
            recalageRight.join();
            double offsetTheta = Offsets.get(ACCELERATEUR_THETA_RECALAGE_JAUNE);
            double offsetThetaAutreCote = Offsets.get(ACCELERATEUR_THETA_RECALAGE_JAUNE_COTE_2);
            if(symetry) {
                offsetTheta = Offsets.get(ACCELERATEUR_THETA_RECALAGE_VIOLET);
                offsetThetaAutreCote = Offsets.get(ACCELERATEUR_THETA_RECALAGE_VIOLET_COTE_2);
            }
            if(recalageMecaAcc) {
                turn(Math.PI/2);
                double recalageY = Offsets.get(ACCELERATEUR_Y_RECALAGE_JAUNE);
                if(symetry) {
                    recalageY = Offsets.get(ACCELERATEUR_Y_RECALAGE_VIOLET);
                }
                robot.recalageMeca(false, (int)recalageY);
                robot.setOrientation(Math.PI/2);
                turn(offsetTheta+0);
            }

            while (robot.getNbPaletsDroits() > 0) {
                storePuck(version, robot);
            }
            actuators.RIGHT_VALVE.desactivate();

            turn(Math.PI-offsetThetaAutreCote);
            recalageLeft.join();
            robot.invertOrders(robot -> {
               while (robot.getNbPaletsDroits() > 0) {
                   storePuck(version, robot);
               }
            });
            actuators.LEFT_VALVE.desactivate();

        } catch (UnableToMoveException e) {
            e.printStackTrace();
        }
    }

    private void storePuck(int version, Robot robot) {
        Module.waitWhileTrue(SensorState.RIGHT_ELEVATOR_MOVING::getData);
        actuators.RIGHT_VALVE.desactivate(true);
        if (version == 0) {
            robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_AU_DESSUS_ACCELERATEUR);
            robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_ACCELERATEUR_DEPOT, true);
        } else if (version == 1) {
            robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_ACCELERATEUR_DEPOT_7_PALETS, true);
        }
        robot.increaseScore(10);

        if (robot.getNbPaletsDroits() > 1) {
            SensorState.RIGHT_ELEVATOR_MOVING.setData(true);
            actuators.RIGHT_ELEVATOR.up();
        }
        actuators.RIGHT_VALVE.activate(true);
        robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_ASCENSEUR, true);
        robot.popPaletDroit();
    }


    public void recalageAccelerateur(int yEntry) throws UnableToMoveException {
        turn(Math.PI);
        robot.computeNewPositionAndOrientation(Sick.NOTHING);
        if(symetry) {
            ecart_mesures_sicks=Sick.SICK_AVANT_DROIT.getLastMeasure() - Sick.SICK_ARRIERE_DROIT.getLastMeasure();
            rapport = ecart_mesures_sicks / dsick;
            teta = Math.atan(-rapport);
            distanceToWall = (float) (Math.cos(teta)*((Sick.SICK_ARRIERE_DROIT.getLastMeasure() + Sick.SICK_AVANT_DROIT.getLastMeasure()) / 2 + offsetSick + ySickToRobotCenter));
            Log.POSITION.critical("symetrie" + Sick.SICK_ARRIERE_DROIT.getLastMeasure() + " " + Sick.SICK_AVANT_DROIT.getLastMeasure() + " " + distanceToWall);
        }
        else {
            ecart_mesures_sicks=Sick.SICK_AVANT_GAUCHE.getLastMeasure() - Sick.SICK_ARRIERE_GAUCHE.getLastMeasure();
            rapport = ecart_mesures_sicks / dsick;
            teta = Math.atan(-rapport);
            distanceToWall = (float) (Math.cos(teta)*((Sick.SICK_AVANT_GAUCHE.getLastMeasure() + Sick.SICK_ARRIERE_GAUCHE.getLastMeasure()) / 2 + offsetSick + ySickToRobotCenter));
            Log.POSITION.critical("no symetrie" + Sick.SICK_AVANT_GAUCHE.getLastMeasure() + " " + Sick.SICK_ARRIERE_GAUCHE.getLastMeasure() + " " + distanceToWall);
        }
        Vec2 currentPosition = XYO.getRobotInstance().getPosition();
        robot.setPositionAndOrientation(new VectCartesian(currentPosition.getX(), distanceToWall + offsetRecalage), Calculs.modulo(teta+Math.PI, Math.PI));
        robot.gotoPoint(new VectCartesian(currentPosition.getX()-decalageAccelerateur, yEntry));
    }

    @Override
    protected boolean shouldContinueScript(Exception e) {
        return false;
    }

    @Override
    public void executeWhileMovingToEntry(int version) {
        robot.useActuator(ActuatorsOrder.REBOOT_LES_BRAS, true);
        try {
            TimeUnit.MILLISECONDS.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        robot.useActuator(ActuatorsOrder.ACTIVE_COUPLE_DU_BRAS_GAUCHE);
        robot.useActuator(ActuatorsOrder.ACTIVE_COUPLE_DU_BRAS_DROIT);
        recalageLeft = async("Recalage ascenseur gauche", () -> {
            robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_LIBERE_ASCENSEUR, true);
            actuators.LEFT_ELEVATOR.updown(true);
            robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_ASCENSEUR, true);
            actuators.LEFT_PUMP.activate();
            actuators.LEFT_VALVE.desactivate(true);
            robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_LIBERE_ASCENSEUR, true);
            robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_ASCENSEUR, true);
        });
        recalageRight = async("Recalage ascenseur droit", () -> {
            //robot.useActuator(ActuatorsOrder.MONTE_ASCENCEUR_DROIT_DE_UN_PALET);
            // robot.waitForRightElevator();
            robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_LIBERE_ASCENSEUR, true);
            actuators.RIGHT_ELEVATOR.updown(true);
            robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_ASCENSEUR, true);
            actuators.RIGHT_PUMP.activate();
            actuators.RIGHT_VALVE.desactivate(true);
            robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_LIBERE_ASCENSEUR, true);
            robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_ASCENSEUR, true);
        });
    }

    @Override
    public Vec2 entryPosition(int version) {
        if (version == 1) {
            if (symetry) {
                return new VectCartesian(-490 + 10 + 76 + decalageAccelerateur, 190+210 - 78 + 50 + 10+Offsets.get(ACCELERATEUR_Y_VIOLET));
            }
            else {
                return new VectCartesian(-490 + 10 + 76 + decalageAccelerateur, 190+210 - 78 + 50 + 10+Offsets.get(ACCELERATEUR_Y_JAUNE));
            }
        } else if (version == 0) {
            if (symetry) {
                return new VectCartesian(-490 + 10 + decalageAccelerateur, 190+210 - 78 + 50 + 10+Offsets.get(ACCELERATEUR_Y_VIOLET));
            }
            else {
                return new VectCartesian(-490 + 10 + decalageAccelerateur, 190+210 - 78 + 50 + 10+Offsets.get(ACCELERATEUR_Y_JAUNE));
            }
        }
        return null;

    }

    @Override
    public void finalize(Exception e) {
        robot.setSpeed(Speed.DEFAULT_SPEED);
    }

}

