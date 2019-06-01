package scripts;

import data.SensorState;
import data.Sick;
import data.Table;
import data.XYO;
import locomotion.UnableToMoveException;
import orders.Speed;
import orders.order.ActuatorsOrder;
import pfg.config.Config;
import robot.Master;
import utils.ConfigData;
import utils.Container;
import utils.Log;
import utils.Offsets;
import utils.math.Calculs;
import utils.math.Vec2;
import utils.math.VectCartesian;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class Accelerateur extends Script {

    /**
     * Position d'entrée du script
     */
    //private final int xEntry = -490+10;
    //private final int yEntry = 410-78+50+10;
    private final Container container;

    /**
     * Boolean de symétrie
     */
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



    final int decalageAccelerateur = -100;


    /**
     * Est-ce qu'on se recale à l'accélérateur?
     */
    private boolean recalageAcc;
    private boolean recalageMecaAcc;

    public Accelerateur(Master robot, Table table, Container container) {
        super(robot, table);
        this.container = container;
        versions = new ArrayList<>();
        versions.add(0);  //version initiale (7 palets)
        versions.add(1);  //version pour mettre 7 palets + le bleu initial
    }

    @Override
    public void execute(Integer version) {
        try {
            int yEntryPostRecalageAvecSymetrie = yEntryPostRecalage;
            if (version == 1) {
                if (!symetry) {
                    yEntryPostRecalageAvecSymetrie += Offsets.ACCELERATEUR_Y_JAUNE.get();     // difference due aux positions des bras
                } else {
                    yEntryPostRecalageAvecSymetrie += Offsets.ACCELERATEUR_Y_VIOLET.get();
                }
            }
            if(recalageAcc) {
                recalageAccelerateur(yEntryPostRecalageAvecSymetrie);
            }
            recalageRight.join();
            double offsetTheta = Offsets.ACCELERATEUR_THETA_RECALAGE_JAUNE.get();
            double offsetThetaAutreCote = Offsets.ACCELERATEUR_THETA_RECALAGE_JAUNE_COTE_2.get();
            if(symetry) {
                offsetTheta = Offsets.ACCELERATEUR_THETA_RECALAGE_VIOLET.get();
                offsetThetaAutreCote = Offsets.ACCELERATEUR_THETA_RECALAGE_VIOLET_COTE_2.get();
            }
            if(recalageMecaAcc) {
                robot.turn(Math.PI/2);
                robot.recalageMeca(false, (int)Offsets.ACCELERATEUR_Y_RECALAGE.get());
                robot.setOrientation(Math.PI/2);
                robot.turn(offsetTheta+0);
            }

            while (robot.getNbPaletsDroits() > 0) {
                robot.waitWhileTrue(SensorState.RIGHT_ELEVATOR_MOVING::getData);
                robot.useActuator(ActuatorsOrder.DESACTIVE_ELECTROVANNE_DROITE, true);
                if (version == 0) {
                    robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_AU_DESSUS_ACCELERATEUR);
                    robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_ACCELERATEUR_DEPOT, true);
                } else if (version == 1) {
                    robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_ACCELERATEUR_DEPOT_7_PALETS, true);
                }
                robot.increaseScore(10);

                if (robot.getNbPaletsDroits() > 1) {
                    SensorState.RIGHT_ELEVATOR_MOVING.setData(true);
                    robot.useActuator(ActuatorsOrder.MONTE_ASCENCEUR_DROIT_DE_UN_PALET);
                }
                robot.useActuator(ActuatorsOrder.ACTIVE_ELECTROVANNE_DROITE, true);
                robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_ASCENSEUR, true);
                robot.popPaletDroit();
            }
            robot.useActuator(ActuatorsOrder.DESACTIVE_LA_POMPE_DROITE);

            robot.turn(Math.PI-offsetThetaAutreCote);
            recalageLeft.join();
            robot.invertOrders(robot -> {
               while (robot.getNbPaletsDroits() > 0) {
                   robot.waitWhileTrue(SensorState.RIGHT_ELEVATOR_MOVING::getData);
                   robot.useActuator(ActuatorsOrder.DESACTIVE_ELECTROVANNE_DROITE, true);
                   if (version == 0) {
                       robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_AU_DESSUS_ACCELERATEUR);
                       robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_ACCELERATEUR_DEPOT, true);
                   } else if (version == 1) {
                       robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_ACCELERATEUR_DEPOT_7_PALETS, true);
                   }
                   robot.increaseScore(10);

                   if (robot.getNbPaletsDroits() > 1) {
                       SensorState.RIGHT_ELEVATOR_MOVING.setData(true);
                       robot.useActuator(ActuatorsOrder.MONTE_ASCENCEUR_DROIT_DE_UN_PALET);
                   }
                   robot.useActuator(ActuatorsOrder.ACTIVE_ELECTROVANNE_DROITE, true);
                   robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_ASCENSEUR, true);
                   robot.popPaletDroit();
               }
           });
            robot.useActuator(ActuatorsOrder.DESACTIVE_LA_POMPE_GAUCHE);

        } catch (UnableToMoveException e) {
            e.printStackTrace();
        }
    }


    public void recalageAccelerateur(int yEntry) throws UnableToMoveException {
        robot.turn(Math.PI);
        robot.computeNewPositionAndOrientation(Sick.NOTHING);
        if(container.getConfig().getString(ConfigData.COULEUR).equals("violet")) {
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
            robot.useActuator(ActuatorsOrder.MONTE_DESCEND_ASCENCEUR_GAUCHE_DE_UN_PALET, true);
            robot.waitForLeftElevator();
            robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_ASCENSEUR, true);
            robot.useActuator(ActuatorsOrder.ACTIVE_LA_POMPE_GAUCHE);
            robot.useActuator(ActuatorsOrder.DESACTIVE_ELECTROVANNE_GAUCHE, true);
            robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_LIBERE_ASCENSEUR, true);
            robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_ASCENSEUR, true);
        });
        recalageRight = async("Recalage ascenseur droit", () -> {
            //robot.useActuator(ActuatorsOrder.MONTE_ASCENCEUR_DROIT_DE_UN_PALET);
           // robot.waitForRightElevator();
            robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_LIBERE_ASCENSEUR, true);
            robot.useActuator(ActuatorsOrder.MONTE_DESCEND_ASCENCEUR_DROIT_DE_UN_PALET, true);
            robot.waitForRightElevator();
            robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_ASCENSEUR, true);
            robot.useActuator(ActuatorsOrder.ACTIVE_LA_POMPE_DROITE);
            robot.useActuator(ActuatorsOrder.DESACTIVE_ELECTROVANNE_DROITE, true);
            robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_LIBERE_ASCENSEUR, true);
            robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_ASCENSEUR, true);
        });
    }

    @Override
    public Vec2 entryPosition(Integer version) {
        if (version == 1) {
            if (symetry) {
                return new VectCartesian(-490 + 10 + 76 + decalageAccelerateur, 190+210 - 78 + 50 + 10+Offsets.ACCELERATEUR_Y_VIOLET.get());
            }
            else {
                return new VectCartesian(-490 + 10 + 76 + decalageAccelerateur, 190+210 - 78 + 50 + 10+Offsets.ACCELERATEUR_Y_JAUNE.get());
            }
        } else if (version == 0) {
            if (symetry) {
                return new VectCartesian(-490 + 10 + decalageAccelerateur, 190+210 - 78 + 50 + 10+Offsets.ACCELERATEUR_Y_VIOLET.get());
            }
            else {
                return new VectCartesian(-490 + 10 + decalageAccelerateur, 190+210 - 78 + 50 + 10+Offsets.ACCELERATEUR_Y_JAUNE.get());
            }
        }
        return null;

    }

    @Override
    public void finalize(Exception e) {
        robot.setSpeed(Speed.DEFAULT_SPEED);
    }

    @Override
    public void updateConfig(Config config) {
        super.updateConfig(config);
        this.symetry = config.getString(ConfigData.COULEUR).equals("violet");
        this.recalageAcc = config.getBoolean(ConfigData.RECALAGE_ACC);
        this.recalageMecaAcc = config.getBoolean(ConfigData.RECALAGE_MECA_ACC);
    }

}

