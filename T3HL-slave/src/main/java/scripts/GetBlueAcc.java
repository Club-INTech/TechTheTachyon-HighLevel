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
import utils.math.Calculs;
import utils.math.Vec2;
import utils.math.VectCartesian;

import java.util.concurrent.TimeUnit;

public class GetBlueAcc extends Script {
    //private int xEntry = 1200; WTF les positions
    //private int yEntry = 250;
    private int xBlue = -170; //FIXME: positions à faire (attention symétrie)
    private int yBlue = 150+150;
    private boolean symetrie;
    private int xEntry = -500;
    private int yEntry = 240;//250+ 30+10  ; //a tester


    /**
     * Offset avec la planche
     */
    private final int offsetRecalage = 31;

    /**
     * Offset pour corriger la mesure des sicks (différence réel - mesuré)
     */
    private final int offsetSick= 6;

    /**
     * Différence en Y et X entre le sick et le centre du robot
     */
    private final int ySickToRobotCenter=100;

    private final double dsick = 64;
    private boolean inSimulation;

    public GetBlueAcc(Slave robot, Table table) {
        super(robot, table);
    }

    @Override
    public void execute(Integer version) {
        // Nouvelle strat: on va pousser le bleu en premier, en faisant un arc de cercle avec le bras du secondaire
        try {
            robot.turn(-Math.PI/2);
            if (symetrie) {
                robot.recalageMeca(true,100+51);
            }
            else{
                robot.recalageMeca(true,100+54);
            }
            //robot.moveLengthwise(-yEntry,false);
            if(symetrie) {
                robot.turn(Math.PI);
            } else {
                robot.turn(0);
            }

            robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DU_SECONDAIRE_A_LA_POSITION_ACCELERATEUR_DEPOT,true);
            robot.useActuator(ActuatorsOrder.ACTIVE_ELECTROVANNE_DU_SECONDAIRE,true);
            robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DU_SECONDAIRE_A_LA_POSITION_ACCELERATEUR_DEPOT_FINAL,true);
            TimeUnit.SECONDS.sleep(1);
            robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DU_SECONDAIRE_A_LA_POSITION_MUSCLOR,true);
            robot.increaseScore(10);


            //robot.moveLengthwise(230,false);
            if (symetrie){
                robot.softGoTo(new VectCartesian(-500+230+100,154+100+34+10),false);
                robot.turn(Math.PI);
            }
            else {
                robot.softGoTo(new VectCartesian(-500 + 230, 154 + 100 + 34 - 30), false);
                robot.turn(0);
            }
            //robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DU_SECONDAIRE_A_LA_POSITION_MUSCLOR_RED);
            /*

          //  recalage();
           // robot.gotoPoint(new VectCartesian(xBlue, yBlue));

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

            //robot.gotoPoint(new VectCartesian(xBlue+50, yBlue+10)); // on répète la position pour être sûr qu'il est là

            if (!symetrie) {
                //robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DU_SECONDAIRE_A_LA_POSITION_MUSCLOR);
                robot.useActuator(ActuatorsOrder.ACTIVE_LA_POMPE_DU_SECONDAIRE);
                robot.useActuator(ActuatorsOrder.DESACTIVE_ELECTROVANNE_DU_SECONDAIRE, true);
                robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DU_SECONDAIRE_A_LA_POSITION_GOLDONIUM,true); /*Cette position permet de ne pas taper dans le palet*/
                TimeUnit.SECONDS.sleep(1);
                robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DU_SECONDAIRE_A_LA_POSITION_ACCELERATEUR_PREND_BLEU,true);
                // robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DU_SECONDAIRE_A_LA_POSITION_ACCELERATEUR2_SECONDAIRE,true);
                TimeUnit.SECONDS.sleep(1);
                //robot.moveLengthwise(-95-20, false);
                robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DU_SECONDAIRE_A_LA_POSITION_ASCENSEUR,true);
                robot.useActuator(ActuatorsOrder.ACTIVE_ELECTROVANNE_DU_SECONDAIRE,true);

            } else {
                //robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DU_SECONDAIRE_A_LA_POSITION_MUSCLOR);
                robot.useActuator(ActuatorsOrder.ACTIVE_LA_POMPE_DU_SECONDAIRE);
                robot.useActuator(ActuatorsOrder.DESACTIVE_ELECTROVANNE_DU_SECONDAIRE, true);
                robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DU_SECONDAIRE_A_LA_POSITION_GOLDONIUM,true); /*Cette position permet de ne pas taper dans le palet*/
                TimeUnit.SECONDS.sleep(1);
               // robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DU_SECONDAIRE_A_LA_POSITION_ACCELERATEUR2_SECONDAIRE,true);
                robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DU_SECONDAIRE_A_LA_POSITION_ACCELERATEUR_PREND_BLEU,true);
                TimeUnit.SECONDS.sleep(1);
                //robot.moveLengthwise(95+20, false);
                robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DU_SECONDAIRE_A_LA_POSITION_ASCENSEUR,true);
                robot.useActuator(ActuatorsOrder.ACTIVE_ELECTROVANNE_DU_SECONDAIRE,true);
            }
          //  robot.useActuator(ActuatorsOrder.DESCEND_MONTE_ASCENCEUR_SECONDAIRE_DE_UN_PALET);
            //robot.useActuator(ActuatorsOrder.DESCEND_ASCENSEUR_DU_SECONDAIRE_DE_UN_PALET);

            //robot.increaseScore(10);
            //robot.gotoPoint(new VectCartesian(-400,300));
            //recalage();
        } catch (UnableToMoveException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void recalage() {
        try {
            robot.turn(Math.PI/2);

            if(!inSimulation) {

                robot.computeNewPositionAndOrientation(Sick.NOTHING);
                int ecart_mesures_sicks=Sick.SICK_AVANT_GAUCHE.getLastMeasure() - Sick.SICK_ARRIERE_GAUCHE.getLastMeasure();
                double rapport = ecart_mesures_sicks / dsick;
                double teta = Math.atan(-rapport);
                float distanceToWall = (float) (Math.cos(teta)*((Sick.SICK_AVANT_GAUCHE.getLastMeasure() + Sick.SICK_ARRIERE_GAUCHE.getLastMeasure()) / 2 + offsetSick + ySickToRobotCenter));
                Log.POSITION.critical("no symetrie" + Sick.SICK_AVANT_GAUCHE.getLastMeasure() + " " + Sick.SICK_ARRIERE_GAUCHE.getLastMeasure() + " " + distanceToWall);

                Vec2 currentPosition = XYO.getRobotInstance().getPosition();
                robot.setPositionAndOrientation(new VectCartesian(currentPosition.getX(), distanceToWall + offsetRecalage), Calculs.modulo(teta+Math.PI/2, Math.PI));
                //System.out.println(" ALORS QUE JE SUIS LA : " + " Position =" + XYO.getRobotInstance().getPosition() + " orientation =" + XYO.getRobotInstance().getOrientation());

            }
        } catch (UnableToMoveException e) {
            e.printStackTrace();
        }
    }

    @Override //à adapter
    public Vec2 entryPosition(Integer version) {
        if (symetrie){
            return new VectCartesian(xEntry+110,yEntry);
        }
        else {
            return new VectCartesian(xEntry, yEntry);
        }
    }


    /**
     * Exécution d'actions pendant le mouvement jusqu'à la position d'entrée du script. Utile pour mettre les bras à la bonne position, baisser un ascenseur, etc.
     * @param version la version du script
     */
    @Override
    public void executeWhileMovingToEntry(int version){

        robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DU_SECONDAIRE_A_LA_POSITION_ASCENSEUR,true);
//        robot.useActuator(ActuatorsOrder.ACTIVE_ELECTROVANNE_DU_SECONDAIRE,true);
//        robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DU_SECONDAIRE_A_LA_POSITION_ASCENSEUR_HAUT,true);
//        robot.useActuator(ActuatorsOrder.DESCEND_MONTE_ASCENCEUR_SECONDAIRE_DE_UN_PALET,true);
//        robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DU_SECONDAIRE_A_LA_POSITION_ASCENSEUR_FOR_RED);
//        robot.useActuator(ActuatorsOrder.DESACTIVE_ELECTROVANNE_DU_SECONDAIRE);
    }

    @Override
    public void finalize(Exception e) { }

    @Override
    public void updateConfig(Config config) {
        this.symetrie = config.getString(ConfigData.COULEUR).equals("violet");
        this.inSimulation = config.getBoolean(ConfigData.SIMULATION);
    }

}
