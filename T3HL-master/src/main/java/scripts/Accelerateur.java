package scripts;

import data.GameState;
import data.Table;
import locomotion.UnableToMoveException;
import orders.Speed;
import orders.order.ActuatorsOrder;
import pfg.config.Config;
import robot.Master;
import robot.Robot;
import utils.ConfigData;
import utils.math.Circle;
import utils.math.Shape;
import utils.math.VectCartesian;

import static data.GameState.GOLDENIUM_LIBERE;


public class Accelerateur extends Script {
    /**
     * Position d'entrée du script
     */

    private int xEntry = -230;
    private int yEntry = 250; // TODO: ancienne valeur = 200

    /**
     * constante
     */
    private int distavance = 19;
    private int palet = 80;

    public Accelerateur(Master robot, Table table) {
        super(robot, table);
    }

    private void actionBras(boolean cotedroite) {
        try{
            if(!cotedroite) {

                robot.moveLengthwise(palet, false);
                robot.useActuator(ActuatorsOrder.ACTIVE_ELECTROVANNE_GAUCHE);
                robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_ASCENSEUR);
                robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_ACCELERATEUR);
                robot.moveLengthwise(-palet, false);
                robot.useActuator(ActuatorsOrder.DESACTIVE_ELECTROVANNE_GAUCHE);
                robot.useActuator(ActuatorsOrder.MONTE_ASCENCEUR_GAUCHE_DE_UN_PALET);
                ((Master) robot).popPaletGauche();
            } else {
                robot.moveLengthwise(palet, false);
                robot.useActuator(ActuatorsOrder.ACTIVE_ELECTROVANNE_DROITE);
                robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_ASCENSEUR);
                robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_ACCELERATEUR);
                robot.moveLengthwise(-palet,false);
                robot.useActuator(ActuatorsOrder.DESACTIVE_ELECTROVANNE_DROITE);
                robot.useActuator(ActuatorsOrder.MONTE_ASCENCEUR_DROIT_DE_UN_PALET);
                ((Master) robot).popPaletDroit();
            }
        } catch (UnableToMoveException a){
            a.printStackTrace();
        }
    }


    @Override
    public void execute(Integer version) {
        try {
            robot.moveToPoint(new VectCartesian(xEntry,yEntry-distavance + (int) ConfigData.ROBOT_RAY.getDefaultValue()) );
            robot.turn(Math.PI);
            robot.useActuator(ActuatorsOrder.ACTIVE_LA_POMPE_GAUCHE);
            robot.useActuator(ActuatorsOrder.ACTIVE_ELECTROVANNE_GAUCHE);
            robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_ASCENSEUR);
            robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_ACCELERATEUR);
            robot.useActuator(ActuatorsOrder.DESACTIVE_ELECTROVANNE_GAUCHE);
            robot.useActuator(ActuatorsOrder.MONTE_ASCENCEUR_GAUCHE_DE_UN_PALET);
            ((Master) robot).popPaletGauche();
            while (((Master) robot).getNbpaletsgauches() > 0) {
                actionBras(false);
                robot.increaseScore(10);
            }
            /**
             * Dire que le goldenium est libéré
             */
            GameState.GOLDENIUM_LIBERE.setData(true);
            robot.turn(0);
            robot.increaseScore(10);
            while(((Master) robot).getNbpaletsdroits() > 0){
                actionBras(true);
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

