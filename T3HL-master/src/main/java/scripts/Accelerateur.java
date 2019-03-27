package scripts;

import data.CouleurPalet;
import data.GameState;
import data.Table;
import locomotion.UnableToMoveException;
import orders.Speed;
import orders.order.ActuatorsOrder;
import pfg.config.Config;
import robot.Master;
import utils.ConfigData;
import utils.math.Circle;
import utils.math.Shape;
import utils.math.VectCartesian;


public class Accelerateur extends Script {
    /**
     * Position d'entrée du script
     */

    private int xEntry = -65;
    private int yEntry = 360;

    /**
     * constante
     */
    private int distavance = 0;
    private int palet = 80;

    public Accelerateur(Master robot, Table table) {
        super(robot, table);
    }

    private void actionBras(boolean cotedroite) {
        try{
            if(!cotedroite) {

                robot.useActuator(ActuatorsOrder.MONTE_ASCENCEUR_GAUCHE_DE_UN_PALET);
                robot.moveLengthwise(palet, false);
                robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_ASCENSEUR);
                robot.useActuator(ActuatorsOrder.DESACTIVE_ELECTROVANNE_GAUCHE, true);
                robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_ACCELERATEUR);
                robot.moveLengthwise(-palet, false);
                robot.useActuator(ActuatorsOrder.ACTIVE_ELECTROVANNE_GAUCHE, true);
                robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_RECULE);
                ((Master) robot).popPaletGauche();
            } else {
                robot.useActuator(ActuatorsOrder.MONTE_ASCENCEUR_DROIT_DE_UN_PALET);
                robot.moveLengthwise(palet, false);
                robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_ASCENSEUR);
                robot.useActuator(ActuatorsOrder.DESACTIVE_ELECTROVANNE_DROITE, true);
                robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_ACCELERATEUR);
                robot.moveLengthwise(-palet,false);
                robot.useActuator(ActuatorsOrder.ACTIVE_ELECTROVANNE_DROITE, true);
                robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_RECULE);
                ((Master) robot).popPaletDroit();
            }
        } catch (UnableToMoveException a){
            a.printStackTrace();
        }
    }


    @Override
    public void execute(Integer version) {
        try {
            System.out.println("debug 2");
            robot.followPathTo(new VectCartesian(xEntry,yEntry-distavance + (int) ConfigData.ROBOT_RAY.getDefaultValue()) );
            for (int k=0; k<5; k++){
                ((Master) robot).pushPaletGauche(CouleurPalet.ROUGE);
                ((Master) robot).pushPaletDroit(CouleurPalet.ROUGE);
                System.out.println("debug 1");
            }
            System.out.println("debug 3");
            robot.turn(0);
            robot.useActuator(ActuatorsOrder.ACTIVE_LA_POMPE_DROITE);
            robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_ASCENSEUR);
            robot.useActuator(ActuatorsOrder.DESACTIVE_ELECTROVANNE_DROITE, true);
            robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_ACCELERATEUR);
            robot.useActuator(ActuatorsOrder.ACTIVE_ELECTROVANNE_DROITE, true);
            ((Master) robot).popPaletDroit();
            while (((Master) robot).getNbpaletsdroits() > 0) {
                actionBras(true);
                robot.increaseScore(10);
            }
            /**
             * Dire que le goldenium est libéré
             */
            GameState.GOLDENIUM_LIBERE.setData(true);
            robot.turn(Math.PI);
            robot.increaseScore(10);
            while(((Master) robot).getNbpaletsgauches() > 0){
                actionBras(false);
                robot.increaseScore(10);
            }
        } catch (UnableToMoveException e) {
            e.printStackTrace();
        }

    }
    @Override
    public Shape entryPosition(Integer version) {
        return new Circle(new VectCartesian(xEntry, yEntry), 42);
    }

    @Override
    public void finalize(Exception e) {
        robot.setSpeed(Speed.DEFAULT_SPEED);
    }

    @Override
    public void updateConfig(Config config) { }

}

