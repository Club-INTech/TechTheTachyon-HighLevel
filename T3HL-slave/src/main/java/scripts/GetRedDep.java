package scripts;

import data.CouleurPalet;
import locomotion.UnableToMoveException;
import orders.order.ActuatorsOrder;
import pfg.config.Configurable;
import utils.Container;
import utils.math.Vec2;
import utils.math.VectCartesian;

public class GetRedDep extends Script {
    private final int xEntry = 1500-449+148; //1244;//1350;
    private final int yEntry = 301+100; //1055;//450;

    @Configurable
    private boolean symetry;

    public GetRedDep(Container container) {
        super(container);
    }

    public void execute(int version) {
        try {
            if (!symetry) {
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
    public Vec2 entryPosition(int version) {
        return new VectCartesian(xEntry, yEntry);
    }

    @Override
    public void finalize(Exception e) { }

}
