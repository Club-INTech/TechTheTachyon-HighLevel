package scripts;

import data.Table;
import locomotion.UnableToMoveException;
import orders.Speed;
import orders.order.ActuatorsOrder;
import pfg.config.Config;
import robot.Master;
import robot.Robot;
import utils.ConfigData;
import utils.Log;
import utils.math.Circle;
import utils.math.Shape;
import utils.math.Vec2;
import utils.math.VectCartesian;

// TODO


public class PaletsZoneChaos extends Script{


    private int xEntry = 200;
    private int yEntry = 1050;
    private Vec2[] positions = new VectCartesian[]{
            new VectCartesian(xEntry,yEntry),
            new VectCartesian(xEntry+ 300,yEntry+250)
    };



    public PaletsZoneChaos(Master robot, Table table) {super(robot, table); }

    @Override
    public void execute(Integer version) {

        boolean premierPaletPris = false;
        try{
            robot.turn(Math.PI/2);
            robot.useActuator(ActuatorsOrder.ACTIVE_LA_POMPE_DROITE);
            for (Vec2 position : positions) {
                if(premierPaletPris == true){
                    robot.turn(-Math.PI);
                }
                robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_SOL);
                robot.useActuator(ActuatorsOrder.ACTIVE_ELECTROVANNE_DROITE);
                robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_ASCENSEUR);
                robot.useActuator(ActuatorsOrder.DESACTIVE_ELECTROVANNE_DROITE);
                robot.useActuator(ActuatorsOrder.DESCEND_ASCENSEUR_DROIT_DE_UN_PALET);
                premierPaletPris = true;
            }
        robot.useActuator(ActuatorsOrder.DESACTIVE_LA_POMPE_DROITE);
        }catch (UnableToMoveException e) {
            e.printStackTrace();
        }

        Log.TABLE.debug("execution zoneChaos");

    }

    @Override
    public Shape entryPosition(Integer version) {return new Circle(new VectCartesian(xEntry, yEntry), 5); }

    @Override
    public void finalize(Exception e) { }

    @Override
    public void updateConfig(Config config) { }
}
