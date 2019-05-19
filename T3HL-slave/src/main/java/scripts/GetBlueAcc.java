package scripts;

import data.Sick;
import data.Table;
import data.XYO;
import locomotion.UnableToMoveException;
import orders.order.ActuatorsOrder;
import pfg.config.Config;
import robot.Slave;
import utils.ConfigData;
import utils.Log;
import utils.math.Vec2;
import utils.math.VectCartesian;

public class GetBlueAcc extends Script {
    //private int xEntry = 1200; WTF les positions
    //private int yEntry = 250;
    private int xBlue = -140; //FIXME: positions à faire
    private int yBlue = 285;
    private boolean symetrie;

    /**
     * Offset avec la planche
     */
    private final int offsetRecalage = 31+5;

    /**
     * Offset pour corriger la mesure des sicks (différence réel - mesuré)
     */
    private final int offsetSick= 6;

    /**
     * Différence en Y et X entre le sick et le centre du robot
     */
    private final int ySickToRobotCenter=100;

    public GetBlueAcc(Slave robot, Table table) {
        super(robot, table);
    }

    @Override
    public void execute(Integer version) {
        // Nouvelle strat: on va pousser le bleu en premier, en faisant un arc de cercle avec le bras du secondaire
        try {
            /*
            robot.turn(Math.PI);
            robot.followPathTo(new VectCartesian(xBlue, yBlue));

            robot.turn(Math.PI/2);
            robot.computeNewPositionAndOrientation(Sick.NOTHING);
            // === Début recalage ===
            double dsick = 64;
            int ecart_mesures_sicks=Sick.SICK_AVANT_GAUCHE.getLastMeasure() - Sick.SICK_ARRIERE_GAUCHE.getLastMeasure();
            double rapport = ecart_mesures_sicks / dsick;
            if(symetrie) {
                rapport = -rapport;
            }
            double teta = Math.atan(rapport);

            float averageDistance = (float) (Math.cos(teta)*((Sick.SICK_AVANT_GAUCHE.getLastMeasure() + Sick.SICK_ARRIERE_GAUCHE.getLastMeasure()) / 2 + this.offsetSick + this.ySickToRobotCenter) + offsetRecalage);

            Vec2 currentPosition = XYO.getRobotInstance().getPosition();
            robot.gotoPoint(new VectCartesian(currentPosition.getX(), currentPosition.getY() + this.yBlue - averageDistance));
            // === Fin recalage ===

            if(symetrie) {
                robot.turn(Math.PI+0.78/2);
                robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DU_SECONDAIRE_A_LA_POSITION_ACCELERATEUR_SECONDAIRE, true);
                robot.turn(Math.PI);
            } else {
                robot.turn(0.78/2);
                robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DU_SECONDAIRE_A_LA_POSITION_ACCELERATEUR_SECONDAIRE, true);
                robot.turn(0);
            }
            // voir si c'est nécessaire: robot.turn(0);
            robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DU_SECONDAIRE_A_LA_POSITION_ASCENSEUR);
            */


            // test sans rotation

            robot.turn(Math.PI);
            robot.followPathTo(new VectCartesian(xBlue, yBlue+10));


            if (!symetrie) {
                robot.turn(0);
                //robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DU_SECONDAIRE_A_LA_POSITION_ACCELERATEUR_SECONDAIRE,true);
                robot.moveLengthwise(-15, false);
                //robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DU_SECONDAIRE_A_LA_POSITION_ASCENSEUR, true);
            } else {
                robot.turn(Math.PI);
                robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DU_SECONDAIRE_A_LA_POSITION_ACCELERATEUR_SECONDAIRE,true);
                robot.moveLengthwise(15, false);
                robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DU_SECONDAIRE_A_LA_POSITION_ASCENSEUR, true);
            }
            Log.STRATEGY.critical("DZQDL PRE SCORE");
            robot.increaseScore(10);

            Log.STRATEGY.critical("DZQDL");
        } catch (UnableToMoveException e) {
            e.printStackTrace();
        }
    }

    @Override //à adapter
    public Vec2 entryPosition(Integer version) { return new VectCartesian(xBlue+10, yBlue); }

    @Override
    public void finalize(Exception e) { }

    @Override
    public void updateConfig(Config config) {
        this.symetrie = config.getString(ConfigData.COULEUR).equals("violet");
    }

}
