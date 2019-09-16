package scripts;

import data.CouleurPalet;
import data.SensorState;
import locomotion.UnableToMoveException;
import orders.Speed;
import orders.order.ActuatorsOrder;
import utils.HLInstance;
import utils.math.Vec2;
import utils.math.InternalVectCartesian;

import java.util.ArrayList;
import java.util.concurrent.Future;

/**
 * Script pour vider les ascenseurs dans la zone de départ si l'accélérateur est bloqué
 */
public class VideDansZoneDepartSiProbleme extends Script {

    /**
     * Position d'entrée du script
     */
    private boolean lastWasRed=true;

    private Future<Void> recalageLeft;
    private Future<Void> recalageRight;

    public VideDansZoneDepartSiProbleme(HLInstance hl) {
        super(hl);
        versions = new ArrayList<>();
        versions.add(0);  //version initiale (7 palets)
        versions.add(1);  //version pour mettre 7 palets + le bleu initial
    }

    @Override
    public void execute(int version) {
        try {
            turn(Math.PI/2);
            actuators.rightPump.activate();
            join(recalageRight);
            while (robot.getNbPaletsDroits() > 0) {
                actuators.rightPump.deactivate(true);

                if (robot.getRightElevatorOrNull().peek() == CouleurPalet.ROUGE && !lastWasRed) {
                    System.out.println(1111);
                    moveLengthwise(-300, false);
                    lastWasRed=true;
                } else if (robot.getRightElevatorOrNull().peek() == CouleurPalet.VERT && lastWasRed) {
                    System.out.println(2222);
                    moveLengthwise(300, false);
                    lastWasRed=false;
                }

                robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_AU_DESSUS_ZONE_DEPART, true);

                if (robot.getNbPaletsDroits() > 1) {
                    SensorState.RIGHT_ELEVATOR_MOVING.setData(true);
                    actuators.rightElevator.up();
                }
                actuators.rightValve.activate(true);
                robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_DE_LA_POSITION_AU_DESSUS_ZONE_DEPART_A_STOCKAGE, true);
                actuators.rightValve.deactivate(true);
                robot.increaseScore(6);
                robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_ASCENSEUR, true);


                if(robot.getRightElevatorOrNull() != null) {
                    robot.popPaletDroit();
                }
            }


            turn(-Math.PI/2);
            actuators.leftPump.activate(true);
            join(recalageLeft);
            while (robot.getNbPaletsGauches() > 0) {
                actuators.leftValve.deactivate(true);

                if (robot.getLeftElevatorOrNull().peek() == CouleurPalet.ROUGE && !lastWasRed) {
                    moveLengthwise(300, false);
                    lastWasRed=true;
                } else if (robot.getLeftElevatorOrNull().peek() == CouleurPalet.VERT && lastWasRed) {
                    moveLengthwise(-300, false);
                    lastWasRed=false;
                }

                robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_AU_DESSUS_ZONE_DEPART, true);
                if (robot.getNbPaletsGauches() > 1) {
                    SensorState.LEFT_ELEVATOR_MOVING.setData(true);
                    actuators.leftElevator.up();
                }
                actuators.leftValve.activate(true);
                robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_GAUCHE_DE_LA_POSITION_AU_DESSUS_ZONE_DEPART_A_STOCKAGE, true);
                actuators.leftValve.deactivate(true);
                robot.increaseScore(6);
                robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_ASCENSEUR, true);


                if (robot.getRightElevatorOrNull() != null) {
                    robot.popPaletGauche();
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
            actuators.leftElevator.updown(true);
            robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_ASCENSEUR, true);
        });
        recalageRight = async("Recalage ascenseur droit", () -> {
            robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_LIBERE_ASCENSEUR, true);
            actuators.rightElevator.updown(true);
            robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_ASCENSEUR, true);
        });
    }

    @Override
    public Vec2 entryPosition(int version) {
        return new InternalVectCartesian(900, 450);

    }

    @Override
    public void finalize(Exception e) {
        robot.setSpeed(Speed.DEFAULT_SPEED);
    }

}
