package scripts;

import data.Table;
import data.XYO;
import locomotion.UnableToMoveException;
import orders.order.ActuatorsOrder;
import pfg.config.Config;
import robot.Slave;
import utils.ConfigData;
import utils.math.Vec2;
import utils.math.VectCartesian;

import static utils.Offsets.ZDD_X_JAUNE;
import static utils.Offsets.ZDD_X_VIOLET;

public class CloseTheCassiopeeSlave extends Script {
    private boolean symetry;

    /**
     * Construit un script
     *
     * @param robot le robot
     * @param table
     */
    protected CloseTheCassiopeeSlave(Slave robot, Table table) {
        super(robot, table);
    }

    @SuppressWarnings("Duplicates")
    @Override
    public void execute(Integer version) {
        Vec2 lowerLeft = new VectCartesian(650, 300);
        Vec2 lowerRight = new VectCartesian(1200, 300);
        Vec2 upperLeft = new VectCartesian(650, 1200);
        Vec2 upperRight = new VectCartesian(1200, 1200);

        try {
            robot.gotoPoint(lowerRight);
        } catch (UnableToMoveException e) {
            e.printStackTrace();
        }
        try {
            robot.turnToPoint(upperRight);
        } catch (UnableToMoveException e) {
            e.printStackTrace();
        }

        while (true) {
            //On donne le palet au robot
            robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DU_SECONDAIRE_A_LA_POSITION_GOLDONIUM, true);
            robot.useActuator(ActuatorsOrder.ACTIVE_LA_POMPE_DU_SECONDAIRE, true);
            robot.useActuator(ActuatorsOrder.DESACTIVE_ELECTROVANNE_DU_SECONDAIRE, true);
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            //On stocke le palet dans le robot
            robot.useActuator(ActuatorsOrder.REMONTE_LE_BRAS_DU_SECONDAIRE_DU_DISTRIBUTEUR_VERS_ASCENSEUR, true);
            robot.useActuator(ActuatorsOrder.DESACTIVE_LA_POMPE_DU_SECONDAIRE, true);
            robot.useActuator(ActuatorsOrder.ACTIVE_ELECTROVANNE_DU_SECONDAIRE, true);
            robot.useActuator(ActuatorsOrder.DESCEND_ASCENSEUR_DU_SECONDAIRE_DE_UN_PALET, true);

            //On se déplace à une autre position
            try {
                robot.followPathTo(upperRight, 0);
            } catch (UnableToMoveException e) {
                e.printStackTrace();
            }

            //On dépose le palet
            robot.useActuator(ActuatorsOrder.MONTE_ASCENSEUR_DU_SECONDAIRE_DE_UN_PALET, true);
            robot.useActuator(ActuatorsOrder.ACTIVE_LA_POMPE_DU_SECONDAIRE, true);
            robot.useActuator(ActuatorsOrder.DESACTIVE_ELECTROVANNE_DU_SECONDAIRE, true);
            robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DU_SECONDAIRE_A_LA_POSITION_ACCELERATEUR_DEPOT,true);
            robot.useActuator(ActuatorsOrder.ACTIVE_ELECTROVANNE_DU_SECONDAIRE,true);
            robot.useActuator(ActuatorsOrder.DESACTIVE_LA_POMPE_DU_SECONDAIRE, true);
            robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DU_SECONDAIRE_A_LA_POSITION_ACCELERATEUR_DEPOT_FINAL,true);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            robot.useActuator(ActuatorsOrder.ACTIVE_LA_POMPE_DU_SECONDAIRE);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


            //On revient à la position d'origine
            try {
                robot.followPathTo(lowerRight, 0);
            } catch (UnableToMoveException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public Vec2 entryPosition(Integer version) {
        return XYO.getRobotInstance().getPosition();
    }

    @Override
    public void updateConfig(Config config) {
        super.updateConfig(config);
        symetry = config.getString(ConfigData.COULEUR).equals("violet");
    }

    @Override
    public void finalize(Exception e) {

    }
}
