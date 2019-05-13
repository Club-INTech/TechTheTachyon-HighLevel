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

public class Cracheur extends Script {
    /**
     * Position d'entrée du script
     */

    //Valeurs à ajuster pour le robot secondaire
    private int xEntry = 1500-1280;
    private int yEntry = 1180;
    private boolean symetrie;

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
        try {
            if (!symetrie){
                robot.turn(Math.PI);
            }
            else {
                robot.turn(0);
            }
        } catch (UnableToMoveException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < robot.getNbPaletsDroits(); i++) {
            robot.useActuator(ActuatorsOrder.CRACHE_UN_PALET, true);
            robot.useActuator(ActuatorsOrder.RANGE_CRACHE_PALET, true);
            robot.useActuator(ActuatorsOrder.MONTE_ASCENSEUR_DU_SECONDAIRE_DE_UN_PALET, true);
            robot.popPaletDroit();
        }
    }
    @Override //à adapter
    public Vec2 entryPosition(Integer version) { return new VectCartesian(xEntry, yEntry); }

    @Override
    public void finalize(Exception e) { }

    @Override
    public void updateConfig(Config config) { this.symetrie = config.getString(ConfigData.COULEUR).equals("violet"); }
}
