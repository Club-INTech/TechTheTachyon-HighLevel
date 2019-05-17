package scripts;

import data.CouleurPalet;
import data.Table;
import locomotion.UnableToMoveException;
import orders.order.ActuatorsOrder;
import pfg.config.Config;
import robot.Slave;
import utils.math.Vec2;
import utils.math.VectCartesian;

public class GetRedDep extends Script {
    private final int xEntry = 1500-191-65;//1350;
    private final int yEntry = 450+605;//450;
    private boolean symetrie;

    public GetRedDep(Slave robot, Table table) {
        super(robot, table);
    }

    public void execute(Integer version) {
        try {
            if (!symetrie) {
                robot.turn(-(Math.PI/2));
            } else {
                robot.turn(Math.PI / 2);
            }
            robot.useActuator(ActuatorsOrder.ACTIVE_LA_POMPE_DU_SECONDAIRE);
            robot.useActuator(ActuatorsOrder.DESACTIVE_ELECTROVANNE_DU_SECONDAIRE);
            robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DU_SECONDAIRE_A_LA_POSITION_SOL, true);
            //robot.pushPaletDroit(CouleurPalet.ROUGE); TODO:push et pop pour le secondaire
            robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DU_SECONDAIRE_A_LA_POSITION_ASCENSEUR, true);
            robot.useActuator(ActuatorsOrder.ACTIVE_ELECTROVANNE_DU_SECONDAIRE);

        } catch (UnableToMoveException e) {
        e.printStackTrace();
        }
    }

    @Override
    public Vec2 entryPosition(Integer version) {
        return new VectCartesian(xEntry, yEntry);
    }

    @Override
    public void finalize(Exception e) {
        // range le bras quand on a fini
        robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DU_SECONDAIRE_A_LA_POSITION_ASCENSEUR,false);
    }

    @Override
    public void updateConfig(Config config) {
        super.updateConfig(config);
    }

}
