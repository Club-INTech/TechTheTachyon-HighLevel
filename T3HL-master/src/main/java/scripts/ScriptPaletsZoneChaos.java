package scripts;

import data.PaletsZoneChaos;
import data.Table;
import locomotion.UnableToMoveException;
import orders.order.ActuatorsOrder;
import pfg.config.Config;
import robot.Master;
import utils.Log;
import utils.math.Circle;
import utils.math.Shape;
import utils.math.Vec2;
import utils.math.VectCartesian;

// TODO


public class ScriptPaletsZoneChaos extends Script{


    private int xEntry = -1020;
    private int yEntry = 1050;
    private Vec2[] positions = new VectCartesian[3];



    public ScriptPaletsZoneChaos(Master robot, Table table) {super(robot, table); }

    @Override
    public void execute(Integer version) {
        positions[0]=new VectCartesian(xEntry,yEntry);
        positions[1]=PaletsZoneChaos.RED_1_ZONE_CHAOS_PURPLE.getPosition();
        positions[2]=PaletsZoneChaos.RED_2_ZONE_CHAOS_PURPLE.getPosition();
        positions[3]=PaletsZoneChaos.GREEN_ZONE_CHAOS_PURPLE.getPosition();

        for(int i=1; i<positions.length;i++){
            positions[i].setX(positions[i].getX()-220);
        }



        boolean premierPaletPris = false;
        try{
            robot.turn(Math.PI/2);
            robot.useActuator(ActuatorsOrder.ACTIVE_LA_POMPE_DROITE);
            robot.useActuator(ActuatorsOrder.DESACTIVE_ELECTROVANNE_DROITE);
            for (Vec2 position : positions) {
                if(premierPaletPris == false){
                    robot.gotoPoint(position);
                } else{ premierPaletPris=true;}
                robot.useActuator(ActuatorsOrder.DESCEND_ASCENSEUR_DROIT_DE_UN_PALET);
                robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_SOL);
                robot.useActuator(ActuatorsOrder.DESACTIVE_ELECTROVANNE_DROITE);
                robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_ASCENSEUR);
                robot.useActuator(ActuatorsOrder.ACTIVE_ELECTROVANNE_DROITE);

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
