package scripts;



import data.Table;
import orders.order.ActuatorsOrder;
import pfg.config.Config;
import robot.Robot;
import utils.math.Shape;
import utils.math.Vec2;
import utils.math.VectCartesian;
import locomotion.UnableToMoveException;

import java.util.ArrayList;


public class PaletsX6 extends Script {

    private Vec2[] positions = new VectCartesian[]{
            new VectCartesian(100, 100),
            new VectCartesian(200, 100),
            new VectCartesian(100, 100),
            new VectCartesian(100, 100),
            new VectCartesian(100, 100),
            new VectCartesian(100, 100)
    };

    public PaletsX6(Robot robot, Table table) {
        super(robot, table);
        /* on va faire plusieurs versions selon la combinaison de palets que l'on veut prendre et dans quel ordre
         *  (selon le côté de la table que l'on choisit ?)
         *  (selon si on est plus proche d'une extrémité ?)
         *  (selon si on s'est fait voler les palets ? (dans lequel pas on shift de côté))
         * */
        versions = new ArrayList<Integer>();
        versions.add(0);
        versions.add(1);
        versions.add(2);
    }
    @Override
    public void execute(Integer version) {
        if (version == 0) {
            boolean premierPaletPris = false;
            try {
                for (Vec2 position : positions) {
                    if (premierPaletPris){
                        robot.moveToPoint(new VectCartesian(100, 100));
                    }
                    else{
                        premierPaletPris=true;
                    }
                    robot.useActuator(ActuatorsOrder.DESCEND_ASCENSEUR_DROIT_DE_UN_PALET);
                    robot.useActuator(ActuatorsOrder.ACTIVE_LA_POMPE_DROITE);
                    robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_DISTRIBUTEUR);
                    robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_ASCENSEUR);
                    robot.useActuator(ActuatorsOrder.DESACTIVE_LA_POMPE_DROITE);
                    robot.useActuator(ActuatorsOrder.DESACTIVE_ELECTROVANNE_DROITE);
                }
            } catch (UnableToMoveException e) {

            }



            /* dans cette version on prend tous les pallets du grand distributeur */
            int Paletsrestants = 5;
            while (Paletsrestants >= 1) {
                Paletsrestants = Paletsrestants - 1;
                /* on a créé une variable palets restants pour ne pas avoir a répéter les actions**/
                try {
                    robot.useActuator(ActuatorsOrder.DESCEND_ASCENSEUR_DROIT_DE_UN_PALET);
                    robot.useActuator(ActuatorsOrder.ACTIVE_LA_POMPE_DROITE);
                    robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_DISTRIBUTEUR);
                    robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_ASCENSEUR);
                    robot.useActuator(ActuatorsOrder.DESACTIVE_LA_POMPE_DROITE);
                    robot.useActuator(ActuatorsOrder.DESACTIVE_ELECTROVANNE_DROITE);
                    robot.moveToPoint(new VectCartesian(100,100));


                } catch (UnableToMoveException e) {

                }
            }
        } else if (version == 1){

            try {
                robot.useActuator(ActuatorsOrder.DESCEND_ASCENSEUR_DROIT_DE_UN_PALET);
                robot.useActuator(ActuatorsOrder.ACTIVE_LA_POMPE_GAUCHE);
                robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_DISTRIBUTEUR);
                robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_ASCENSEUR);
                robot.useActuator(ActuatorsOrder.DESACTIVE_LA_POMPE_DROITE);
                robot.useActuator(ActuatorsOrder.DESACTIVE_ELECTROVANNE_GAUCHE);
                robot.moveToPoint(new VectCartesian(100,100));

            } catch (UnableToMoveException e){

            }
        }
        else if (version == 2){
            try {
                robot.useActuator(ActuatorsOrder.DESCEND_ASCENSEUR_GAUCHE_DE_UN_PALET);
                robot.useActuator(ActuatorsOrder.ACTIVE_LA_POMPE_DROITE);
                robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_DISTRIBUTEUR);
                robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_ACCELERATEUR);
                robot.useActuator(ActuatorsOrder.DESACTIVE_LA_POMPE_DROITE);
                robot.useActuator(ActuatorsOrder.DESACTIVE_ELECTROVANNE_DROITE);
                robot.moveToPoint(new VectCartesian(100,100));


            }catch (UnableToMoveException e){

            }
        }
        else{

        }

    }
    @Override
    public Shape entryPosition(Integer version) {
        return null;
    }

    @Override
    public void finalize(Exception e) {
        robot.useActuator(ActuatorsOrder.DESCEND_ASCENSEUR_GAUCHE_DE_UN_PALET);

    }


    @Override
    public void updateConfig(Config config) {

    }
}