package scripts;

import data.CouleurPalet;
import data.Table;
import data.XYO;
import orders.order.ActuatorsOrder;
import pfg.config.Config;
import robot.Slave;
import utils.math.Vec2;

public class GetRedDep extends Script {
    private final int xEntry = 1500-191-65;//1350;
    private final int yEntry = 450+605;//450;
    private boolean symetrie;

    public GetRedDep(Slave robot, Table table) {
        super(robot, table);
    }

    public void execute(Integer version) {
        robot.useActuator(ActuatorsOrder.ACTIVE_LA_POMPE_DU_SECONDAIRE);
        robot.useActuator(ActuatorsOrder.DESACTIVE_ELECTROVANNE_DU_SECONDAIRE);
        robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DU_SECONDAIRE_A_LA_POSITION_SOL, true);
        robot.pushPaletDroit(CouleurPalet.ROUGE);
        table.removeTemporaryObstacle(table.getPaletRougeDroite());
        async("Remonte et stock", () -> {
            robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DU_SECONDAIRE_A_LA_POSITION_ASCENSEUR, true);
            robot.useActuator(ActuatorsOrder.ACTIVE_ELECTROVANNE_DU_SECONDAIRE);
        });
    }

    @Override
    public Vec2 entryPosition(Integer version) {
        return XYO.getRobotInstance().getPosition();
    }

    @Override
    public void finalize(Exception e) { }

    @Override
    public void updateConfig(Config config) {
        super.updateConfig(config);
    }

}
