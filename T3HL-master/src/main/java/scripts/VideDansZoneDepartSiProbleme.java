package scripts;

import data.CouleurPalet;
import data.SensorState;
import data.Table;
import locomotion.UnableToMoveException;
import orders.order.ActuatorsOrder;
import robot.Master;
import utils.math.Vec2;
import utils.math.VectCartesian;

import java.math.MathContext;
import java.util.concurrent.CompletableFuture;

/**
 * Script pour vider les ascenseurs dans la zone de départ si l'accélérateur est bloqué
 */
public class VideDansZoneDepartSiProbleme extends Script {
    private CompletableFuture<Void> recalageLeft;
    private CompletableFuture<Void> recalageRight;

    public VideDansZoneDepartSiProbleme(Master robot, Table table) {
        super(robot, table);
    }

    @Override
    public void execute(Integer version) {
        try {
            robot.turn(Math.PI/2);
        } catch (UnableToMoveException e) {
            e.printStackTrace();
        }
        while(robot.getNbPaletsDroits() > 0 || robot.getNbPaletsGauches() > 0) {
            removePuck();
        }



    }

    /**
     * Retire un palet à droite puis à gauche, puis à droite etc.
     */
    private void removePuck() {
        // pour s'assurer que le bras est au bon endroit

        if(robot.getNbPaletsDroits() > 0) {
            robot.waitWhileTrue(SensorState.RIGHT_ELEVATOR_MOVING::getData);
            robot.useActuator(ActuatorsOrder.DESACTIVE_ELECTROVANNE_DROITE, true);
            robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_DEPOT,true);
            robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_AU_DESSUS_ZONE_DEPART, true);
            CouleurPalet couleur = robot.popPaletDroit();
            if(couleur == CouleurPalet.VERT){robot.increaseScore(6);}
            else {robot.increaseScore(1);}
            robot.useActuator(ActuatorsOrder.MONTE_ASCENCEUR_DROIT_DE_UN_PALET,true);
            robot.useActuator(ActuatorsOrder.ACTIVE_ELECTROVANNE_DROITE, true);
            robot.waitForRightElevator();
            robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_ASCENSEUR,true);
        }
        else {
            try {
                robot.turn(Math.PI/2);
            } catch (UnableToMoveException e) {
                e.printStackTrace();
            }

            robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_DEPOT,true);
            robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_ASCENSEUR,true);
            robot.useActuator(ActuatorsOrder.DESACTIVE_LA_POMPE_DROITE,true);
            robot.useActuator(ActuatorsOrder.ACTIVE_LA_POMPE_GAUCHE,true);
            recalageLeft.join();
            robot.useActuator(ActuatorsOrder.ACTIVE_ELECTROVANNE_GAUCHE,true);

        while(robot.getNbPaletsGauches() > 0) {
            robot.waitWhileTrue(SensorState.LEFT_ELEVATOR_MOVING::getData);
            robot.useActuator(ActuatorsOrder.DESACTIVE_ELECTROVANNE_GAUCHE);
            robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_AU_DESSUS_ZONE_DEPART, true);
            CouleurPalet couleur = robot.popPaletGauche();
            if(couleur == CouleurPalet.ROUGE){robot.increaseScore(6);}
            else {robot.increaseScore(1);}
            robot.useActuator(ActuatorsOrder.MONTE_ASCENCEUR_GAUCHE_DE_UN_PALET,true);
            robot.useActuator(ActuatorsOrder.ACTIVE_ELECTROVANNE_GAUCHE, true);
            robot.waitForLeftElevator();
            robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_ASCENSEUR);
        }
        }
    }

    @Override
    public Vec2 entryPosition(Integer version) {
        return new VectCartesian(900, 500);
    }

    @Override
    public void executeWhileMovingToEntry(int version) {
        recalageLeft = async("Recalage ascenseur gauche", () -> {
            robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_AU_DESSUS_ZONE_DEPART, true);
            robot.useActuator(ActuatorsOrder.MONTE_DESCEND_ASCENCEUR_GAUCHE_DE_UN_PALET, true);
            robot.waitForLeftElevator();
            robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_ASCENSEUR, true);
        });
        recalageRight = async("Recalage ascenseur droit", () -> {
            //robot.useActuator(ActuatorsOrder.MONTE_ASCENCEUR_DROIT_DE_UN_PALET);
            // robot.waitForRightElevator();
            robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_AU_DESSUS_ZONE_DEPART, true);
            robot.useActuator(ActuatorsOrder.MONTE_DESCEND_ASCENCEUR_DROIT_DE_UN_PALET, true);
            robot.waitForRightElevator();
            robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_ASCENSEUR, true);
        });
        recalageRight.join();


    }

    @Override
    public void finalize(Exception e) {

    }
}
