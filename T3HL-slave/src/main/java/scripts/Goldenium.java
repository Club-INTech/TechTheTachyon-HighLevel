package scripts;

import data.Table;
import orders.Speed;
import orders.order.ActuatorsOrder;
import pfg.config.Config;
import robot.Slave;
import utils.math.Circle;
import utils.math.Shape;
import utils.math.VectCartesian;

// TODO Mettre ce script dans le code du slave
public class Goldenium extends Script {

    //position d'entrée

    private int xEntry = -725; //a tester
    private int yEntry = 250 ; //a tester

    //position de fin

    private int xBalance = 137; //a tester
    private int yBalance = 1385; //a tester (vraie valeur: 1388)

    //paramètres

    private final VectCartesian positionDepart;
    private final VectCartesian positionBalance;


    public Goldenium(Slave robot, Table table) {
        super(robot, table);
        positionDepart = new VectCartesian(xEntry, yEntry);
        positionBalance = new VectCartesian(xBalance, yBalance);
    }

    @Override
    public void execute(Integer version) {
        //attention il n'y qu'une seule pompe sur le robot secondaire
        robot.followPathTo(positionDepart);
        robot.turn(-Math.PI/2);

        robot.useActuator(ActuatorsOrder.ACTIVE_LA_POMPE_DROITE);
        robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_GOLDONIUM);
        robot.useActuator(ActuatorsOrder.DESACTIVE_ELECTROVANNE_DROITE);
        // FIXME: insérer position stockage goldenium
        robot.increaseScore(20);


        robot.followPathTo(positionBalance);
        robot.turn(Math.PI/2);

        robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_BALANCE);
        robot.useActuator(ActuatorsOrder.ACTIVE_ELECTROVANNE_DROITE);
        robot.increaseScore(24);
        robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_ASCENSEUR);


    }

    @Override
        public Shape entryPosition(Integer version) {
            return new Circle(new VectCartesian(xEntry, yEntry), 5);
        }

    @Override
    public void finalize(Exception e) {
        robot.setSpeed(Speed.DEFAULT_SPEED);
    }

    @Override
    public void updateConfig(Config config) {
        super.updateConfig(config);
    }
}
