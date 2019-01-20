package scripts;

import data.Table;
import locomotion.UnableToMoveException;
import orders.Speed;
import orders.order.ActuatorsOrder;
import pfg.config.Config;
import robot.Master;
import utils.ConfigData;
import utils.math.Circle;
import utils.math.Shape;
import utils.math.Vec2;
import utils.math.VectCartesian;

import java.awt.*;

public class Paletsx3 extends Script{
    /**
     * Position d'entrée du script
     */

    private int xEntry = 1375;
    private int yEntry = 1800 +  (int) ConfigData.ROBOT_RAY.getDefaultValue() ;
    /**
     * constante
     */
    Vec2[] positions = new Vec2[]{
            new VectCartesian(xEntry,yEntry),
            new VectCartesian(xEntry-100,yEntry),
            new VectCartesian(xEntry-200,yEntry)
    };


    public Paletsx3(Master robot, Table table) {
        super(robot, table);
    }

    @Override
    public void execute(Integer version) {
        try {
            robot.turn(Math.PI);
            robot.useActuator(ActuatorsOrder.ACTIVE_LA_POMPE_DROITE);
            for (int j = 1; j < 3; j++) {
                robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_DISTRIBUTEUR);
                robot.useActuator(ActuatorsOrder.ACTIVE_ELECTROVANNE_DROITE);
                robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_ASCENSEUR);
                robot.useActuator(ActuatorsOrder.DESACTIVE_ELECTROVANNE_DROITE);
                robot.useActuator(ActuatorsOrder.DESCEND_ASCENSEUR_GAUCHE_DE_UN_PALET);
                robot.moveToPoint(positions[j]);
            }
            robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_DISTRIBUTEUR);
            robot.useActuator(ActuatorsOrder.ACTIVE_ELECTROVANNE_DROITE);
            robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_ASCENSEUR);
            robot.useActuator(ActuatorsOrder.DESACTIVE_ELECTROVANNE_DROITE);
            robot.useActuator(ActuatorsOrder.DESCEND_ASCENSEUR_GAUCHE_DE_UN_PALET);
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
    public void updateConfig(Config config) { }

}