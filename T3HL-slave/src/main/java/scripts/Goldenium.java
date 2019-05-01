package scripts;

import data.Table;
import locomotion.UnableToMoveException;
import orders.Speed;
import orders.order.ActuatorsOrder;
import pfg.config.Config;
import robot.Slave;
import utils.ConfigData;
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
    boolean symetrie;

    public Goldenium(Slave robot, Table table) {
        super(robot, table);
        positionDepart = new VectCartesian(xEntry, yEntry);
        positionBalance = new VectCartesian(xBalance, yBalance);
    }

    @Override
    public void execute(Integer version) {
        //attention il n'y qu'une seule pompe sur le robot secondaire
        try {
            if(!symetrie) {
                robot.turn(0);
            }
            else {
                robot.turn(Math.PI);
            }
        } catch (UnableToMoveException e) {
            e.printStackTrace();
        }

        robot.useActuator(ActuatorsOrder.ACTIVE_LA_POMPE_DU_SECONDAIRE);
        robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DU_SECONDAIRE_A_LA_POSITION_GOLDONIUM);
        robot.useActuator(ActuatorsOrder.DESACTIVE_ELECTROVANNE_DU_SECONDAIRE);
        robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DU_SECONDAIRE_A_LA_POSITION_MUSCLOR);
        robot.increaseScore(20);


        try {
            robot.followPathTo(positionBalance);
            if(!symetrie) {
                robot.turn(Math.PI);
            }
            else {
                robot.turn(0);
            }
        } catch (UnableToMoveException e) {
            e.printStackTrace();
        }

        robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DU_SECONDAIRE_A_LA_POSITION_BALANCE);
        robot.useActuator(ActuatorsOrder.ACTIVE_ELECTROVANNE_DU_SECONDAIRE);
        robot.increaseScore(24);
        robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DU_SECONDAIRE_A_LA_POSITION_ASCENSEUR);
        robot.useActuator(ActuatorsOrder.DESACTIVE_LA_POMPE_DU_SECONDAIRE);

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
        this.symetrie = config.getString(ConfigData.COULEUR).equals("violet");
    }
}
