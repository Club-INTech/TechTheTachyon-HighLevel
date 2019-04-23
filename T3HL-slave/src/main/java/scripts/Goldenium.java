package scripts;

import data.Table;
import orders.order.ActuatorsOrder;
import pfg.config.Config;
import robot.Slave;
import utils.math.Circle;
import utils.math.Shape;
import utils.math.VectCartesian;

// TODO Mettre ce script dans le code du slave
public class Goldenium extends Script {

    /**
     * Construit un script
     *
     * @param robot le robot
     * @param table
     */
    /**
     * Position d'entrée du script
     */

    private int xEntry = -700; //a vérifier
    private int yEntry = 300 ; // distance à verifier

    /**
     * constante
     */
    protected Goldenium(Slave robot, Table table) {
        super(robot, table);
    }

    @Override
    public void execute(Integer version) {
        //attention il n'y qu'une seule pompe sur le robot secondaire
        robot.useActuator(ActuatorsOrder.DESCEND_ASCENSEUR_GAUCHE_DE_UN_PALET);
        robot.useActuator(ActuatorsOrder.ACTIVE_LA_POMPE_GAUCHE);
        robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_GOLDONIUM);

        robot.useActuator(ActuatorsOrder.DESACTIVE_LA_POMPE_GAUCHE);
        robot.useActuator(ActuatorsOrder.DESACTIVE_ELECTROVANNE_GAUCHE);
        robot.increaseScore(20);

    }

    @Override
    public Shape entryPosition(Integer version) {
        {
            return new Circle(new VectCartesian(xEntry, yEntry), 42);
        }
    }

    @Override
    public void finalize(Exception e) {
        robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_ASCENSEUR);
        robot.useActuator(ActuatorsOrder.DESACTIVE_LA_POMPE_GAUCHE);
        robot.useActuator(ActuatorsOrder.DESACTIVE_ELECTROVANNE_GAUCHE);
    }

    @Override
    public void updateConfig(Config config) {

    }
}
