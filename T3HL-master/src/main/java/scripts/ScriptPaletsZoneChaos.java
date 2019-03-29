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


    private int xEntry = 0;
    private int yEntry = 355;
    private Vec2[] positions = new VectCartesian[3];
    VectCartesian positionentre = new VectCartesian(xEntry,yEntry);



    public ScriptPaletsZoneChaos(Master robot, Table table) {super(robot, table); }

    @Override
    public void execute(Integer version) {
        float[] signes= new float[3];
        signes[0]= Math.signum(PaletsZoneChaos.RED_1_ZONE_CHAOS_YELLOW.getPosition().getX()+500);
        signes[1]=Math.signum(PaletsZoneChaos.RED_2_ZONE_CHAOS_YELLOW.getPosition().getX()+500);
        signes[2]=Math.signum(PaletsZoneChaos.GREEN_ZONE_CHAOS_YELLOW.getPosition().getX()+500);

        for (int i=1;i<positions.length;i++) {
            if(i==1){
                positions[0]=new VectCartesian(PaletsZoneChaos.RED_1_ZONE_CHAOS_YELLOW.getPosition().getX()+signes[0]*221,PaletsZoneChaos.RED_1_ZONE_CHAOS_YELLOW.getPosition().getY());
            }
            if(i==2){
                positions[1]=new VectCartesian(PaletsZoneChaos.RED_2_ZONE_CHAOS_YELLOW.getPosition().getX()+signes[1]*221,PaletsZoneChaos.RED_2_ZONE_CHAOS_YELLOW.getPosition().getY());
            }
            if(i==3){
                positions[2]=new VectCartesian(PaletsZoneChaos.GREEN_ZONE_CHAOS_YELLOW.getPosition().getX()+signes[2]*221,PaletsZoneChaos.GREEN_ZONE_CHAOS_YELLOW.getPosition().getY());
            }

        }




        try{
            robot.followPathTo(positionentre);
            robot.useActuator(ActuatorsOrder.ACTIVE_LA_POMPE_DROITE);
            robot.useActuator(ActuatorsOrder.DESACTIVE_ELECTROVANNE_DROITE,true);
            int numero = 0;
            for (Vec2 position : positions) {
                robot.followPathTo(position);
                if(signes[numero]==-1){
                robot.useActuator(ActuatorsOrder.DESCEND_ASCENSEUR_DROIT_DE_UN_PALET);
                robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_SOL);
                robot.useActuator(ActuatorsOrder.DESACTIVE_ELECTROVANNE_DROITE,true);
                robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_ASCENSEUR);
                robot.useActuator(ActuatorsOrder.ACTIVE_ELECTROVANNE_DROITE,true);
                if (position == positions[0]){
                    table.removeFixedObstacle(table.getPaletRedUnZoneChaosYellow());
                }
                if (position == positions[1]){
                    table.removeFixedObstacle(table.getPaletRedDeuxZoneChaosYellow());
                }
                if (position == positions[2]){
                    table.removeFixedObstacle(table.getPaletGreenZoneChaosYellow());
                }
                }
                else{
                    robot.useActuator(ActuatorsOrder.DESCEND_ASCENSEUR_GAUCHE_DE_UN_PALET);
                    robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_SOL);
                    robot.useActuator(ActuatorsOrder.DESACTIVE_ELECTROVANNE_GAUCHE,true);
                    robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_ASCENSEUR);
                    robot.useActuator(ActuatorsOrder.ACTIVE_ELECTROVANNE_GAUCHE,true);
                    if (position == positions[0]){
                        table.removeFixedObstacle(table.getPaletRedUnZoneChaosYellow());
                    }
                    if (position == positions[1]){
                        table.removeFixedObstacle(table.getPaletRedDeuxZoneChaosYellow());
                    }
                    if (position == positions[2]){
                        table.removeFixedObstacle(table.getPaletGreenZoneChaosYellow());
                    }
                }
                numero=numero+1;

            }
        robot.useActuator(ActuatorsOrder.DESACTIVE_LA_POMPE_DROITE,true);
        robot.useActuator(ActuatorsOrder.DESACTIVE_LA_POMPE_GAUCHE,true);
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
