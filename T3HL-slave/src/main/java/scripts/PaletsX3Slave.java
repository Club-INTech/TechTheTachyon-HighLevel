package scripts;

import data.CouleurPalet;
import data.Sick;
import data.Table;
import locomotion.UnableToMoveException;
import orders.order.ActuatorsOrder;
import pfg.config.Config;
import robot.Slave;
import utils.ConfigData;
import utils.math.Vec2;
import utils.math.VectCartesian;

public class PaletsX3Slave extends Script{
    /**
     * Position d'entrée du script
     */

    private int xEntry = 1500-230;// 1338
    private int yEntry = 1700 ;//+  (int) ConfigData.ROBOT_RAY.getDefaultValue() ;
    private int offsetX = 0;
    private int offsetY = 0;
    /**
     * constante
     */
    private boolean symetrie;


    public PaletsX3Slave(Slave robot, Table table) {
        super(robot, table);
    }

    @Override
    public void execute(Integer version) {
        try {
            recalage();
            robot.followPathTo(new VectCartesian(1338+offsetX, yEntry+offsetY));

            if(!symetrie) {
                robot.turn(Math.PI);
            }
            else {
                robot.turn(0);
            }

            getPuck();
            robot.pushPaletDroit(CouleurPalet.BLEU); // TODO
            robot.moveLengthwise(-100,false);

            getPuck();
            robot.pushPaletDroit(CouleurPalet.VERT); // TODO
            robot.moveLengthwise(-100,false);

            getPuck();
            robot.pushPaletDroit(CouleurPalet.ROUGE); // TODO
            robot.useActuator(ActuatorsOrder.DESACTIVE_LA_POMPE_DU_SECONDAIRE);

        }
        catch (UnableToMoveException e) {
            e.printStackTrace();
        }
    }

    private void recalage() {
        if(symetrie) {
            try {
                robot.turn(-Math.PI/2);
                robot.computeNewPositionAndOrientation(Sick.SECONDAIRE);
            } catch (UnableToMoveException e) {
                e.printStackTrace();
            }
        } else {
            try {
                robot.turn(Math.PI);
                robot.computeNewPositionAndOrientation(Sick.SECONDAIRE);
            } catch (UnableToMoveException e) {
                e.printStackTrace();
            }
        }
    }


    private void getPuck() {
        robot.useActuator(ActuatorsOrder.ACTIVE_LA_POMPE_DU_SECONDAIRE);
        robot.useActuator(ActuatorsOrder.DESACTIVE_ELECTROVANNE_DU_SECONDAIRE);
        robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DU_SECONDAIRE_A_LA_POSITION_DISTRIBUTEUR, true);//TODO refaire position bras
        robot.useActuator(ActuatorsOrder.REMONTE_LE_BRAS_DU_SECONDAIRE_DU_DISTRIBUTEUR_VERS_ASCENSEUR, true);
        robot.useActuator(ActuatorsOrder.ACTIVE_ELECTROVANNE_DU_SECONDAIRE, true);
        robot.useActuator(ActuatorsOrder.DESCEND_ASCENSEUR_DU_SECONDAIRE_DE_UN_PALET);
    }

    @Override
    public Vec2 entryPosition(Integer version) {
        return new VectCartesian(xEntry+offsetX, yEntry+offsetY);
    }

    @Override
    public void finalize(Exception e) { }

    @Override
    public void updateConfig(Config config) {
        this.symetrie = config.getString(ConfigData.COULEUR).equals("violet");
        super.updateConfig(config);
    }

}