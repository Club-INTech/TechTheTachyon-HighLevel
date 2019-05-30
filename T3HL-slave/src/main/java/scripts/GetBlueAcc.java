package scripts;

import data.Table;
import locomotion.UnableToMoveException;
import orders.order.ActuatorsOrder;
import pfg.config.Config;
import robot.Slave;
import utils.ConfigData;
import utils.Offsets;
import utils.math.Vec2;
import utils.math.VectCartesian;

import java.util.concurrent.TimeUnit;

public class GetBlueAcc extends Script {
    private int xBlue = -170; //FIXME: positions à faire (attention symétrie)
    private int yBlue = 150+150;
    private boolean symetrie;
    private int xEntry = -500;
    private int yEntry = 240;//250+ 30+10  ; //a tester
    private int offsetY;
    private int offsetX;

    public GetBlueAcc(Slave robot, Table table) {
        super(robot, table);
    }

    @Override
    public void execute(Integer version) {
        try {
            robot.turn(-Math.PI/2);
            if (symetrie) {
                robot.recalageMeca(true,100+51+offsetY);
            }
            else{
                robot.recalageMeca(true,100+54+offsetY);
            }

            robot.setOrientation(-Math.PI/2);
            if(symetrie) {
                robot.turn(Math.PI);
            } else {
                robot.turn(0);
            }

            robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DU_SECONDAIRE_A_LA_POSITION_ACCELERATEUR_DEPOT,true);
            robot.useActuator(ActuatorsOrder.ACTIVE_ELECTROVANNE_DU_SECONDAIRE,true);
            robot.useActuator(ActuatorsOrder.DESACTIVE_LA_POMPE_DU_SECONDAIRE);
            robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DU_SECONDAIRE_A_LA_POSITION_ACCELERATEUR_DEPOT_FINAL,true);
            TimeUnit.SECONDS.sleep(1);
            robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DU_SECONDAIRE_A_LA_POSITION_MUSCLOR);
            robot.increaseScore(20);
            robot.useActuator(ActuatorsOrder.ACTIVE_LA_POMPE_DU_SECONDAIRE);


            double offsetXGoto = Offsets.GETBLUEACC_X_RETRAIT_JAUNE.get();
            double offsetYGoto = Offsets.GETBLUEACC_Y_RETRAIT_JAUNE.get();
            if(symetrie) {
                offsetXGoto = Offsets.GETBLUEACC_X_RETRAIT_VIOLET.get();
                offsetYGoto = Offsets.GETBLUEACC_Y_RETRAIT_VIOLET.get();
            }
            //robot.moveLengthwise(230,false);
            if (symetrie){
                robot.softGoTo(new VectCartesian(-500+230+100+offsetXGoto,154+100+34+10+offsetYGoto),false);
                robot.turn(Math.PI);
            }
            else {
                robot.softGoTo(new VectCartesian(-500 + 230+offsetXGoto, 154 + 100 + 34 - 30+offsetYGoto), false);
                robot.turn(0);
            }
            robot.useActuator(ActuatorsOrder.ACTIVE_LA_POMPE_DU_SECONDAIRE);
            robot.useActuator(ActuatorsOrder.DESACTIVE_ELECTROVANNE_DU_SECONDAIRE, true);
            robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DU_SECONDAIRE_A_LA_POSITION_GOLDONIUM_POUR_BLEU,true); /*Cette position permet de ne pas taper dans le palet*/
            TimeUnit.MILLISECONDS.sleep(250);

            robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DU_SECONDAIRE_A_LA_POSITION_ACCELERATEUR_PREND_BLEU,true);
            TimeUnit.MILLISECONDS.sleep(250);
            async("Remonte le palet bleu dans l'ascenseur du secondaire", () -> {
                robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DU_SECONDAIRE_A_LA_POSITION_ASCENSEUR,true);
                robot.useActuator(ActuatorsOrder.ACTIVE_ELECTROVANNE_DU_SECONDAIRE,true);
            });
        } catch (UnableToMoveException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override //à adapter
    public Vec2 entryPosition(Integer version) {
        if (symetrie){

            offsetX = (int)Offsets.GETBLUEACC_X_VIOLET.get();
            offsetY = (int)Offsets.GETBLUEACC_Y_VIOLET.get();
            return new VectCartesian(xEntry+110,yEntry);
        }
        else {
            offsetX = (int)Offsets.GETBLUEACC_X_JAUNE.get();
            offsetY = (int)Offsets.GETBLUEACC_Y_JAUNE.get();
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
        //c cassé, jsp pk il envoie down, demande d'attendre et reçoit quasi instantanément la confirmation de "je bouge plus" ...
        /*
        robot.useActuator(ActuatorsOrder.ACTIVE_ELECTROVANNE_DU_SECONDAIRE,true);
        robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DU_SECONDAIRE_A_LA_POSITION_ASCENSEUR_HAUT,true);
        //robot.useActuator(ActuatorsOrder.DESCEND_MONTE_ASCENCEUR_SECONDAIRE_DE_UN_PALET,true);
        robot.useActuator(ActuatorsOrder.DESCEND_ASCENSEUR_DROIT_DE_UN_PALET,true);
        robot.waitForRightElevator();
        robot.useActuator(ActuatorsOrder.MONTE_ASCENCEUR_DROIT_DE_UN_PALET,true);
        robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DU_SECONDAIRE_A_LA_POSITION_ASCENSEUR_FOR_RED);
        robot.useActuator(ActuatorsOrder.DESACTIVE_ELECTROVANNE_DU_SECONDAIRE);
        */
    }

    @Override
    public void finalize(Exception e) { }

    @Override
    public void updateConfig(Config config) {
        this.symetrie = config.getString(ConfigData.COULEUR).equals("violet");
    }

}
