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

public class GetBlueAcc extends Script {
    private int xEntry = -170;
    private int yEntry = 340;
    private boolean symetrie;

    public GetBlueAcc(Slave robot, Table table) {
        super(robot, table);
    }

    @Override
    public void execute(Integer version) {
        try {
            if (!symetrie){
                robot.turn(0);
            }
            else{
                robot.turn(Math.PI);
            }
        } catch (UnableToMoveException e) {
            e.printStackTrace();
        }
        robot.useActuator(ActuatorsOrder.ACTIVE_LA_POMPE_DU_SECONDAIRE);
        robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DU_SECONDAIRE_A_LA_POSITION_ACCELERATEUR);
        robot.useActuator(ActuatorsOrder.DESACTIVE_ELECTROVANNE_DU_SECONDAIRE);
        robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DU_SECONDAIRE_A_LA_POSITION_ASCENSEUR);
        robot.useActuator(ActuatorsOrder.ACTIVE_ELECTROVANNE_DU_SECONDAIRE);
        robot.useActuator(ActuatorsOrder.DESCEND_ASCENSEUR_DU_SECONDAIRE_DE_UN_PALET);
    }
    @Override //à adapter
    public Vec2 entryPosition(Integer version) { return new VectCartesian(xEntry, yEntry); }

    @Override
    public void finalize(Exception e) { }

    @Override
    public void updateConfig(Config config) { this.symetrie = config.getString(ConfigData.COULEUR).equals("violet"); }

}
