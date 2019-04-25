package scripts;

import data.GameState;
import data.Table;
import locomotion.UnableToMoveException;
import orders.Speed;
import orders.order.ActuatorsOrder;
import pfg.config.Config;
import robot.Master;
import utils.math.Circle;
import utils.math.Shape;
import utils.math.VectCartesian;

public class PrecoupeAccelerateurx6 extends Script {

    private final VectCartesian positionEcartementPalet;
    private final VectCartesian positionDepart;
    /**
     * Position d'entrée du script
     */

    private int xEntry = 500;
    private int yEntry = 1206;

    /**
     * constante
     */
    private int distavance = 0;
    private int palet = -90;
    private final int ecartement = -20;
    private final int distanceToCorner = -30;
    private VectCartesian positionAccelerateur = new VectCartesian(-170,340);
    private VectCartesian position2palet6 = new VectCartesian(700,1206);
    private VectCartesian position3palet6 = new VectCartesian(900,1206);

    public PrecoupeAccelerateurx6(Master robot, Table table) {
        super(robot, table);
        positionEcartementPalet = new VectCartesian(xEntry + palet + ecartement, yEntry);
        positionDepart = new VectCartesian(xEntry, yEntry);
    }


    @Override
    public void execute(Integer version) {
        try {

            if (version==0){
                robot.turn(0);
                robot.useActuator(ActuatorsOrder.ACTIVE_LA_POMPE_GAUCHE);
                robot.useActuator(ActuatorsOrder.DESACTIVE_ELECTROVANNE_GAUCHE, true);
                robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_DISTRIBUTEUR);
                robot.useActuator(ActuatorsOrder.REMONTE_LE_BRAS_GAUCHE_DU_DISTRIBUTEUR_VERS_ASCENSEUR);
                robot.useActuator(ActuatorsOrder.ACTIVE_ELECTROVANNE_GAUCHE,true);
                robot.moveLengthwise(100,false);
                robot.useActuator(ActuatorsOrder.DESACTIVE_ELECTROVANNE_GAUCHE, true);
                robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_ACCELERATEUR);
                robot.gotoPoint(positionAccelerateur);
                robot.turn(Math.PI);


                // FIXME: juste pour la précoupe: on pousse le palet bleu de l'accélérateur
                robot.moveLengthwise(palet + ecartement, false);
                robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_ACCELERATEUR);
                robot.moveLengthwise(-palet - ecartement, false);
                robot.useActuator(ActuatorsOrder.POUSSE_LE_PALET_BRAS_GAUCHE, true);
                robot.increaseScore(20);
                robot.useActuator(ActuatorsOrder.POUSSE_LE_PALET_BRAS_GAUCHE, true);
                robot.moveLengthwise(-distanceToCorner, false);
                robot.useActuator(ActuatorsOrder.ACTIVE_ELECTROVANNE_GAUCHE, true);
                robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_ACCELERATEUR, true);
                robot.moveLengthwise(distanceToCorner, false);
                robot.moveLengthwise(-palet - ecartement, false);
                robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_ASCENSEUR);
                robot.useActuator(ActuatorsOrder.DESACTIVE_ELECTROVANNE_GAUCHE, true);
                robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_ACCELERATEUR);
                robot.moveLengthwise(palet + ecartement, false);
                robot.increaseScore(10);
                robot.useActuator(ActuatorsOrder.POUSSE_LE_PALET_BRAS_GAUCHE, true);
                robot.moveLengthwise(distanceToCorner, false);
                robot.useActuator(ActuatorsOrder.ACTIVE_ELECTROVANNE_GAUCHE, true);
                robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_RECULE, true);
                robot.moveLengthwise(-distanceToCorner, false);


                robot.gotoPoint(position2palet6);
                robot.turn(0);
                robot.useActuator(ActuatorsOrder.ACTIVE_LA_POMPE_GAUCHE);
                robot.useActuator(ActuatorsOrder.DESACTIVE_ELECTROVANNE_GAUCHE, true);
                robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_DISTRIBUTEUR);
                robot.useActuator(ActuatorsOrder.REMONTE_LE_BRAS_GAUCHE_DU_DISTRIBUTEUR_VERS_ASCENSEUR);
                robot.useActuator(ActuatorsOrder.ACTIVE_ELECTROVANNE_GAUCHE,true);
                robot.moveLengthwise(100,false);
                robot.useActuator(ActuatorsOrder.DESACTIVE_ELECTROVANNE_GAUCHE, true);
                robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_ACCELERATEUR);
                robot.gotoPoint(positionAccelerateur);
                robot.turn(Math.PI);
                robot.moveLengthwise(palet + ecartement, false);
                robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_ACCELERATEUR);
                robot.moveLengthwise(-palet - ecartement, false);
                robot.useActuator(ActuatorsOrder.POUSSE_LE_PALET_BRAS_GAUCHE, true);
                robot.increaseScore(10);
                robot.useActuator(ActuatorsOrder.POUSSE_LE_PALET_BRAS_GAUCHE, true);
                robot.moveLengthwise(-distanceToCorner, false);
                robot.useActuator(ActuatorsOrder.ACTIVE_ELECTROVANNE_GAUCHE, true);
                robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_ACCELERATEUR, true);
                robot.moveLengthwise(distanceToCorner, false);
                robot.moveLengthwise(-palet - ecartement, false);
                robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_ASCENSEUR);
                robot.useActuator(ActuatorsOrder.DESACTIVE_ELECTROVANNE_GAUCHE, true);
                robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_ACCELERATEUR);
                robot.moveLengthwise(palet + ecartement, false);
                robot.increaseScore(10);
                robot.useActuator(ActuatorsOrder.POUSSE_LE_PALET_BRAS_GAUCHE, true);
                robot.moveLengthwise(distanceToCorner, false);
                robot.useActuator(ActuatorsOrder.ACTIVE_ELECTROVANNE_GAUCHE, true);
                robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_RECULE, true);
                robot.moveLengthwise(-distanceToCorner, false);

                robot.gotoPoint(position3palet6);
                robot.turn(0);
                robot.useActuator(ActuatorsOrder.ACTIVE_LA_POMPE_GAUCHE);
                robot.useActuator(ActuatorsOrder.DESACTIVE_ELECTROVANNE_GAUCHE, true);
                robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_DISTRIBUTEUR);
                robot.useActuator(ActuatorsOrder.REMONTE_LE_BRAS_GAUCHE_DU_DISTRIBUTEUR_VERS_ASCENSEUR);
                robot.useActuator(ActuatorsOrder.ACTIVE_ELECTROVANNE_GAUCHE,true);
                robot.moveLengthwise(100,false);
                robot.useActuator(ActuatorsOrder.DESACTIVE_ELECTROVANNE_GAUCHE, true);
                robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_ACCELERATEUR);
                robot.gotoPoint(positionAccelerateur);
                robot.turn(Math.PI);
                robot.moveLengthwise(palet + ecartement, false);
                robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_ACCELERATEUR);
                robot.moveLengthwise(-palet - ecartement, false);
                robot.useActuator(ActuatorsOrder.POUSSE_LE_PALET_BRAS_GAUCHE, true);
                robot.increaseScore(10);
                robot.useActuator(ActuatorsOrder.POUSSE_LE_PALET_BRAS_GAUCHE, true);
                robot.moveLengthwise(-distanceToCorner, false);
                robot.useActuator(ActuatorsOrder.ACTIVE_ELECTROVANNE_GAUCHE, true);
                robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_ACCELERATEUR, true);
                robot.moveLengthwise(distanceToCorner, false);
                robot.moveLengthwise(-palet - ecartement, false);
                robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_ASCENSEUR);
                robot.useActuator(ActuatorsOrder.DESACTIVE_ELECTROVANNE_GAUCHE, true);
                robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_ACCELERATEUR);
                robot.moveLengthwise(palet + ecartement, false);
                robot.increaseScore(10);
                robot.useActuator(ActuatorsOrder.POUSSE_LE_PALET_BRAS_GAUCHE, true);
                robot.moveLengthwise(distanceToCorner, false);
                robot.useActuator(ActuatorsOrder.ACTIVE_ELECTROVANNE_GAUCHE, true);
                robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_RECULE, true);
                robot.moveLengthwise(-distanceToCorner, false);
                robot.moveLengthwise(palet + ecartement, false);
                robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_RECULE);
                robot.moveLengthwise(-palet - ecartement, false);
                robot.increaseScore(10);
            }
            else if (version==1){
                robot.turn(Math.PI);
                robot.useActuator(ActuatorsOrder.ACTIVE_LA_POMPE_DROITE);
                robot.useActuator(ActuatorsOrder.DESACTIVE_ELECTROVANNE_DROITE, true);
                robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_DISTRIBUTEUR);
                robot.useActuator(ActuatorsOrder.REMONTE_LE_BRAS_DROIT_DU_DISTRIBUTEUR_VERS_ASCENSEUR);
                robot.useActuator(ActuatorsOrder.ACTIVE_ELECTROVANNE_DROITE,true);
                robot.moveLengthwise(100,false);
                robot.useActuator(ActuatorsOrder.DESACTIVE_ELECTROVANNE_DROITE, true);
                robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_ACCELERATEUR);
                robot.gotoPoint(positionAccelerateur);
                robot.turn(0);


                // FIXME: juste pour la précoupe: on pousse le palet bleu de l'accélérateur
                robot.moveLengthwise(palet + ecartement, false);
                robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_ACCELERATEUR);
                robot.moveLengthwise(-palet - ecartement, false);
                robot.useActuator(ActuatorsOrder.POUSSE_LE_PALET_BRAS_DROIT, true);
                robot.increaseScore(20);
                robot.useActuator(ActuatorsOrder.POUSSE_LE_PALET_BRAS_DROIT, true);
                robot.moveLengthwise(-distanceToCorner, false);
                robot.useActuator(ActuatorsOrder.ACTIVE_ELECTROVANNE_DROITE, true);
                robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_ACCELERATEUR, true);
                robot.moveLengthwise(distanceToCorner, false);
                robot.moveLengthwise(-palet - ecartement, false);
                robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_ASCENSEUR);
                robot.useActuator(ActuatorsOrder.DESACTIVE_ELECTROVANNE_DROITE, true);
                robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_ACCELERATEUR);
                robot.moveLengthwise(palet + ecartement, false);
                robot.increaseScore(10);
                robot.useActuator(ActuatorsOrder.POUSSE_LE_PALET_BRAS_DROIT, true);
                robot.moveLengthwise(distanceToCorner, false);
                robot.useActuator(ActuatorsOrder.ACTIVE_ELECTROVANNE_DROITE, true);
                robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_RECULE, true);
                robot.moveLengthwise(-distanceToCorner, false);


                robot.gotoPoint(position2palet6);
                robot.turn(Math.PI);
                robot.useActuator(ActuatorsOrder.ACTIVE_LA_POMPE_DROITE);
                robot.useActuator(ActuatorsOrder.DESACTIVE_ELECTROVANNE_DROITE, true);
                robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_DISTRIBUTEUR);
                robot.useActuator(ActuatorsOrder.REMONTE_LE_BRAS_DROIT_DU_DISTRIBUTEUR_VERS_ASCENSEUR);
                robot.useActuator(ActuatorsOrder.ACTIVE_ELECTROVANNE_DROITE,true);
                robot.moveLengthwise(100,false);
                robot.useActuator(ActuatorsOrder.DESACTIVE_ELECTROVANNE_DROITE, true);
                robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_ACCELERATEUR);
                robot.gotoPoint(positionAccelerateur);
                robot.turn(0);
                robot.moveLengthwise(palet + ecartement, false);
                robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_ACCELERATEUR);
                robot.moveLengthwise(-palet - ecartement, false);
                robot.useActuator(ActuatorsOrder.POUSSE_LE_PALET_BRAS_DROIT, true);
                robot.increaseScore(10);
                robot.useActuator(ActuatorsOrder.POUSSE_LE_PALET_BRAS_DROIT, true);
                robot.moveLengthwise(-distanceToCorner, false);
                robot.useActuator(ActuatorsOrder.ACTIVE_ELECTROVANNE_DROITE, true);
                robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_ACCELERATEUR, true);
                robot.moveLengthwise(distanceToCorner, false);
                robot.moveLengthwise(-palet - ecartement, false);
                robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_ASCENSEUR);
                robot.useActuator(ActuatorsOrder.DESACTIVE_ELECTROVANNE_DROITE, true);
                robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_ACCELERATEUR);
                robot.moveLengthwise(palet + ecartement, false);
                robot.increaseScore(10);
                robot.useActuator(ActuatorsOrder.POUSSE_LE_PALET_BRAS_DROIT, true);
                robot.moveLengthwise(distanceToCorner, false);
                robot.useActuator(ActuatorsOrder.ACTIVE_ELECTROVANNE_DROITE, true);
                robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_RECULE, true);
                robot.moveLengthwise(-distanceToCorner, false);

                robot.gotoPoint(position3palet6);
                robot.turn(Math.PI);
                robot.useActuator(ActuatorsOrder.ACTIVE_LA_POMPE_DROITE);
                robot.useActuator(ActuatorsOrder.DESACTIVE_ELECTROVANNE_DROITE, true);
                robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_DISTRIBUTEUR);
                robot.useActuator(ActuatorsOrder.REMONTE_LE_BRAS_DROIT_DU_DISTRIBUTEUR_VERS_ASCENSEUR);
                robot.useActuator(ActuatorsOrder.ACTIVE_ELECTROVANNE_DROITE,true);
                robot.moveLengthwise(100,false);
                robot.useActuator(ActuatorsOrder.DESACTIVE_ELECTROVANNE_DROITE, true);
                robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_ACCELERATEUR);
                robot.gotoPoint(positionAccelerateur);
                robot.turn(0);
                robot.moveLengthwise(palet + ecartement, false);
                robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_ACCELERATEUR);
                robot.moveLengthwise(-palet - ecartement, false);
                robot.useActuator(ActuatorsOrder.POUSSE_LE_PALET_BRAS_DROIT, true);
                robot.increaseScore(10);
                robot.useActuator(ActuatorsOrder.POUSSE_LE_PALET_BRAS_DROIT, true);
                robot.moveLengthwise(-distanceToCorner, false);
                robot.useActuator(ActuatorsOrder.ACTIVE_ELECTROVANNE_DROITE, true);
                robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_ACCELERATEUR, true);
                robot.moveLengthwise(distanceToCorner, false);
                robot.moveLengthwise(-palet - ecartement, false);
                robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_ASCENSEUR);
                robot.useActuator(ActuatorsOrder.DESACTIVE_ELECTROVANNE_DROITE, true);
                robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_ACCELERATEUR);
                robot.moveLengthwise(palet + ecartement, false);
                robot.increaseScore(10);
                robot.useActuator(ActuatorsOrder.POUSSE_LE_PALET_BRAS_DROIT, true);
                robot.moveLengthwise(distanceToCorner, false);
                robot.useActuator(ActuatorsOrder.ACTIVE_ELECTROVANNE_DROITE, true);
                robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_RECULE, true);
                robot.moveLengthwise(-distanceToCorner, false);
                robot.moveLengthwise(palet + ecartement, false);
                robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_RECULE);
                robot.moveLengthwise(-palet - ecartement, false);
                robot.increaseScore(10);
            }





        } catch (UnableToMoveException e) {
            e.printStackTrace();
        }
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
