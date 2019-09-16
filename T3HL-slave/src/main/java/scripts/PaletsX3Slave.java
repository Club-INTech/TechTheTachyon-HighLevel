package scripts;

import data.CouleurPalet;
import data.synchronization.SynchronizationWithBuddy;
import locomotion.UnableToMoveException;
import orders.order.ActuatorsOrder;
import pfg.config.Configurable;
import utils.HLInstance;
import utils.Offsets;
import utils.math.Vec2;
import utils.math.InternalVectCartesian;

import java.util.ArrayList;

public class PaletsX3Slave extends Script implements Offsets {
    /**
     * Position d'entrée du script
     */

    private ArrayList<InternalVectCartesian> positions = new ArrayList<>();
    private int xEntry = 1300; //1235;// 1338
    private int yEntry = 1300 ;//+  (int) ConfigData.ROBOT_RAY.getDefaultValue() ;
    private double offsetX;
    private double offsetY;
    private static final int DISTANCE_INTER_PUCK = 100;
    private int xFirstPuck=1320; //1235;
    private int yFirstPuck=1610;

    /**
     * constante
     */
    @Configurable
    private boolean symetry;
    private SynchronizationWithBuddy syncBuddy;


    public PaletsX3Slave(HLInstance hl, SynchronizationWithBuddy syncBuddy) {
        super(hl);
        this.syncBuddy = syncBuddy;
    }

    @Override
    public void execute(int version) {
        try {
            if(version == 0) {
                syncBuddy.sendBalanceFree();
            }

            positions.add(new InternalVectCartesian(xFirstPuck+offsetX, yFirstPuck+offsetY));     //???
            positions.add(new InternalVectCartesian(xFirstPuck+offsetX+DISTANCE_INTER_PUCK , yFirstPuck+offsetY));
            positions.add(new InternalVectCartesian(xFirstPuck+offsetX+2*DISTANCE_INTER_PUCK, yFirstPuck+offsetY));

            turn(Math.PI/2);

            moveLengthwise(600,false);

            if(!symetry) {
                robot.recalageMeca(true,-1800+positions.get(0).getY()+5);
                turn(Math.PI);
            }
            else{
                robot.recalageMeca(true,-1800+positions.get(0).getY()-15);
                turn(0);
            }
            //robot.recalageMeca(false,1500-positions.get(0).getX());

            if(!symetry) {
                robot.recalageMeca(false,1500-positions.get(0).getX());
                //robot.turn(-Math.PI/2);
                robot.setOrientation(Math.PI);
            }
            else{
                robot.recalageMeca(true,1400-positions.get(0).getX());          //-90 si on prend le rouge
                //robot.turn(Math.PI/2);
                robot.setOrientation(0);

            }

            getPuck();
            robot.pushPaletDroit(CouleurPalet.VERT); // TODO

            turn(-Math.PI/2);

            moveLengthwise(600, false);
            turn(Math.PI);
        }
        catch (UnableToMoveException e) {
            e.printStackTrace();
        }
    }

    private void getPuck() {
        robot.useActuator(ActuatorsOrder.ACTIVE_LA_POMPE_DU_SECONDAIRE);
        robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DU_SECONDAIRE_A_LA_POSITION_DISTRIBUTEUR, true); //TODO refaire position bras
        robot.useActuator(ActuatorsOrder.DESACTIVE_ELECTROVANNE_DU_SECONDAIRE);
        robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DU_SECONDAIRE_A_LA_POSITION_MUSCLOR, true);
    }

    @Override
    public Vec2 entryPosition(int version) {
        if (!symetry) {
            offsetX = Offsets.get(PALETSX3_X_JAUNE);
            offsetY = Offsets.get(PALETSX3_Y_JAUNE);
        } else {
            offsetX = Offsets.get(PALETSX3_X_VIOLET);
            offsetY = Offsets.get(PALETSX3_Y_VIOLET);
        }
        if (!symetry) {
            return new InternalVectCartesian(xEntry+offsetX, yEntry+offsetY-300);
        }
        return new InternalVectCartesian(xEntry+offsetX, yEntry+offsetY);
    }

    @Override
    public void finalize(Exception e) { }

}