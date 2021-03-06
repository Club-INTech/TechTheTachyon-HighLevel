package scripts;

import data.Table;
import data.XYO;
import data.synchronization.SynchronizationWithBuddy;
import data.table.Obstacle;
import locomotion.UnableToMoveException;
import orders.Speed;
import orders.order.ActuatorsOrder;
import pfg.config.Config;
import robot.Slave;
import utils.ConfigData;
import utils.Offsets;
import utils.TimeoutError;
import utils.container.Service;
import utils.math.Circle;
import utils.math.Shape;
import utils.math.Vec2;
import utils.math.VectCartesian;

import java.util.concurrent.TimeUnit;

public class Goldenium extends Script {

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
    private int yBalance1 = 1360-20; //a tester (vraie valeur: 1388)

    private int xBalance2 = 300;
    private int yBalance2 = 1500-350;

    //paramètres

    private VectCartesian positionBalance1;
    private VectCartesian positionBalance2;
    private boolean symetrie;
    private SynchronizationWithBuddy syncBuddy;
    private long balanceWaitTime;


    public Goldenium(Slave robot, Table table, SynchronizationWithBuddy syncBuddy) {
        super(robot, table);
        this.syncBuddy = syncBuddy;
    }

    @Override
    public void execute(Integer version) {
        double balanceOffsetX;
        double balanceOffsetY;
        if (symetrie){
            balanceOffsetX = Offsets.SECONDAIRE_BALANCE_OFFSET_X_VIOLET.get();
            balanceOffsetY = Offsets.SECONDAIRE_BALANCE_OFFSET_Y_VIOLET.get();
            positionBalance1 = new VectCartesian(xBalance1+30+balanceOffsetX, yBalance1+40+balanceOffsetY);
            positionBalance2 =new VectCartesian(xBalance2+balanceOffsetX, yBalance2+balanceOffsetY);
        }
        else {
            balanceOffsetX = Offsets.SECONDAIRE_BALANCE_OFFSET_X_JAUNE.get();
            balanceOffsetY = Offsets.SECONDAIRE_BALANCE_OFFSET_Y_JAUNE.get();
            positionBalance1 = new VectCartesian(xBalance1+20+balanceOffsetX, yBalance1-10+balanceOffsetY);
            positionBalance2 = new VectCartesian(xBalance2+balanceOffsetX, yBalance2+balanceOffsetY);
        }



        //attention il n'y qu'une seule pompe sur le robot secondaire
        /*try {
            if(!symetrie) {
                robot.turn(Math.PI);
                try {
                    robot.moveLengthwise(510,false);
                } catch (UnableToMoveException e) {
                    e.printStackTrace();
                }
                robot.turn(0);
            }
            else {
                robot.turn(Math.PI);
                try {
                    robot.moveLengthwise(510,false);
                } catch (UnableToMoveException e) {
                    e.printStackTrace();
                }
            }
        } catch (UnableToMoveException e) {
            e.printStackTrace();
        }*/

        /**
         * on tente un recalage mécanique
         */
        /*
        try {
            robot.turn(-Math.PI/2);
        } catch (UnableToMoveException e) {
            e.printStackTrace();
        }


        //insert mechanicle recalation here
        //
        robot.recalageMeca(20);
        //



        try {
            if(symetrie) {
                robot.turn(Math.PI);
            } else {
                robot.turn(0);
            }
        } catch (UnableToMoveException e) {
            e.printStackTrace();
        }
        */


        try {
           // robot.turn(-Math.PI/2);
           // robot.recalageMeca();
            //robot.moveLengthwise(-yEntry,false);
            //robot.moveLengthwise(-72+25,false);
           if(symetrie) {
                robot.turn(Math.PI);
                //robot.moveLengthwise(517-10,false,() -> { robot.useActuator(ActuatorsOrder.DESCEND_MONTE_ASCENCEUR_SECONDAIRE_DE_UN_PALET);});
                robot.moveLengthwise(517-10,false);     //FIXME
            } else {
                robot.turn(0);
                //robot.moveLengthwise(-517,false,() -> { robot.useActuator(ActuatorsOrder.DESCEND_MONTE_ASCENCEUR_SECONDAIRE_DE_UN_PALET);});
                robot.moveLengthwise(-517,false);       //FIXME
            }
            //robot.moveLengthwise(-517,false,() -> { robot.useActuator(ActuatorsOrder.DESCEND_MONTE_ASCENCEUR_SECONDAIRE_DE_UN_PALET);});

        } catch (UnableToMoveException e) {
            e.printStackTrace();
        }
        robot.useActuator(ActuatorsOrder.ACTIVE_LA_POMPE_DU_SECONDAIRE);
        robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DU_SECONDAIRE_A_LA_POSITION_GOLDONIUM,true);
        robot.useActuator(ActuatorsOrder.DESACTIVE_ELECTROVANNE_DU_SECONDAIRE,true);
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        /*try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
        robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DU_SECONDAIRE_A_LA_POSITION_MUSCLOR,true);
        robot.increaseScore(20);

        // TODO: Timeout?
        syncBuddy.waitForFreeBalance();
        try {
            // le principal part en même temps que nous
            syncBuddy.sendAcceleratorFree();
            robot.followPathTo(positionBalance2);
            table.removeTassot();
            robot.followPathTo(positionBalance1);
            if(!symetrie) {
                robot.turn(Math.PI);
            }
            else {
                robot.turn(0);
                robot.moveLengthwise(-100,false);
            }
        } catch (UnableToMoveException e) {
            e.printStackTrace();
        }

        robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DU_SECONDAIRE_A_LA_POSITION_GOLDONIUM_DEPOT,true);
        robot.useActuator(ActuatorsOrder.ACTIVE_ELECTROVANNE_DU_SECONDAIRE);
        robot.increaseScore(24);
        /*robot.useActuator(ActuatorsOrder.DESACTIVE_LA_POMPE_DU_SECONDAIRE);
        robot.useActuator(ActuatorsOrder.MONTE_ASCENSEUR_DU_SECONDAIRE_DE_UN_PALET);
        robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DU_SECONDAIRE_A_LA_POSITION_MUSCLOR,true);
        try {
            robot.turn(Math.PI/2);
        } catch (UnableToMoveException e) {
            e.printStackTrace();
        }
        robot.useActuator(ActuatorsOrder.MONTE_ASCENCEUR_DU_SECONDAIRE_POUR_CRACHER_LES_PALETS);
        robot.useActuator(ActuatorsOrder.CRACHE_UN_PALET);
        robot.useActuator(ActuatorsOrder.RANGE_CRACHE_PALET);
        robot.useActuator(ActuatorsOrder.DESCEND_ASCENCEUR_DU_SECONDAIRE_POUR_CRACHER_LES_PALETS);*/

        //robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DU_SECONDAIRE_A_LA_POSITION_ASCENSEUR);
        //robot.useActuator(ActuatorsOrder.DESACTIVE_LA_POMPE_DU_SECONDAIRE);
        robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DU_SECONDAIRE_A_LA_POSITION_ASCENSEUR,true);
        robot.useActuator(ActuatorsOrder.DESACTIVE_ELECTROVANNE_DU_SECONDAIRE);
        robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DU_SECONDAIRE_A_LA_POSITION_BALANCE_APRES_STOCK,true);
        robot.useActuator(ActuatorsOrder.ACTIVE_ELECTROVANNE_DU_SECONDAIRE);
        robot.increaseScore(12);
        robot.useActuator(ActuatorsOrder.DESACTIVE_LA_POMPE_DU_SECONDAIRE);
        robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DU_SECONDAIRE_A_LA_POSITION_ASCENSEUR,true);
        try {
            if(!symetrie) {
                robot.moveLengthwise(-60, false);
            }
            else{
                robot.moveLengthwise(60, false);
            }
            robot.turn(-Math.PI/2);
            robot.moveLengthwise(200,false);
/*            robot.softGoTo(new VectCartesian(200,750),false);
            robot.softGoTo(new VectCartesian(1200,750),false);
            syncBuddy.sendBalanceFree();*/
            
        } catch (UnableToMoveException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Vec2 entryPosition(Integer version) {
        if (!symetrie) {
            offsetX = Offsets.GOLDENIUM_X_JAUNE.get();
            offsetY = Offsets.GOLDENIUM_Y_JAUNE.get();
        } else {
            offsetX=Offsets.GOLDENIUM_X_VIOLET.get();
            offsetY=Offsets.GOLDENIUM_Y_VIOLET.get();
        }
        return new VectCartesian(xEntry+offsetX, yEntry+offsetY);
    }

    @Override
    public void finalize(Exception e) {
        robot.setSpeed(Speed.DEFAULT_SPEED);
    }

    @Override
    public void updateConfig(Config config) {
        this.symetrie = config.getString(ConfigData.COULEUR).equals("violet");
        balanceWaitTime = config.getLong(ConfigData.BALANCE_SLAVE_WAIT_TIME);
        super.updateConfig(config);
    }
}
