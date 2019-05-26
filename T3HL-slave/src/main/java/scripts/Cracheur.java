package scripts;

import locomotion.UnableToMoveException;
import orders.order.ActuatorsOrder;
import data.Table;
import pfg.config.Config;
import robot.Slave;
import utils.ConfigData;
import utils.math.Circle;
import utils.math.Shape;
import utils.math.Vec2;
import utils.math.VectCartesian;

import java.util.concurrent.TimeUnit;

public class Cracheur extends Script {
    /**
     * Position d'entrée du script
     */

    //Valeurs à ajuster pour le robot secondaire
    private int xEntry = 1500-1280;
    private int yEntry = 1180;
    private boolean symetrie;
    private boolean first = true;

    private int nbPalets = robot.getNbPaletsDroits();


    public Cracheur(Slave robot, Table table) {
        super(robot, table);
    }

    @Override
    public void execute(Integer version) {
        try {
            robot.gotoPoint(new VectCartesian(1500-1330, 1380));
            robot.turn(Math.PI / 2);
        } catch (UnableToMoveException e) {
            e.printStackTrace();
        }

        robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DU_SECONDAIRE_A_LA_POSITION_BALANCE,true);

        for (int i = 0; i < robot.getNbPaletsDroits(); i++) {

            robot.useActuator(ActuatorsOrder.MONTE_ASCENSEUR_DU_SECONDAIRE_DE_UN_PALET, true);
            robot.waitForRightElevator();
            robot.useActuator(ActuatorsOrder.CRACHE_UN_PALET, true);
            try {
                TimeUnit.MILLISECONDS.sleep(250);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            robot.useActuator(ActuatorsOrder.RANGE_CRACHE_PALET, true);
            try {
                TimeUnit.MILLISECONDS.sleep(250);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            robot.popPaletDroit();
        }

        robot.useActuator(ActuatorsOrder.DESCEND_ASCENCEUR_DU_SECONDAIRE_POUR_CRACHER_LES_PALETS);
    }
    @Override //à adapter
    public Vec2 entryPosition(Integer version) { return new VectCartesian(xEntry, yEntry); }

    @Override
    public void finalize(Exception e) { }

    @Override
    public void updateConfig(Config config) { this.symetrie = config.getString(ConfigData.COULEUR).equals("violet"); }
}
