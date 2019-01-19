package scripts;

import data.Table;
import locomotion.UnableToMoveException;
import orders.Speed;
import orders.order.ActuatorsOrder;
import pfg.config.Config;
import robot.Master;
import robot.Robot;
import utils.math.Circle;
import utils.math.Shape;
import utils.math.VectCartesian;


public class Accelerateur extends Script {
    /**
     * Position d'entrée du script
     */

    private int xEntry = 150;
    private int yEntry = 150;

    /**
     * constante
     */
    private int distavance = 19;

    public Accelerateur(Master robot, Table table) {
        super(robot, table);
    }

    @Override
    public void execute(Integer version) {
        try {

            /** Fait avancer le robot de distavance */
            robot.moveLengthwise(distavance, false);

            /** Fait tourner le robot vers PI */
            robot.turn(Math.PI);

            /** Active la pompe*/
            robot.useActuator(ActuatorsOrder.ACTIVE_LA_POMPE_GAUCHE);

            while (((Master) robot).getNbpaletsdroits() > 0) {
                /** Active l'electrovanne droite */
                robot.useActuator(ActuatorsOrder.ACTIVE_ELECTROVANNE_GAUCHE);

                /** Bras va chercher palet dans le stock */
                robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_ASCENSEUR);

                /** Bras met le palet dans l'accélérateur */
                robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_ACCELERATEUR);

                /** Désactive l'électrovanne */
                robot.useActuator(ActuatorsOrder.DESACTIVE_ELECTROVANNE_GAUCHE);

                /** Fait monter l'ascenceur */
                robot.useActuator(ActuatorsOrder.MONTE_ASCENCEUR_GAUCHE_DE_UN_PALET);

                /** Décrémentation du nb de palets*/
                ((Master) robot).decrement();

            }






            /


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

