package scripts;

import data.Table;
import locomotion.UnableToMoveException;
import orders.order.ActuatorsOrder;
import pfg.config.Config;
import robot.Master;
import utils.math.Circle;
import utils.math.Shape;
import utils.math.Vec2;
import utils.math.VectCartesian;

public class PaletsZoneDepart extends Script {

    private int yEntry = 450;
    private int xEntry = 225;
    private Vec2[] positions = new VectCartesian[]{
            new VectCartesian(xEntry,yEntry),
            new VectCartesian(xEntry,yEntry+300),
            new VectCartesian(xEntry,yEntry+600)
    };


    public PaletsZoneDepart(Master robot, Table table) {
        super(robot, table);
    }

    @Override
    public void execute(Integer version) {
            boolean premierPaletPris = false;
            try {
                robot.turn(Math.PI/2);
                robot.useActuator(ActuatorsOrder.ACTIVE_LA_POMPE_GAUCHE);
                for (Vec2 position : positions) {
                    if (premierPaletPris) {
                        robot.moveToPoint(position);
                    } else {
                        premierPaletPris = true;
                    }
                    robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_SOL);
                    robot.useActuator(ActuatorsOrder.ACTIVE_ELECTROVANNE_GAUCHE);
                    robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_ACCELERATEUR);
                    robot.useActuator(ActuatorsOrder.DESACTIVE_ELECTROVANNE_GAUCHE);
                    robot.useActuator(ActuatorsOrder.DESCEND_ASCENSEUR_GAUCHE_DE_UN_PALET);
                }
                robot.useActuator(ActuatorsOrder.DESACTIVE_LA_POMPE_GAUCHE);
            }catch (UnableToMoveException e) {
            e.printStackTrace();
        }
    }
    @Override
    public Shape entryPosition(Integer version) {
        return new Circle(new VectCartesian(xEntry, yEntry), 5);
    }

    @Override
    public void finalize(Exception e) { }

    @Override
    public void updateConfig(Config config) { }
}
