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
    private final VectCartesian positionDepart;
    /**
     * Position d'entrée du script
     */

    private int xEntry = -210-30;
    private int yEntry = 340+20;

    /**
     * constante
     */
    private int distavance = 0;
    private int palet = -90;
    private final int ecartement = 20;
    private final int distanceToCorner = -30;


    public Accelerateur(Master robot, Table table) {
        super(robot, table);
        positionDepart = new VectCartesian(xEntry, yEntry);
    }

    private void actionBras(boolean coteDroit, boolean firstOfThisSide, boolean firstDone) {
        try {
            if (coteDroit) {
                if (!firstOfThisSide) {
                    robot.useActuator(ActuatorsOrder.MONTE_ASCENCEUR_DROIT_DE_UN_PALET);
                }
                if (firstDone) {
                    robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_ASCENSEUR, true);
                }
                else{
                    robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_ASCENSEUR, false);
                    robot.moveLengthwise(-palet - ecartement, false);
                }
                robot.useActuator(ActuatorsOrder.DESACTIVE_ELECTROVANNE_DROITE, true);
                robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_ACCELERATEUR, true);
                robot.useActuator(ActuatorsOrder.POUSSE_LE_PALET_BRAS_DROIT, true);
                robot.moveLengthwise(distanceToCorner+palet+ecartement, false);
                robot.useActuator(ActuatorsOrder.ACTIVE_ELECTROVANNE_DROITE, true);
                robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_RECULE, false);
                robot.moveLengthwise(-distanceToCorner-palet-ecartement, false);
                robot.popPaletDroit();
            } else {
                if (!firstOfThisSide) {
                    //On monte l'ascenseur gauche quand nécessaire
                    robot.useActuator(ActuatorsOrder.MONTE_ASCENCEUR_GAUCHE_DE_UN_PALET);
                }

                if (firstDone) {
                    //On envoie le bras à la position ascenseur
                    robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_ASCENSEUR, true);
                }
                else{
                    //On envoie le bras à la position ascenseur
                    robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_ASCENSEUR, false);
                    //On se déplace pour mettre un autre palet sur la rampe
                    robot.moveLengthwise(palet + ecartement, false);
                }

                //On aspire
                robot.useActuator(ActuatorsOrder.DESACTIVE_ELECTROVANNE_GAUCHE, true);

                //On place le palet sur la rampe
                robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_ACCELERATEUR, true);

                //On pousse le palet contre la rampe
                robot.useActuator(ActuatorsOrder.POUSSE_LE_PALET_BRAS_GAUCHE, true);

                //On se déplace en ayant le palet sur la rampe
                robot.moveLengthwise(-palet-ecartement-distanceToCorner, false);

                //On casse le vide
                robot.useActuator(ActuatorsOrder.ACTIVE_ELECTROVANNE_GAUCHE, true);

                //On recule le bras
                robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_ASCENSEUR, false);

                //On recule
                robot.moveLengthwise(distanceToCorner+palet+ecartement, false);

                //On met à jour la tour gauche
                robot.popPaletGauche();
            }
        } catch (UnableToMoveException a){
            a.printStackTrace();
        }
    }


    @Override
    public void execute(Integer version) {
        try {
            robot.useActuator(ActuatorsOrder.ACTIVE_LA_POMPE_DROITE, false);
            robot.useActuator(ActuatorsOrder.ACTIVE_LA_POMPE_GAUCHE, false);

            //On se cale dans la bonne orientation : vers le camp ennemi
            robot.turn(Math.PI);

            //On dépose tous les palets gauche en priorité
            boolean firstDone = false;
            boolean firstOfThisSide = true;
            while (robot.getNbPaletsGauches() > 0) {
                actionBras(false, firstOfThisSide, firstDone);
                GameState.GOLDENIUM_LIBERE.setData(true);
                robot.increaseScore(10);
                firstDone = true;
                firstOfThisSide = true;
            }

            //On reset les bras aux positions ascenseur
            robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_ASCENSEUR, false);
            robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_ASCENSEUR, true);

            //On tourne pour changer de bras
            robot.turn(0);

            //On dépose tous les palets droits
            firstOfThisSide = false;
            while(robot.getNbPaletsDroits() > 0) {
                actionBras(true, firstOfThisSide, firstDone);
                robot.increaseScore(10);
                firstDone = true;
                firstOfThisSide = true;
            }

            robot.useActuator(ActuatorsOrder.DESACTIVE_LA_POMPE_DROITE, false);
            robot.useActuator(ActuatorsOrder.DESACTIVE_LA_POMPE_GAUCHE, false);

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
        robot.useActuator(ActuatorsOrder.DESACTIVE_ELECTROVANNE_DROITE, false);
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

