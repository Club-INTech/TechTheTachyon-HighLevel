package scripts;

import data.CouleurPalet;
import data.SensorState;
import locomotion.UnableToMoveException;
import orders.Speed;
import orders.order.ActuatorsOrder;
import utils.Container;
import utils.math.Vec2;
import utils.math.VectCartesian;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

/**
 * Script pour vider les ascenseurs dans la zone de départ si l'accélérateur est bloqué
 */
public class VideDansZoneDepartSiProbleme extends Script {

    /**
     * Position d'entrée du script
     */
    //private final int xEntry = -490+10;
    //private final int yEntry = 410-78+50+10;
    private boolean lastWasRed=true;

    private CompletableFuture<Void> recalageLeft;
    private CompletableFuture<Void> recalageRight;

    public VideDansZoneDepartSiProbleme(Container container) {
        super(container);
        versions = new ArrayList<>();
        versions.add(0);  //version initiale (7 palets)
        versions.add(1);  //version pour mettre 7 palets + le bleu initial
    }

    @Override
    public void execute(int version) {
        try {
            turn(Math.PI/2);
            actuators.RIGHT_PUMP.activate();
            recalageRight.join();
            while (robot.getNbPaletsDroits() > 0) {
                actuators.RIGHT_PUMP.desactivate(true);

                if (robot.getRightElevatorOrNull().peek() == CouleurPalet.ROUGE && !lastWasRed) {
                    System.out.println(1111);
                    robot.moveLengthwise(-300, false);
                    lastWasRed=true;
                } else if (robot.getRightElevatorOrNull().peek() == CouleurPalet.VERT && lastWasRed) {
                    System.out.println(2222);
                    robot.moveLengthwise(300, false);
                    lastWasRed=false;
                }

                robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_AU_DESSUS_ZONE_DEPART, true);

                if (robot.getNbPaletsDroits() > 1) {
                    SensorState.RIGHT_ELEVATOR_MOVING.setData(true);
                    actuators.RIGHT_ELEVATOR.up();
                }
                actuators.RIGHT_VALVE.activate(true);
                robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_DE_LA_POSITION_AU_DESSUS_ZONE_DEPART_A_STOCKAGE, true);
                actuators.RIGHT_VALVE.desactivate(true);
                robot.increaseScore(6);
                robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_ASCENSEUR, true);


                if(robot.getRightElevatorOrNull() != null) {
                    robot.popPaletDroit();
                }
            }


            turn(-Math.PI/2);
            actuators.LEFT_PUMP.activate(true);
            recalageLeft.join();
            while (robot.getNbPaletsGauches() > 0) {
                actuators.LEFT_VALVE.desactivate(true);

                if (robot.getLeftElevatorOrNull().peek() == CouleurPalet.ROUGE && !lastWasRed) {
                    robot.moveLengthwise(300, false);
                    lastWasRed=true;
                } else if (robot.getLeftElevatorOrNull().peek() == CouleurPalet.VERT && lastWasRed) {
                    robot.moveLengthwise(-300, false);
                    lastWasRed=false;
                }

                robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_AU_DESSUS_ZONE_DEPART, true);
                if (robot.getNbPaletsGauches() > 1) {
                    SensorState.LEFT_ELEVATOR_MOVING.setData(true);
                    actuators.LEFT_ELEVATOR.up();
                }
                actuators.LEFT_VALVE.activate(true);
                robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_GAUCHE_DE_LA_POSITION_AU_DESSUS_ZONE_DEPART_A_STOCKAGE, true);
                actuators.LEFT_VALVE.desactivate(true);
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
            actuators.LEFT_ELEVATOR.updown(true);
            robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_ASCENSEUR, true);
        });
        recalageRight = async("Recalage ascenseur droit", () -> {
            robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_LIBERE_ASCENSEUR, true);
            actuators.RIGHT_ELEVATOR.updown(true);
            robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_ASCENSEUR, true);
        });
    }

    @Override
    public Vec2 entryPosition(int version) {
        return new VectCartesian(900, 450);

    }

    @Override
    public void finalize(Exception e) {
        robot.setSpeed(Speed.DEFAULT_SPEED);
    }

}
