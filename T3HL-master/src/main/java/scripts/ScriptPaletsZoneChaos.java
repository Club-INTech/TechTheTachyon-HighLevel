package scripts;

import data.CouleurPalet;
import data.PaletsZoneChaos;
import data.Table;
import locomotion.UnableToMoveException;
import orders.order.ActuatorsOrder;
import pfg.config.Config;
import robot.Master;
import utils.ConfigData;
import utils.Log;
import utils.math.Circle;
import utils.math.Shape;
import utils.math.Vec2;
import utils.math.VectCartesian;

import java.util.concurrent.TimeUnit;

// TODO


public class ScriptPaletsZoneChaos extends Script{


    private int xEntry = 0;
    private int yEntry = 450;
    private Vec2[] positions = new VectCartesian[3];
    VectCartesian positionentre = new VectCartesian(xEntry,yEntry);
    int rayonRobot = 0;
    int rayonPalet= 38;

    public ScriptPaletsZoneChaos(Master robot, Table table) {super(robot, table); }

    @Override
    public void execute(Integer version) {
        float[] signes= new float[3];
        signes[0]= Math.signum(PaletsZoneChaos.RED_1_ZONE_CHAOS_YELLOW.getPosition().getX()-500);
        signes[1]=Math.signum(PaletsZoneChaos.RED_2_ZONE_CHAOS_YELLOW.getPosition().getX()-500);
        signes[2]=Math.signum(PaletsZoneChaos.GREEN_ZONE_CHAOS_YELLOW.getPosition().getX()-500);

        positions[0]=new VectCartesian(PaletsZoneChaos.RED_1_ZONE_CHAOS_YELLOW.getPosition().getX()+signes[0]*(rayonRobot+rayonPalet+10),PaletsZoneChaos.RED_1_ZONE_CHAOS_YELLOW.getPosition().getY());
        positions[1]=new VectCartesian(PaletsZoneChaos.RED_2_ZONE_CHAOS_YELLOW.getPosition().getX()+signes[1]*(rayonRobot+rayonPalet+10),PaletsZoneChaos.RED_2_ZONE_CHAOS_YELLOW.getPosition().getY());
        positions[2]=new VectCartesian(PaletsZoneChaos.GREEN_ZONE_CHAOS_YELLOW.getPosition().getX()+signes[2]*(rayonRobot+rayonPalet+10),PaletsZoneChaos.GREEN_ZONE_CHAOS_YELLOW.getPosition().getY());


        try{
            robot.followPathTo(positionentre);
            robot.useActuator(ActuatorsOrder.ACTIVE_LA_POMPE_DROITE);
            robot.useActuator(ActuatorsOrder.DESACTIVE_ELECTROVANNE_DROITE,true);
            int numero = 0;
            for (Vec2 position : positions) {
                robot.followPathTo(position);
                robot.turn(Math.PI/2);
                if(signes[numero]==-1){
                    robot.useActuator(ActuatorsOrder.DESCEND_ASCENSEUR_DROIT_DE_UN_PALET);
                    robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_SOL);
                    robot.useActuator(ActuatorsOrder.DESACTIVE_ELECTROVANNE_DROITE,true);
                    robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_ASCENSEUR);
                    robot.useActuator(ActuatorsOrder.ACTIVE_ELECTROVANNE_DROITE,true);
                    if (position == positions[0]){
                        table.removeTemporaryObstacle(table.getPaletRedUnZoneChaosYellow());
                        robot.pushPaletDroit(CouleurPalet.ROUGE);
                    }
                    if (position == positions[1]){
                        table.removeTemporaryObstacle(table.getPaletRedDeuxZoneChaosYellow());
                        robot.pushPaletDroit(CouleurPalet.ROUGE);

                    }
                    if (position == positions[2]){
                        table.removeTemporaryObstacle(table.getPaletGreenZoneChaosYellow());
                        robot.pushPaletDroit(CouleurPalet.VERT);
                    }
                }
                else{
                    robot.useActuator(ActuatorsOrder.DESCEND_ASCENSEUR_GAUCHE_DE_UN_PALET);
                    robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_SOL);
                    robot.useActuator(ActuatorsOrder.DESACTIVE_ELECTROVANNE_GAUCHE,true);
                    robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_ASCENSEUR);
                    robot.useActuator(ActuatorsOrder.ACTIVE_ELECTROVANNE_GAUCHE,true);
                    if (position == positions[0]) {
                        table.removeTemporaryObstacle(table.getPaletRedUnZoneChaosYellow());
                        robot.pushPaletGauche(CouleurPalet.ROUGE);
                    }
                    if (position == positions[1]) {
                        table.removeTemporaryObstacle(table.getPaletRedDeuxZoneChaosYellow());
                        robot.pushPaletGauche(CouleurPalet.ROUGE);
                    }
                    if (position == positions[2]){
                        table.removeTemporaryObstacle(table.getPaletGreenZoneChaosYellow());
                        robot.pushPaletGauche(CouleurPalet.VERT);
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
    public void finalize(Exception e) {
        table.removeAllChaosObstacles();
    }

    @Override
    public void updateConfig(Config config) {
        super.updateConfig(config);
        rayonRobot=config.getInt(ConfigData.ROBOT_RAY)+1;
    }
}
