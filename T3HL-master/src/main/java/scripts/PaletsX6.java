package scripts;
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
        versions = new ArrayList<Integer>();
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

        if (version == 0) { //rouge droite
            positions.add(new VectCartesian(-1000, 1225));
            positions.add(new VectCartesian(-850, 1225));
            positions.add(new VectCartesian(-450, 1225));
        } else if (version == 1) {  //vert droite
            positions.add(new VectCartesian(-900, 1225));
            positions.add(new VectCartesian(-500, 1225));
            //positions.add(new VectCartesian(0, 800));
        } else if (version == 2) {  //bleu droite
            positions.add(new VectCartesian(-834, 1225));
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
        boolean premierdeplacement = false;
        try {
            for (Vec2 position : positions) {
                /*petit booléen qui permet de ne pas bouger au début de la première action comme on est dans l'entry position*/
               //
                    robot.followPathTo(position);
                    if (premierdeplacement) {}
                    else {
                    premierdeplacement = true;
                    robot.turn(0 );}

                robot.useActuator(ActuatorsOrder.DESCEND_ASCENSEUR_GAUCHE_DE_UN_PALET);
                robot.useActuator(ActuatorsOrder.ACTIVE_LA_POMPE_GAUCHE);
                robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_DISTRIBUTEUR);
                robot.useActuator(ActuatorsOrder.REMONTE_LE_BRAS_GAUCHE_DU_DISTRIBUTEUR_VERS_ASCENSEUR);
                robot.useActuator(ActuatorsOrder.DESACTIVE_LA_POMPE_GAUCHE);
                robot.useActuator(ActuatorsOrder.DESACTIVE_ELECTROVANNE_GAUCHE);
            }
        } catch (UnableToMoveException e) {
            e.printStackTrace();
            // TODO
        }
    }
    @Override
    public Shape entryPosition(Integer version) {

        if (version == 0) {
            Shape positionEntree = new Circle(new VectCartesian(500,500), 5);
            return positionEntree;
        }
        else if (version == 1) {
            Shape positionEntree = new Circle(new VectCartesian(0,800), 5);
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
    }
}