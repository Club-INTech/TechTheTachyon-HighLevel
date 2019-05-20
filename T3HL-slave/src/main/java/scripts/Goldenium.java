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
import utils.math.Vec2;
import utils.math.VectCartesian;

public class Goldenium extends Script {

    //position d'entrée

    private int xEntry = -715 ; //a tester
    private int yEntry = 285-20;//250+ 30+10  ; //a tester

    //position de fin

    private int xBalance = 137; //a tester
    private int yBalance = 1385-150-15; //a tester (vraie valeur: 1388)

    //paramètres

    private final VectCartesian positionBalance;
    private boolean symetrie;


    public Goldenium(Slave robot, Table table) {
        super(robot, table);
        positionBalance = new VectCartesian(xBalance, yBalance);
    }

    @Override
    public void execute(Integer version) {
        //attention il n'y qu'une seule pompe sur le robot secondaire
        /*try {
            if(!symetrie) {
                robot.turn(Math.PI);
                try {
                    robot.moveLengthwise(510,false);
                } catch (UnableToMoveException e) {
                    e.printStackTrace();
                }
                robot.turn(0);
            }
            else {
                robot.turn(Math.PI);
                try {
                    robot.moveLengthwise(510,false);
                } catch (UnableToMoveException e) {
                    e.printStackTrace();
                }
            }
        } catch (UnableToMoveException e) {
            e.printStackTrace();
        }*/

        try {
            if(symetrie) {
                robot.turn(Math.PI);
            } else {
                robot.turn(0);
            }
        } catch (UnableToMoveException e) {
            e.printStackTrace();
        }

        robot.useActuator(ActuatorsOrder.ACTIVE_LA_POMPE_DU_SECONDAIRE);
        robot.useActuator(ActuatorsOrder.DESACTIVE_ELECTROVANNE_DU_SECONDAIRE);
        robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DU_SECONDAIRE_A_LA_POSITION_GOLDONIUM,true);
        /*try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
        robot.useActuator(ActuatorsOrder.DESACTIVE_ELECTROVANNE_DU_SECONDAIRE,true);
        robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DU_SECONDAIRE_A_LA_POSITION_MUSCLOR,true);
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

        //robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DU_SECONDAIRE_A_LA_POSITION_GOLDONIUM_DEPOT,true);
        robot.useActuator(ActuatorsOrder.ACTIVE_ELECTROVANNE_DU_SECONDAIRE);
        robot.increaseScore(24);
        //robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DU_SECONDAIRE_A_LA_POSITION_ASCENSEUR);
        robot.useActuator(ActuatorsOrder.DESACTIVE_LA_POMPE_DU_SECONDAIRE);

    }

    @Override
    public Vec2 entryPosition(Integer version) {
        return new VectCartesian(xEntry, yEntry);
    }

    @Override
    public void finalize(Exception e) {
        robot.setSpeed(Speed.DEFAULT_SPEED);
    }

    @Override
    public void updateConfig(Config config) {
        this.symetrie = config.getString(ConfigData.COULEUR).equals("violet");
        super.updateConfig(config);
    }
}
