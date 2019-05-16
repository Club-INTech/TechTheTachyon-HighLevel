package scripts;

import data.SensorState;
import data.Table;
import locomotion.UnableToMoveException;
import orders.order.ActuatorsOrder;
import robot.Master;
import utils.math.Vec2;
import utils.math.VectCartesian;

/**
 * Script pour vider les ascenseurs dans la zone de départ si l'accélérateur est bloqué
 */
public class VideDansZoneDepartSiProbleme extends Script {

    public VideDansZoneDepartSiProbleme(Master robot, Table table) {
        super(robot, table);
    }

    @Override
    public void execute(Integer version) {
        try {
            robot.turn(0);
        } catch (UnableToMoveException e) {
            e.printStackTrace();
        }
        SensorState.RIGHT_ARM_MOVING.setData(false);
        SensorState.LEFT_ARM_MOVING.setData(false);
        while(robot.getNbPaletsDroits() > 0 || robot.getNbPaletsGauches() > 0) {
            removePuck();
        }
    }

    /**
     * Retire un palet à droite puis à gauche, puis à droite etc.
     */
    private void removePuck() {
        // pour s'assurer que le bras est au bon endroit
        waitWhileTrue(SensorState.LEFT_ARM_MOVING::getData);
        waitWhileTrue(SensorState.RIGHT_ARM_MOVING::getData);
        robot.useActuator(ActuatorsOrder.DESACTIVE_ELECTROVANNE_GAUCHE);
        robot.useActuator(ActuatorsOrder.DESACTIVE_ELECTROVANNE_DROITE, true);
        if(robot.getNbPaletsDroits() > 0) {
            robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_AU_DESSUS_PALET, true);
            robot.useActuator(ActuatorsOrder.ACTIVE_ELECTROVANNE_DROITE, true);
            SensorState.RIGHT_ARM_MOVING.setData(true); // pour s'assurer que le bras est au bon endroit
            robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_ASCENSEUR);
            robot.useActuator(ActuatorsOrder.MONTE_ASCENCEUR_DROIT_DE_UN_PALET);
            robot.popPaletDroit();
        }

        if(robot.getNbPaletsGauches() > 0) {
            robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_AU_DESSUS_PALET, true);
            robot.useActuator(ActuatorsOrder.ACTIVE_ELECTROVANNE_GAUCHE, true);
            SensorState.LEFT_ARM_MOVING.setData(true); // pour s'assurer que le bras est au bon endroit
            robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_ASCENSEUR);
            robot.useActuator(ActuatorsOrder.MONTE_ASCENCEUR_GAUCHE_DE_UN_PALET);
            robot.popPaletGauche();
        }
    }

    @Override
    public Vec2 entryPosition(Integer version) {
        return new VectCartesian(1300, 350);
    }

    @Override
    public void executeWhileMovingToEntry(int version) {
        robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_ASCENSEUR);
        robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_ASCENSEUR);
        robot.useActuator(ActuatorsOrder.ACTIVE_LA_POMPE_GAUCHE);
        robot.useActuator(ActuatorsOrder.ACTIVE_LA_POMPE_DROITE);
        robot.useActuator(ActuatorsOrder.ACTIVE_ELECTROVANNE_DROITE);
        robot.useActuator(ActuatorsOrder.ACTIVE_ELECTROVANNE_GAUCHE);
    }

    @Override
    public void finalize(Exception e) {

    }
}
