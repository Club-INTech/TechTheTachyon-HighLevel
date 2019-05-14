package scripts;

import data.GameState;
import data.Sick;
import data.Table;
import data.XYO;
import locomotion.UnableToMoveException;
import orders.Speed;
import orders.order.ActuatorsOrder;
import pfg.config.Config;
import robot.Master;
import utils.ConfigData;
import utils.Log;
import utils.math.Circle;
import utils.math.Shape;
import utils.math.Vec2;
import utils.math.VectCartesian;


public class Accelerateur extends Script {
    private final VectCartesian positionDepart;
    /**
     * Position d'entrée du script
     */

    private final int xEntry = -210-27+30+90;
    private final int yEntry = 340+10;

    /**
     * Boolean de symétrie
     */
    private boolean symetry = false;

    /**
     * Distance d'aller retour entre le moment où l'on dépose un palet et le moment où l'en le libère
     */
    private final int distanceAllerRetour = 150;


    /**
     * Offset avec la planche
     */
    private final int offsetRecalage = 31;

    /*
     * Offset pour corriger la mesure des sicks (différence réel - mesuré)
     */
    private final int offsetSick= 6;

    /**
     * Différence en Y et X entre le sick et le centre du robot
     */
    private final int ySickToRobotCenter=113;

    private final int xSickToRobotCenter=101;

    /**
     * Distance entre les sicks et rapport entre dsick et écart de valeures mesurées pour faire un recalage en rotation
     */

    private final int dsick = 173;

    double rapport ;
    double ecart_mesures_sicks;
    double teta;

    public Accelerateur(Master robot, Table table) {
        super(robot, table);
        positionDepart = new VectCartesian(xEntry, yEntry);
    }

    private void actionBras(boolean coteDroit, boolean firstOfThisSide) {
        try {
            if (coteDroit) {
                robot.useActuator(ActuatorsOrder.DESACTIVE_ELECTROVANNE_DROITE, false);
                if (!firstOfThisSide) {
                    robot.useActuator(ActuatorsOrder.MONTE_ASCENCEUR_DROIT_DE_UN_PALET, false);
                }
                robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_ASCENSEUR, true);
                robot.useActuator(ActuatorsOrder.DESACTIVE_ELECTROVANNE_DROITE, true);
                robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_ACCELERATEUR, true);
                robot.useActuator(ActuatorsOrder.POUSSE_LE_PALET_BRAS_DROIT, true);
                robot.moveLengthwise(-distanceAllerRetour, false);
                robot.useActuator(ActuatorsOrder.ACTIVE_ELECTROVANNE_DROITE, true);
                robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_RECULE, true);
                robot.moveLengthwise(distanceAllerRetour, false);
                robot.popPaletDroit();
            } else {
                //On prépare la pompe pour aspirer mieux
                robot.useActuator(ActuatorsOrder.DESACTIVE_ELECTROVANNE_GAUCHE, false);

                if (!firstOfThisSide) {
                    //On monte l'ascenseur gauche quand nécessaire
                    robot.useActuator(ActuatorsOrder.MONTE_ASCENCEUR_GAUCHE_DE_UN_PALET, false);
                }
                //On envoie le bras à la position ascenseur
                robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_ASCENSEUR, true);

                //On attend un peu pour bien aspirer
                robot.useActuator(ActuatorsOrder.DESACTIVE_ELECTROVANNE_GAUCHE, true);

                //On place le palet sur la rampe
                robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_ACCELERATEUR, true);

                //On pousse le palet contre la rampe
                robot.useActuator(ActuatorsOrder.POUSSE_LE_PALET_BRAS_GAUCHE, true);

                //On se déplace en ayant le palet sur la rampe
                robot.moveLengthwise(distanceAllerRetour, false);

                //On casse le vide
                robot.useActuator(ActuatorsOrder.ACTIVE_ELECTROVANNE_GAUCHE, true);

                //On recule le bras
                robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_RECULE, true);

                //On recule
                robot.moveLengthwise(-distanceAllerRetour, false);

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

            //On regarde notre distance par rapport au mur
            robot.computeNewPositionAndOrientation(Sick.NOTHING);

            int averageDistanceY;
            int averageDistanceX;

            Vec2 currentPosition = XYO.getRobotInstance().getPosition();

           /* if (this.symetry) {
                averageDistanceY = (Sick.SICK_ARRIERE_DROIT.getLastMeasure() + Sick.SICK_AVANT_DROIT.getLastMeasure()) / 2 + offsetRecalage + this.offsetSick + this.ySickToRobotCenter;
                Log.POSITION.critical("symetrie" + Sick.SICK_ARRIERE_DROIT.getLastMeasure() + " " + Sick.SICK_AVANT_DROIT.getLastMeasure() + " " + averageDistanceY);

            }
            else{
                ecart_mesures_sicks=Sick.SICK_AVANT_DROIT.getLastMeasure() - Sick.SICK_ARRIERE_DROIT.getLastMeasure();
                rapport = ecart_mesures_sicks / dsick;
                teta = Math.atan(rapport);
                averageDistanceY = (int) (Math.cos(teta)*(Sick.SICK_AVANT_GAUCHE.getLastMeasure() + Sick.SICK_ARRIERE_GAUCHE.getLastMeasure()) / 2 + offsetRecalage + this.offsetSick + this.ySickToRobotCenter);
                averageDistanceX= (int) (Math.cos(teta)*Sick.SICK_AVANT.getLastMeasure()+ this.offsetSick + this.xSickToRobotCenter);
                if (averageDistanceX < (currentPosition.getX()-72)){
                    averageDistanceX= (int) (currentPosition.getX()*Math.cos(teta));
                }
                Log.POSITION.critical("no symetrie" + Sick.SICK_AVANT_GAUCHE.getLastMeasure() + " " + Sick.SICK_ARRIERE_GAUCHE.getLastMeasure() + " " + averageDistanceY);
                robot.setPositionAndOrientation(new VectCartesian(averageDistanceX, averageDistanceY),teta+Math.PI);

            }*/

            robot.gotoPoint(new VectCartesian(this.xEntry,this.yEntry));

            robot.turn(Math.PI);

            //On dépose tous les palets gauche en priorité
            boolean firstOfThisSide = true;
            while (robot.getNbPaletsGauches() > 0) {
                robot.turn(Math.PI);
                actionBras(false, firstOfThisSide);
                GameState.GOLDENIUM_LIBERE.setData(true);
                robot.increaseScore(10);
                firstOfThisSide = false;
            }

            //On reset les bras aux positions ascenseur
            robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_ASCENSEUR, false);
            robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_ASCENSEUR, true);

            //On tourne pour changer de bras
            robot.turn(0);

            //On dépose tous les palets droits
            firstOfThisSide = true;
            while(robot.getNbPaletsDroits() > 0) {
                robot.turn(0);
                actionBras(true, firstOfThisSide);
                robot.increaseScore(10);
                firstOfThisSide = false;
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
    public Vec2 entryPosition(Integer version) {
        return new VectCartesian(xEntry, yEntry);
    }

    @Override
    public void finalize(Exception e) {
        robot.setSpeed(Speed.DEFAULT_SPEED);
    }

    @Override
    public void updateConfig(Config config) {
        super.updateConfig(config);
        this.symetry = config.getString(ConfigData.COULEUR).equals("violet");
    }

}

