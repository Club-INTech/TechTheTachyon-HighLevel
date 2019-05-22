package scripts;

import data.*;
import locomotion.UnableToMoveException;
import orders.Speed;
import orders.order.ActuatorsOrder;
import pfg.config.Config;
import robot.Master;
import utils.ConfigData;
import utils.Container;
import utils.Log;
import utils.Offsets;
import utils.container.ContainerException;
import utils.math.Calculs;
import utils.math.Vec2;
import utils.math.VectCartesian;

import java.math.MathContext;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

import static data.CouleurPalet.ROUGE;

/**
 * Script pour vider les ascenseurs dans la zone de départ si l'accélérateur est bloqué
 */
public class VideDansZoneDepartSiProbleme extends Script {

    /**
     * Position d'entrée du script
     */
    //private final int xEntry = -490+10;
    //private final int yEntry = 410-78+50+10;
    private final Container container;

    private boolean symetry=false;
    private boolean lastWasRed=true;


    private CompletableFuture<Void> recalageLeft;
    private CompletableFuture<Void> recalageRight;

    public VideDansZoneDepartSiProbleme(Master robot, Table table, Container container) {
        super(robot, table);
        this.container = container;
        versions = new ArrayList<>();
        versions.add(0);  //version initiale (7 palets)
        versions.add(1);  //version pour mettre 7 palets + le bleu initial
    }

    @Override
    public void execute(Integer version) {
        //TODO CHANGER LE SCORE
        try {
            robot.turn(Math.PI/2);
            robot.useActuator(ActuatorsOrder.ACTIVE_LA_POMPE_DROITE);
            recalageRight.join();
            while (robot.getNbPaletsDroits() > 0) {
                robot.useActuator(ActuatorsOrder.DESACTIVE_ELECTROVANNE_DROITE, true);
                if (robot.getRightElevatorOrNull() != null) {
                    if (robot.getRightElevatorOrNull().peek() == CouleurPalet.ROUGE && !lastWasRed) {
                        robot.moveLengthwise(300, false);
                        lastWasRed=true;
                    } else if (robot.getRightElevatorOrNull().peek() == CouleurPalet.VERT && lastWasRed) {
                        robot.moveLengthwise(-300, false);
                        lastWasRed=false;
                    }
                }
                robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_AU_DESSUS_ZONE_DEPART, true);

                if (robot.getNbPaletsDroits() > 1) {
                    SensorState.RIGHT_ELEVATOR_MOVING.setData(true);
                    robot.useActuator(ActuatorsOrder.MONTE_ASCENCEUR_DROIT_DE_UN_PALET,true);
                }
                robot.useActuator(ActuatorsOrder.ACTIVE_ELECTROVANNE_DROITE, true);
                robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_DE_LA_POSITION_AU_DESSUS_ZONE_DEPART_A_STOCKAGE, true);
                robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_ASCENSEUR, true);

                robot.increaseScore(6);

                if(robot.getRightElevatorOrNull() != null) {
                    robot.useActuator(ActuatorsOrder.DESACTIVE_LA_POMPE_DROITE,true);
                    robot.popPaletDroit();
                }
            }


            robot.turn(-Math.PI/2);
            robot.useActuator(ActuatorsOrder.ACTIVE_LA_POMPE_GAUCHE,true);
            recalageLeft.join();
            while (robot.getNbPaletsGauches() > 0) {
                robot.useActuator(ActuatorsOrder.DESACTIVE_ELECTROVANNE_GAUCHE, true);
                if (robot.getLeftElevatorOrNull() != null) {
                    if (robot.getLeftElevatorOrNull().peek() == CouleurPalet.ROUGE && !lastWasRed) {
                        robot.moveLengthwise(-300, false);
                        lastWasRed=true;
                    } else if (robot.getLeftElevatorOrNull().peek() == CouleurPalet.VERT && lastWasRed) {
                        robot.moveLengthwise(300, false);
                        lastWasRed=false;
                    }
                }
                robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_AU_DESSUS_ZONE_DEPART, true);
                if (robot.getNbPaletsGauches() > 1) {
                    SensorState.LEFT_ELEVATOR_MOVING.setData(true);
                    robot.useActuator(ActuatorsOrder.MONTE_ASCENCEUR_GAUCHE_DE_UN_PALET, true);
                }
                robot.useActuator(ActuatorsOrder.ACTIVE_ELECTROVANNE_GAUCHE, true);
                robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_GAUCHE_DE_LA_POSITION_AU_DESSUS_ZONE_DEPART_A_STOCKAGE, true);
                robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_ASCENSEUR, true);

                robot.increaseScore(6);

                if (robot.getRightElevatorOrNull() != null) {
                    robot.popPaletGauche();
                    robot.useActuator(ActuatorsOrder.DESACTIVE_LA_POMPE_DROITE,true);
                }
            }

        } catch (UnableToMoveException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void executeWhileMovingToEntry(int version) {
        recalageLeft = async("Recalage ascenseur gauche", () -> {
            robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_LIBERE_ASCENSEUR,true);
            robot.useActuator(ActuatorsOrder.MONTE_DESCEND_ASCENCEUR_GAUCHE_DE_UN_PALET, true);
            robot.waitForLeftElevator();
            robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_ASCENSEUR, true);
        });
        recalageRight = async("Recalage ascenseur droit", () -> {
            //robot.useActuator(ActuatorsOrder.MONTE_ASCENCEUR_DROIT_DE_UN_PALET);
            // robot.waitForRightElevator();
            robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_LIBERE_ASCENSEUR, true);
            robot.useActuator(ActuatorsOrder.MONTE_DESCEND_ASCENCEUR_DROIT_DE_UN_PALET, true);
            robot.waitForRightElevator();
            robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_ASCENSEUR, true);
        });
    }

    @Override
    public Vec2 entryPosition(Integer version) {
        return new VectCartesian(900, 450);

    }

    @Override
    public void finalize(Exception e) {
        robot.setSpeed(Speed.DEFAULT_SPEED);
    }

    @Override
    public void updateConfig(Config config) {
        super.updateConfig(config);
        this.symetry = config.getString(ConfigData.COULEUR).equals("violet");
    }

}
