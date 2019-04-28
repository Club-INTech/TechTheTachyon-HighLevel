package scripts;
import data.CouleurPalet;
import data.Sick;
import data.Table;
import orders.order.ActuatorsOrder;
import pfg.config.Config;
import robot.Master;
import utils.math.Circle;
import utils.math.Shape;
import utils.math.Vec2;
import utils.math.VectCartesian;
import locomotion.UnableToMoveException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class PaletsX6 extends Script {
    private ArrayList<VectCartesian> positions;
    public PaletsX6(Master robot, Table table) {
        super(robot, table);
        /* on va faire plusieurs versions selon la combinaison de palets que l'on veut prendre et dans quel ordre
         *  (selon le côté de la table que l'on choisit ?)
         *  (selon si on est plus proche d'une extrémité ?)
         *  (selon si on s'est fait voler les palets ? (dans lequel pas on shift de côté))
         * */
        /*met en place les versions différentes*/
        versions = new ArrayList<>();
        versions.add(0);
        versions.add(1);
        versions.add(2);
        versions.add(3);
        versions.add(4);
        versions.add(5);
        /*position des 6 palets ( position dans le tableau positionS ) */
        this.positions = new ArrayList<>();
    }
    @Override
    public void execute(Integer version) {
            /*donne le côté duquel on commence à prendre les palets selon la position au début du script du robot.
     Autrement dit on divise la demi table en deux et selon cela on choisit de commencer à droite ou à gauche du distributeur
     */
        //difference de ~100  entre chaque palet
        if (version == 0) { //rouge droite
            positions.add(new VectCartesian(1000, 1206));
            positions.add(new VectCartesian(805 , 1206));
            positions.add(new VectCartesian(597, 1206));
        } else if (version == 1) {  //vert droite
            positions.add(new VectCartesian(505, 1206));
            positions.add(new VectCartesian(905, 1206));
            //positions.add(new VectCartesian(0, 800));
        } else if (version == 2) {  //bleu droite
            positions.add(new VectCartesian(834, 1206));
            //positions.add(new VectCartesian(0, 800));
            //positions.add(new VectCartesian(0, 800));
        } else if (version == 3) {  //rouge gauche On doit symetriser la partie droite pas la peine d'écrire des cordonnées
            positions.add(new VectCartesian(0, 800));
            positions.add(new VectCartesian(0, 800));
            positions.add(new VectCartesian(0, 800));
        } else if (version == 4) {  //vert gauche
            positions.add(new VectCartesian(0, 800));
            positions.add(new VectCartesian(0, 800));
            positions.add(new VectCartesian(0, 800));
        } else if (version == 5) {  //bleu gauche
            positions.add(new VectCartesian(0, 800));
            positions.add(new VectCartesian(0, 800));
            positions.add(new VectCartesian(0, 800));
        }
        try {
            robot.turn(Math.PI);
            try {
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            robot.computeNewPositionAndOrientation(Sick.UPPER_RIGHT_CORNER_TOWARDS_PI);
            for (Vec2 position : positions) {
                /*petit booléen qui permet de ne pas bouger au début de la première action comme on est dans l'entry position*/
                //
                robot.followPathTo(position);
                robot.turn(Math.PI);

                // on suposse que l'ascenseur est monté au mx au début

                robot.useActuator(ActuatorsOrder.ACTIVE_LA_POMPE_DROITE);
                robot.useActuator(ActuatorsOrder.DESACTIVE_ELECTROVANNE_DROITE, true);
                robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_DISTRIBUTEUR);
                robot.useActuator(ActuatorsOrder.REMONTE_LE_BRAS_DROIT_DU_DISTRIBUTEUR_VERS_ASCENSEUR);
                //robot.useActuator(ActuatorsOrder.DESACTIVE_LA_POMPE_GAUCHE);
                robot.useActuator(ActuatorsOrder.ACTIVE_ELECTROVANNE_DROITE,true);
                robot.useActuator(ActuatorsOrder.DESCEND_ASCENSEUR_DROIT_DE_UN_PALET);
                if(version == 0) {
                    // TODO
                    robot.pushPaletDroit(CouleurPalet.ROUGE);
                } else if(version == 1) {
                    // TODO
                    robot.pushPaletDroit(CouleurPalet.VERT);
                }
            }
        } catch (UnableToMoveException e) {
            e.printStackTrace();
            // TODO
        }
    }
    @Override
    public Shape entryPosition(Integer version) {
        if (version == 0) {
            Shape positionEntree = new Circle(new VectCartesian(1500-280,1206), 5);
            return positionEntree;
        }
        else if (version == 1) {
            Shape positionEntree = new Circle(new VectCartesian(1500-280,1206), 5);
            return positionEntree;
        }
        else if (version == 2) {
            Shape positionEntree = new Circle(new VectCartesian(0,800), 5);
            return positionEntree;
        }
        else if (version == 3) {
            Shape positionEntree = new Circle(new VectCartesian(0,800), 5);
            return positionEntree;
        }
        else if (version == 4) {
            Shape positionEntree = new Circle(new VectCartesian(0,800), 5);
            return positionEntree;
        }
        else if (version == 5) {
            Shape positionEntree = new Circle(new VectCartesian(0,800), 5);
            return positionEntree;
        }

        return null;
    }
    @Override
    public void finalize(Exception e) {
        robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_ASCENSEUR);
        robot.useActuator(ActuatorsOrder.DESACTIVE_LA_POMPE_DROITE);
        robot.useActuator(ActuatorsOrder.DESACTIVE_ELECTROVANNE_DROITE);

        // TODO: push palet
    }
    @Override
    public void updateConfig(Config config) {
        super.updateConfig(config);
    }
}