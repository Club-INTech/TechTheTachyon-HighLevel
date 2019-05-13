package scripts;

import data.Table;
import orders.Speed;
import orders.order.ActuatorsOrder;
import pfg.config.Config;
import robot.Robot;
import utils.math.Circle;
import utils.math.Shape;
import utils.math.Vec2;
import utils.math.VectCartesian;
import locomotion.UnableToMoveException;

/**
 * Exemple d'un script
 *
 * @author sam
 */

public class ActivationPanneauDomotique extends Script{

    /** Position d'entrée du script */

    private int xEntry=370;
    private int yEntry=230;


    public ActivationPanneauDomotique(Robot robot, Table table) {
        super(robot, table);
    }

    /** Ensembles des actions à réaliser pendant le script
     *
     * @param version la version à executer
     *
     */

    @Override
    public void execute(Integer version) {
        try {
            /** Fait avancer le robot de 10mm */
            robot.moveLengthwise(10,false);

            /** Fait tourner le robot de -PI/2 */
            robot.turn(-Math.PI/2);

            /** Fait avancer vers le point de coordonnée (xEntry, yEntry) */
            robot.followPathTo(new VectCartesian(xEntry, yEntry));
        } catch (UnableToMoveException e) {
            e.printStackTrace();
        }
        /** Envoie l'ordre de fermer la porte avant au LL */
        robot.useActuator(ActuatorsOrder.FERME_PORTE_AVANT);
    }

    /**
     * Methode retournant la zone d'entrée du script, ici un cercle
     *
     * @param version la version à executer
     *
     */

    @Override
    public Vec2 entryPosition(Integer version) {
        return new VectCartesian(xEntry, yEntry);
    }

    /** Methode à executer en cas d'erreur pendant le script */

    @Override
    public void finalize(Exception e) {
        robot.setSpeed(Speed.DEFAULT_SPEED);
    }

    @Override
    public void updateConfig(Config config) {

    }
}
