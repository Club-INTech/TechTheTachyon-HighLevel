package scripts;

import data.CouleurPalet;
import data.Table;
import data.XYO;
import locomotion.UnableToMoveException;
import orders.order.ActuatorsOrder;
import pfg.config.Config;
import robot.Slave;
import utils.ConfigData;
import utils.math.Vec2;
import utils.math.VectCartesian;

public class GetRedDep extends Script {
    private final int xEntry = 1500-449+148; //1244;//1350;
    private final int yEntry = 301+100; //1055;//450;
    private boolean symetrie;

    public GetRedDep(Slave robot, Table table) {
        super(robot, table);
    }

    public void execute(Integer version) {
        try {
            if (!symetrie) {
                robot.moveLengthwise(-100,false);
            }
        } catch (UnableToMoveException e) {
            e.printStackTrace();
        }
        robot.useActuator(ActuatorsOrder.ACTIVE_LA_POMPE_DU_SECONDAIRE);
        robot.useActuator(ActuatorsOrder.DESACTIVE_ELECTROVANNE_DU_SECONDAIRE);
        robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DU_SECONDAIRE_A_LA_POSITION_SOL, true);
        robot.useActuator(ActuatorsOrder.DESACTIVE_ELECTROVANNE_DU_SECONDAIRE, true);
        robot.pushPaletDroit(CouleurPalet.ROUGE);
        table.removeTemporaryObstacle(table.getPaletRougeDroite());
    }

    @Override
    public Vec2 entryPosition(Integer version) {
        return new VectCartesian(xEntry, yEntry);
    }

    @Override
    public void finalize(Exception e) { }

    @Override
    public void updateConfig(Config config) {
        super.updateConfig(config);
        symetrie = config.getString(ConfigData.COULEUR).equals("violet");
    }

}
