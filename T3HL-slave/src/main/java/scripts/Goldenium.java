package scripts;

import data.synchronization.SynchronizationWithBuddy;
import locomotion.UnableToMoveException;
import orders.OrderWrapper;
import orders.Speed;
import orders.order.ActuatorsOrder;
import pfg.config.Configurable;
import utils.HLInstance;
import utils.Offsets;
import utils.TimeoutError;
import utils.container.Module;
import utils.math.Vec2;
import utils.math.InternalVectCartesian;

import java.util.concurrent.TimeUnit;

public class Goldenium extends Script implements Offsets {

    //position d'entrée pour le recalage mécanique

    private int xEntry = -500;
    private int yEntry = 250;//250+ 30+10  ; //a tester
    private double offsetX;
    private double offsetY;

    //position du goldenium (unused parce qu'on y va avec des moveLengthWise)
    private int xGold = -775;
    private int yGold = 280;

    //position de fin

    private int xBalance1 =  137+60; //137//a tester
    private int yBalance1 = 1360-5; //a tester (vraie valeur: 1388)

    private int xBalance2 = 300;
    private int yBalance2 = 1500-350;

    //paramètres

    private InternalVectCartesian positionBalance1;
    private InternalVectCartesian positionBalance2;

    @Configurable
    private boolean symetry;
    private SynchronizationWithBuddy syncBuddy;
    @Configurable
    private long balanceSlaveWaitTime;
	private OrderWrapper orderWrapper;


    public Goldenium(HLInstance hl, OrderWrapper orderWrapper, SynchronizationWithBuddy syncBuddy) {
        super(hl);
	    this.orderWrapper = orderWrapper;
        this.syncBuddy = syncBuddy;
    }

    @Override
    public void execute(int version) {
        double balanceOffsetX;
        double balanceOffsetY;
        if (symetry){
            balanceOffsetX = Offsets.get(SECONDAIRE_BALANCE_OFFSET_X_VIOLET);
            balanceOffsetY = Offsets.get(SECONDAIRE_BALANCE_OFFSET_Y_VIOLET);
            positionBalance1 = new InternalVectCartesian(xBalance1+30+balanceOffsetX, yBalance1+40+balanceOffsetY);
            positionBalance2 =new InternalVectCartesian(xBalance2+balanceOffsetX, yBalance2+balanceOffsetY);
        }
        else {
            balanceOffsetX = Offsets.get(SECONDAIRE_BALANCE_OFFSET_X_JAUNE);
            balanceOffsetY = Offsets.get(SECONDAIRE_BALANCE_OFFSET_Y_JAUNE);
            positionBalance1 = new InternalVectCartesian(xBalance1+20+balanceOffsetX, yBalance1-10-50+balanceOffsetY);
            positionBalance2 = new InternalVectCartesian(xBalance2+balanceOffsetX, yBalance2+balanceOffsetY);
        }

        int decalageGold = Offsets.get(DECALAGE_GOLD_JAUNE);
        if(symetry) {
            decalageGold = Offsets.get(DECALAGE_GOLD_VIOLET);
        }

        try  {
            if(symetry) {
                turn(Math.PI);
                robot.setTranslationSpeed(Speed.SLOW_ALL);
                moveLengthwise(Offsets.get(MOVE_GOLDENIUM_VIOLET), false);
            } else {
                turn(Math.PI);
                robot.setTranslationSpeed(Speed.SLOW_ALL);
                moveLengthwise(Offsets.get(MOVE_GOLDENIUM_JAUNE), false);
                turn(0);
            }

            turn(Math.PI/2);
            moveLengthwise(decalageGold, false);
            if(symetry) {
                turn(Math.PI);
            } else {
                turn(0);
            }
        } catch(UnableToMoveException e) {
            e.printStackTrace();
        } finally {
            robot.setTranslationSpeed(Speed.DEFAULT_SPEED);
        }
        robot.useActuator(ActuatorsOrder.ACTIVE_LA_POMPE_DU_SECONDAIRE);
        robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DU_SECONDAIRE_A_LA_POSITION_GOLDONIUM,true);
        robot.useActuator(ActuatorsOrder.DESACTIVE_ELECTROVANNE_DU_SECONDAIRE,true);
        try {
            TimeUnit.MILLISECONDS.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DU_SECONDAIRE_A_LA_POSITION_MUSCLOR);
        robot.increaseScore(20);

        // TODO: Timeout?
        try {
            Module.withTimeout(3000, () -> syncBuddy.waitForFreeBalance());
            TimeUnit.SECONDS.sleep(5);
        } catch (TimeoutError error) {
            error.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            // le principal part en même temps que nous
            syncBuddy.sendAcceleratorFree();
            robot.getAudioPlayer().play("DEJAVU");
            followPathTo(positionBalance2);
            table.removeTassot();
            followPathTo(positionBalance1);
            if(!symetry) {
                turn(Math.PI);
                depose();
            }
            else {
                turn(0);
                try {
                    async("balance gold", () -> {
                        try {
                            TimeUnit.SECONDS.sleep(3);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        try  {
                            orderWrapper.immobilise();
                            orderWrapper.forceStop();
                        } catch (Exception e)  {
                            e.printStackTrace();
                        } finally {
                            depose();
                        }
                    });
                    moveLengthwise(-90,false);
                } catch (UnableToMoveException e) {
                    e.printStackTrace();
                }
            }
        } catch (UnableToMoveException e) {
            e.printStackTrace();
        }
    }

    private void depose() {
        robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DU_SECONDAIRE_A_LA_POSITION_GOLDONIUM_DEPOT,true);
        try {
            TimeUnit.MILLISECONDS.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        robot.useActuator(ActuatorsOrder.ACTIVE_ELECTROVANNE_DU_SECONDAIRE);
        robot.increaseScore(24);

        robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DU_SECONDAIRE_A_LA_POSITION_ASCENSEUR,true);
        robot.useActuator(ActuatorsOrder.DESACTIVE_ELECTROVANNE_DU_SECONDAIRE);
        robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DU_SECONDAIRE_A_LA_POSITION_BALANCE_APRES_STOCK,true);
        robot.useActuator(ActuatorsOrder.ACTIVE_ELECTROVANNE_DU_SECONDAIRE);
        robot.increaseScore(12);
        robot.useActuator(ActuatorsOrder.DESACTIVE_LA_POMPE_DU_SECONDAIRE);
        robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DU_SECONDAIRE_A_LA_POSITION_ASCENSEUR,true);

    }

    @Override
    public Vec2 entryPosition(int version) {
        if (!symetry) {
            offsetX = Offsets.get(GOLDENIUM_X_JAUNE);
            offsetY = Offsets.get(GOLDENIUM_Y_JAUNE);
        } else {
            offsetX = Offsets.get(GOLDENIUM_X_VIOLET);
            offsetY = Offsets.get(GOLDENIUM_Y_VIOLET);
        }
        return new InternalVectCartesian(xEntry+offsetX, yEntry+offsetY);
    }

    @Override
    public void finalize(Exception e) {
        robot.setSpeed(Speed.DEFAULT_SPEED);
    }

}
