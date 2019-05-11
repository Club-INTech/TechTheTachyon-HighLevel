package scripts;

import data.CouleurPalet;
import data.Table;
import locomotion.UnableToMoveException;
import orders.order.ActuatorsOrder;
import pfg.config.Config;
import robot.Slave;
import utils.math.Circle;
import utils.math.Shape;
import utils.math.Vec2;
import utils.math.VectCartesian;

public class Paletsx3 extends Script{
    /**
     * Position d'entrée du script
     */

    private int xEntry = 1375;
    private int yEntry = 1700 ;//+  (int) ConfigData.ROBOT_RAY.getDefaultValue() ;
    /**
     * constante
     */
    private Vec2[] positions = new Vec2[]{
            new VectCartesian(xEntry,yEntry),
            //new VectCartesian(xEntry-100,yEntry),
            //new VectCartesian(xEntry-200,yEntry)
    };


    public Paletsx3(Slave robot, Table table) {
        super(robot, table);
    }

    @Override
    public void execute(Integer version) {
        try {
            robot.turn(Math.PI);
            robot.useActuator(ActuatorsOrder.ACTIVE_LA_POMPE_DROITE);
            for (int j = 1; j < positions.length; j++) {
                robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_DISTRIBUTEUR);
                robot.useActuator(ActuatorsOrder.DESACTIVE_ELECTROVANNE_DROITE);
                robot.useActuator(ActuatorsOrder.REMONTE_LE_BRAS_DROIT_DU_DISTRIBUTEUR_VERS_ASCENSEUR);
                robot.useActuator(ActuatorsOrder.TEST_PALET_ATTRAPE_EN_FONCTION_DU_COUPLE_DROIT);

                CouleurPalet couleur = CouleurPalet.getCouleurPalRecu();

                robot.useActuator(ActuatorsOrder.ACTIVE_ELECTROVANNE_DROITE);
                robot.useActuator(ActuatorsOrder.DESCEND_ASCENSEUR_DROIT_DE_UN_PALET);

                robot.pushPaletDroit(couleur); // TODO
                robot.followPathTo(positions[j]);
            }
            robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_DISTRIBUTEUR);
            robot.useActuator(ActuatorsOrder.DESACTIVE_ELECTROVANNE_DROITE);
            robot.useActuator(ActuatorsOrder.REMONTE_LE_BRAS_DROIT_DU_DISTRIBUTEUR_VERS_ASCENSEUR);
            robot.useActuator(ActuatorsOrder.TEST_PALET_ATTRAPE_EN_FONCTION_DU_COUPLE_DROIT);

            CouleurPalet couleur = CouleurPalet.getCouleurPalRecu();
            robot.useActuator(ActuatorsOrder.ACTIVE_ELECTROVANNE_DROITE);
            robot.useActuator(ActuatorsOrder.DESCEND_ASCENSEUR_DROIT_DE_UN_PALET);
            robot.pushPaletDroit(couleur); // TODO
            robot.useActuator(ActuatorsOrder.DESACTIVE_LA_POMPE_DROITE);

        }
        catch (UnableToMoveException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Shape entryPosition(Integer version) {
        return new Circle(new VectCartesian(xEntry, yEntry), 5);
    }

    @Override
    public void finalize(Exception e) { }

    @Override
    public void updateConfig(Config config) {
        super.updateConfig(config);
    }

}