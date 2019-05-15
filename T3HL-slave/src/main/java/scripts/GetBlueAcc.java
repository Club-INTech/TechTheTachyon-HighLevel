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
    private int xBlue = -210; //FIXME: positions à faire
    private int yBlue = 270;
    private boolean symetrie;

    public GetBlueAcc(Slave robot, Table table) {
        super(robot, table);
    }

    @Override
    public void execute(Integer version) {
        /*try {
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
        robot.useActuator(ActuatorsOrder.DESCEND_ASCENSEUR_DU_SECONDAIRE_DE_UN_PALET);*/

        // Nouvelle strat: on va pousser le bleu en premier, en faisant un arc de cercle avec le bras du secondaire

        try {
            if (!symetrie) {
                robot.turn(Math.PI);
                robot.moveLengthwise(1000, false);
                robot.followPathTo(new VectCartesian(xBlue, yBlue));
                robot.turn(Math.PI/2);
                robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DU_SECONDAIRE_A_LA_POSITION_ACCELERATEUR_SECONDAIRE);
                robot.turn(0);
                robot.turn(-0.78);
                robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DU_SECONDAIRE_A_LA_POSITION_ASCENSEUR);
                robot.turn(Math.PI);


            }
            else {
                robot.turn(0);
                robot.moveLengthwise(1000, false);
                robot.followPathTo(new VectCartesian(-(xBlue), yBlue));
                robot.turn(-(Math.PI/2));
                robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DU_SECONDAIRE_A_LA_POSITION_ACCELERATEUR_SECONDAIRE);
                robot.turn(0.78);
                robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DU_SECONDAIRE_A_LA_POSITION_ASCENSEUR);
                robot.turn(0);


            }
        } catch (UnableToMoveException e) {
            e.printStackTrace();
        }




    }
    @Override //à adapter
    public Vec2 entryPosition(Integer version) { return new VectCartesian(xEntry, yEntry); }

    @Override
    public void finalize(Exception e) { }

    @Override
    public void updateConfig(Config config) { this.symetrie = config.getString(ConfigData.COULEUR).equals("violet"); }

}
