package scripts;

import data.GameState;
import data.Table;
import locomotion.UnableToMoveException;
import orders.Speed;
import orders.order.ActuatorsOrder;
import pfg.config.Config;
import robot.Master;
import utils.Log;
import utils.math.Circle;
import utils.math.Shape;
import utils.math.VectCartesian;


public class Accelerateur extends Script {
    private final VectCartesian positionEcartementPalet;
    private final VectCartesian positionCoin;
    private final VectCartesian positionDepart;
    /**
     * Position d'entrée du script
     */

    private int xEntry = -170;
    private int yEntry = 340+50;

    /**
     * constante
     */
    private int distavance = 0;
    private int palet = -90;
    private final int ecartement = -20;
    private final int distanceToCorner = -30;

    public Accelerateur(Master robot, Table table) {
        super(robot, table);
        positionEcartementPalet = new VectCartesian(xEntry+palet+ecartement, yEntry);
        positionCoin = new VectCartesian(xEntry-distanceToCorner, yEntry);
        positionDepart = new VectCartesian(xEntry, yEntry);
    }

    private void actionBras(boolean cotedroite, boolean monteAsc /*TODO: tmp*/) {
        try {
            if (cotedroite) {
                if(monteAsc) {
                    robot.useActuator(ActuatorsOrder.MONTE_ASCENCEUR_DROIT_DE_UN_PALET);
                }
                robot.moveLengthwise(-palet-ecartement, false);
                robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_ASCENSEUR);
                robot.useActuator(ActuatorsOrder.DESACTIVE_ELECTROVANNE_DROITE, true);
                robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_ACCELERATEUR);
                robot.moveLengthwise(palet+ecartement,false);
                robot.useActuator(ActuatorsOrder.POUSSE_LE_PALET_BRAS_DROIT, true);
                robot.moveLengthwise(distanceToCorner, false);
                robot.useActuator(ActuatorsOrder.ACTIVE_ELECTROVANNE_DROITE, true);
                robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_RECULE, true);
                robot.moveLengthwise(-distanceToCorner, false);
                robot.popPaletDroit();
            } else {
                if(monteAsc) {
                    robot.useActuator(ActuatorsOrder.MONTE_ASCENCEUR_GAUCHE_DE_UN_PALET);
                }
                robot.moveLengthwise(palet+ecartement, false);
                robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_ASCENSEUR);
                robot.useActuator(ActuatorsOrder.DESACTIVE_ELECTROVANNE_GAUCHE, true);
                robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_ACCELERATEUR);
                robot.moveLengthwise(-palet-ecartement, false);
                robot.useActuator(ActuatorsOrder.POUSSE_LE_PALET_BRAS_GAUCHE, true);
                robot.moveLengthwise(-distanceToCorner, false);
                robot.useActuator(ActuatorsOrder.ACTIVE_ELECTROVANNE_GAUCHE, true);
                robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_RECULE, true);
                robot.moveLengthwise(distanceToCorner, false);
                robot.popPaletGauche();
            }
        } catch (UnableToMoveException a){
            a.printStackTrace();
        }
    }


    @Override
    public void execute(Integer version) {
        try {
            // TODO: inverser le sens: faire gauche avant droite->moins de rotations

            robot.turn(0);

            robot.moveLengthwise(-palet-ecartement, false);
            if(robot.getNbPaletsDroits() > 0) {
                robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_ACCELERATEUR);
                robot.useActuator(ActuatorsOrder.POUSSE_LE_PALET_BRAS_DROIT);
                robot.useActuator(ActuatorsOrder.ACTIVE_ELECTROVANNE_DROITE, true);
                robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_RECULE);
                robot.popPaletDroit();
                robot.increaseScore(10);
            }
            while (robot.getNbPaletsDroits() > 0) {
                actionBras(true, true);
                robot.increaseScore(10);
            }


            /**
             * Dire que le goldenium est libéré
             */
            GameState.GOLDENIUM_LIBERE.setData(true);
            Log.LOCOMOTION.warning("PRE POS DEPART");
            robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_ASCENSEUR, true);
            robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_ASCENSEUR, true);
            robot.followPathTo(positionDepart);
            Log.LOCOMOTION.warning("POST POS DEPART");
            robot.turn(Math.PI);
            robot.increaseScore(10);
            boolean first = false;
            while(robot.getNbPaletsGauches() > 0) {
                actionBras(false, first);
                robot.increaseScore(10);
                first = true;
            }
        } catch (UnableToMoveException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void executeWhileMovingToEntry(int version) {
        robot.useActuator(ActuatorsOrder.ACTIVE_LA_POMPE_DROITE);
        robot.useActuator(ActuatorsOrder.ACTIVE_LA_POMPE_GAUCHE);
        robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_ASCENSEUR);
        robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_ASCENSEUR);
        robot.useActuator(ActuatorsOrder.DESACTIVE_ELECTROVANNE_DROITE, true);
        robot.useActuator(ActuatorsOrder.DESACTIVE_ELECTROVANNE_GAUCHE, true);
    }

    @Override
    public Shape entryPosition(Integer version) {
        return new Circle(new VectCartesian(xEntry, yEntry), 5);
    }

    @Override
    public void finalize(Exception e) {
        robot.setSpeed(Speed.DEFAULT_SPEED);
    }

    @Override
    public void updateConfig(Config config) {
        super.updateConfig(config);
    }

}

