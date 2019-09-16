package scripts;

import locomotion.UnableToMoveException;
import orders.order.ActuatorsOrder;
import pfg.config.Configurable;
import utils.HLInstance;
import utils.math.Vec2;
import utils.math.InternalVectCartesian;

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


    public Cracheur(HLInstance hl) {
        super(hl);
    }

    @Override
    public void execute(int version) {
        try {
            robot.softGoTo(new InternalVectCartesian(1500-1330+60, 1380),false);
            if (symetry){
                turn(0);
                moveLengthwise(-60,false);
            }
            else{
                turn(Math.PI);
                moveLengthwise(60,false);
            }
        } catch (UnableToMoveException e) {
            e.printStackTrace();
        }
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
    public Vec2 entryPosition(int version) { return new InternalVectCartesian(xEntry, yEntry); }

    @Override
    public void finalize(Exception e) { }

}
