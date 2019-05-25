package scripts;

import data.CouleurPalet;
import data.Sick;
import data.Table;
import locomotion.UnableToMoveException;
import orders.order.ActuatorsOrder;
import pfg.config.Config;
import robot.Slave;
import utils.ConfigData;
import utils.Offsets;
import utils.math.Vec2;
import utils.math.VectCartesian;

import java.util.ArrayList;

public class PaletsX3Slave extends Script{
    /**
     * Position d'entrée du script
     */

    private ArrayList<VectCartesian> positions = new ArrayList<>();
    private int xEntry = 1300; //1235;// 1338
    private int yEntry = 1250 ;//+  (int) ConfigData.ROBOT_RAY.getDefaultValue() ;
//    private int xEntry2 = 1300;
//    private int yEntry2 = 1630;
    private double offsetX;
    private double offsetY;
    private static final int DISTANCE_INTER_PUCK = 100;
    private int xFirstPuck=1320; //1235;
    private int yFirstPuck=1610;

    /**
     * constante
     */
    private boolean symetry;


    public PaletsX3Slave(Slave robot, Table table) {
        super(robot, table);
    }

    @Override
    public void execute(Integer version) {
        try {

            if (!symetry) {
                offsetX= Offsets.PALETSX3_X_JAUNE.get();
                offsetY=Offsets.PALETSX3_Y_JAUNE.get();
            } else {
                offsetX=Offsets.PALETSX3_X_VIOLET.get();
                offsetY=Offsets.PALETSX3_Y_VIOLET.get();
            }



            positions.add(new VectCartesian(xFirstPuck+offsetX, yFirstPuck+offsetY));     //???
            positions.add(new VectCartesian(xFirstPuck+offsetX+DISTANCE_INTER_PUCK , yFirstPuck+offsetY));
            positions.add(new VectCartesian(xFirstPuck+offsetX+2*DISTANCE_INTER_PUCK, yFirstPuck+offsetY));

            //recalage();
            if(!symetry) {
                robot.turn(Math.PI/2);
            }
            else{
                robot.turn(-Math.PI/2);
            }

            robot.recalageMeca(true,-1800+positions.get(0).getY());
            robot.turn(Math.PI);
            robot.recalageMeca(false,1500-positions.get(0).getX());


            getPuck();
            robot.pushPaletDroit(CouleurPalet.BLEU); // TODO
            robot.moveLengthwise(-100,false);
            //robot.gotoPoint(positions.get(1));

            getPuck();
            robot.pushPaletDroit(CouleurPalet.VERT); // TODO

            robot.moveLengthwise(-100,false);
            //robot.gotoPoint(positions.get(2));

            getPuck(); //FIXME: faire la nouvelle position et la rotation pour prendre le dernier palet de X3
            robot.pushPaletDroit(CouleurPalet.ROUGE); // TODO
            robot.useActuator(ActuatorsOrder.DESACTIVE_LA_POMPE_DU_SECONDAIRE);

            robot.recalageMeca(false,100);
            robot.turn(-Math.PI/2);
            robot.moveLengthwise(450, false);
            robot.turn(Math.PI);

        }
        catch (UnableToMoveException e) {
            e.printStackTrace();
        }
    }

    /*
    private void recalage() {
        if(symetry) {
            try {
                robot.turn(-Math.PI/2);
                robot.computeNewPositionAndOrientation(Sick.SECONDAIRE);
            } catch (UnableToMoveException e) {
                e.printStackTrace();
            }
        } else {
            try {
                robot.turn(Math.PI);
                robot.computeNewPositionAndOrientation(Sick.SECONDAIRE);
            } catch (UnableToMoveException e) {
                e.printStackTrace();
            }
        }
    }
    */

    private void getPuck() {
        robot.useActuator(ActuatorsOrder.ACTIVE_LA_POMPE_DU_SECONDAIRE);
        robot.useActuator(ActuatorsOrder.DESACTIVE_ELECTROVANNE_DU_SECONDAIRE);
        robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DU_SECONDAIRE_A_LA_POSITION_DISTRIBUTEUR, true);//TODO refaire position bras
        robot.useActuator(ActuatorsOrder.REMONTE_LE_BRAS_DU_SECONDAIRE_DU_DISTRIBUTEUR_VERS_ASCENSEUR, true);
        robot.useActuator(ActuatorsOrder.ACTIVE_ELECTROVANNE_DU_SECONDAIRE, true);
        robot.useActuator(ActuatorsOrder.DESCEND_ASCENSEUR_DU_SECONDAIRE_DE_UN_PALET);
    }

    @Override
    public Vec2 entryPosition(Integer version) {
        if (!symetry) {
            offsetX= Offsets.PALETSX3_X_JAUNE.get();
            offsetY=Offsets.PALETSX6_Y_JAUNE.get();
        } else {
            offsetX=Offsets.PALETSX3_X_VIOLET.get();
            offsetY=Offsets.PALETSX3_Y_VIOLET.get();
        }

        return new VectCartesian(xEntry+offsetX, yEntry+offsetY);
    }

    @Override
    public void finalize(Exception e) { }

    @Override
    public void updateConfig(Config config) {
        this.symetry = config.getString(ConfigData.COULEUR).equals("violet");
        super.updateConfig(config);
    }

}