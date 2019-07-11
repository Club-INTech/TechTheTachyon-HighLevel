package scripts;

import data.Table;
import locomotion.UnableToMoveException;
import orders.order.ActuatorsOrder;
import pfg.config.Configurable;
import robot.Slave;
import utils.math.Vec2;
import utils.math.VectCartesian;

public class Cracheur extends Script {
    /**
     * Position d'entrée du script
     */

    //Valeurs à ajuster pour le robot secondaire
    private int xEntry = 1500-1280;
    private int yEntry = 1180;

    @Configurable
    private boolean symetry;
    private boolean first = true;

    private int nbPalets = robot.getNbPaletsDroits();


    public Cracheur(Slave robot, Table table) {
        super(robot, table);
    }

    @Override
    public void execute(Integer version) {
        try {
            robot.softGoTo(new VectCartesian(1500-1330+60, 1380),false);
            if (symetry){
                robot.turn(0);
                robot.moveLengthwise(-60,false);
            }
            else{
                robot.turn(Math.PI);
                robot.moveLengthwise(60,false);
            }
        } catch (UnableToMoveException e) {
            e.printStackTrace();
        }

        /*robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DU_SECONDAIRE_A_LA_POSITION_OUST_BRAS,true);
        robot.useActuator(ActuatorsOrder.MONTE_ASCENCEUR_DU_SECONDAIRE_DE_UN_PALET_ET_MONTE_POUR_CRACHER_LES_PALETS, true);
        robot.waitForRightElevator();


        for (int i = 0; i < robot.getNbPaletsDroits(); i++) {
            robot.useActuator(ActuatorsOrder.CRACHE_UN_PALET, true);
            try {
                TimeUnit.MILLISECONDS.sleep(250);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            robot.useActuator(ActuatorsOrder.RANGE_CRACHE_PALET, true);
            try {
                TimeUnit.MILLISECONDS.sleep(250);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            robot.popPaletDroit();
            if(i != robot.getNbPaletsDroits()-1){
                robot.useActuator(ActuatorsOrder.MONTE_ASCENSEUR_DU_SECONDAIRE_DE_UN_PALET, true);
            }
        }

        robot.useActuator(ActuatorsOrder.DESCEND_ASCENCEUR_DU_SECONDAIRE_POUR_CRACHER_LES_PALETS);*/
        //robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DU_SECONDAIRE_A_LA_POSITION_MUSCLOR,true);
        robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DU_SECONDAIRE_A_LA_POSITION_ACCELERATEUR_PREND_BLEU,true);
        robot.useActuator(ActuatorsOrder.ACTIVE_ELECTROVANNE_DU_SECONDAIRE);
        if (symetry){
            robot.increaseScore(12);
        }
        else{
            robot.increaseScore(8);
        }
    }
    @Override //à adapter
    public Vec2 entryPosition(Integer version) { return new VectCartesian(xEntry, yEntry); }

    @Override
    public void finalize(Exception e) { }

}
