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

import java.util.*;
import java.util.concurrent.TimeUnit;

// TODO


public class ScriptPaletsZoneChaos extends Script{


    private int xEntry = 900;
    private int yEntry = 1055;
    private Vec2[] positions = new VectCartesian[4];
    int rayonRobot = 190;
    int rayonPalet= 38;

    public ScriptPaletsZoneChaos(Master robot, Table table) {super(robot, table); }

    @Override
    public void execute(Integer version) {

        positions[0]=new VectCartesian(PaletsZoneChaos.RED_1_ZONE_CHAOS_YELLOW.getPosition().getX()+(rayonRobot+rayonPalet+10),PaletsZoneChaos.RED_1_ZONE_CHAOS_YELLOW.getPosition().getY());
        positions[1]=new VectCartesian(PaletsZoneChaos.RED_2_ZONE_CHAOS_YELLOW.getPosition().getX()+(rayonRobot+rayonPalet+10),PaletsZoneChaos.RED_2_ZONE_CHAOS_YELLOW.getPosition().getY());
        positions[2]=new VectCartesian(PaletsZoneChaos.GREEN_ZONE_CHAOS_YELLOW.getPosition().getX()+(rayonRobot+rayonPalet+10),PaletsZoneChaos.GREEN_ZONE_CHAOS_YELLOW.getPosition().getY());
        positions[3]=new VectCartesian(PaletsZoneChaos.BLUE_ZONE_CHAOS_YELLOW.getPosition().getX()+(rayonRobot+rayonPalet+10),PaletsZoneChaos.GREEN_ZONE_CHAOS_YELLOW.getPosition().getY());

/**
 * On trie les palets selon l'axe X pour les prendre de droite à gauche
 */
        Arrays.sort(positions,(v1, v2) -> {
            if (v1.getX() < v2.getX()){
                return 1;
            }
            else if (v1.getX() > v2.getX()){
                return -1;
            }
            else {
                return Integer.compare(v1.getY(), v2.getY());
            }
        });


        try{
            table.removeAllChaosObstacles();
            robot.useActuator(ActuatorsOrder.ACTIVE_LA_POMPE_GAUCHE);
            for (Vec2 position : positions) {
                robot.followPathTo(position);
                robot.turn(Math.PI/2);
                robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_SOL, true);
                robot.useActuator(ActuatorsOrder.DESACTIVE_ELECTROVANNE_GAUCHE,false);
                robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_DEPOT, true);
                robot.useActuator(ActuatorsOrder.ACTIVE_ELECTROVANNE_GAUCHE,true);
                robot.useActuator(ActuatorsOrder.DESCEND_MONTE_ASCENCEUR_GAUCHE_DE_UN_PALET);

                // à défaut de savoir la couleur, au moins on cassera pas les ascenseurs
                robot.pushPaletGauche(CouleurPalet.ROUGE);
                    /*
                    //Ce qui suit c'est du piff j'ai la flemme de vérifier la couleur parcequ'on s'en fou
                    if (position == positions[0]){
                        table.removeTemporaryObstacle(table.getPaletRedUnZoneChaosYellow());
                        robot.pushPaletGauche(CouleurPalet.ROUGE);
                    }
                    if (position == positions[1]){
                        table.removeTemporaryObstacle(table.getPaletRedDeuxZoneChaosYellow());
                        robot.pushPaletGauche(CouleurPalet.ROUGE);

                    }
                    if (position == positions[2]){
                        table.removeTemporaryObstacle(table.getPaletGreenZoneChaosYellow());
                        robot.pushPaletGauche(CouleurPalet.VERT);
                    }
                    if (position== positions[3]){
                        table.removeTemporaryObstacle(table.getPaletBlueZoneChaosYellow());
                        robot.pushPaletGauche(CouleurPalet.BLEU);
                    }*/

            }
            robot.useActuator(ActuatorsOrder.DESACTIVE_LA_POMPE_GAUCHE);
        }catch (UnableToMoveException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Vec2 entryPosition(Integer version) {return new VectCartesian(xEntry, yEntry); }

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
