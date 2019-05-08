package scripts;

import data.CouleurPalet;
import data.Table;
import locomotion.UnableToMoveException;
import orders.order.ActuatorsOrder;
import pfg.config.Config;
import robot.Master;
import utils.ConfigData;
import utils.math.Circle;
import utils.math.Shape;
import utils.math.Vec2;
import utils.math.VectCartesian;

import java.util.concurrent.TimeUnit;

public class PaletsZoneDepart extends Script {

    private static final int DISTANCE_INTERPALET = 300;
    private int xEntry = 1500-191-65+20;//1350;
    private int yEntry = 455;
    private Vec2[] positions = new VectCartesian[]{
            new VectCartesian(xEntry, yEntry),
            new VectCartesian(xEntry,yEntry+300),
            new VectCartesian(xEntry,yEntry+600)
    };

    public PaletsZoneDepart(Master robot, Table table) {
        super(robot, table);
    }

    @Override
    public void execute(Integer version) {
        boolean premierPaletPris = false;
        int i =0;
        try {
            robot.turn(Math.PI / 2);
            robot.useActuator(ActuatorsOrder.ACTIVE_LA_POMPE_GAUCHE, true); // on attent que le vide se fasse
           // robot.useActuator(ActuatorsOrder.DESACTIVE_ELECTROVANNE_GAUCHE, true);
            for (Vec2 position : positions) {
                if (premierPaletPris) {
                    robot.moveLengthwise(DISTANCE_INTERPALET, false);
//                    robot.followPathTo(position);
                } else {
                    premierPaletPris = true;
                    //robot.followPathTo(position);
                }
                //robot.turn(Math.PI / 2);
                robot.useActuator(ActuatorsOrder.DESACTIVE_ELECTROVANNE_GAUCHE, true);

                robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_SOL,true);
                robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_ASCENSEUR,true);
                robot.useActuator(ActuatorsOrder.ACTIVE_ELECTROVANNE_GAUCHE, true); // on attend que le vide se casse
                robot.useActuator(ActuatorsOrder.DESCEND_ASCENSEUR_GAUCHE_DE_UN_PALET);
                // FIXME: corriger couleur
                //il vaut mieux enlever les obstacles en même temps que attendre d'enlever les 3 nn ?
                switch (i) {
                    case 0:
                        robot.pushPaletGauche(CouleurPalet.BLEU);
                    table.removeFixedObstacleNoReInit(table.getPaletBleuDroite());
                    i++;
                    break;
                    case 1:
                        robot.pushPaletGauche(CouleurPalet.ROUGE);
                    table.removeFixedObstacleNoReInit(table.getPaletRougeDroite());
                    i++;
                    break;
                    case 2:
                        robot.pushPaletGauche(CouleurPalet.ROUGE);
                    table.removeFixedObstacleNoReInit(table.getPaletRougeDroite());
                    i++;
                    break;
                }
            }
            robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_INTERMEDIAIRE);
            // ""recalage""
            robot.useActuator(ActuatorsOrder.ACTIVE_ELECTROVANNE_GAUCHE, true);  // on attend que le vide se casse

            // FIXME A faire en roulant
            /*
            robot.waitForLeftElevator();
            robot.useActuator(ActuatorsOrder.DESCEND_ASCENSEUR_GAUCHE_DE_UN_PALET);
            robot.waitForLeftElevator();
            robot.useActuator(ActuatorsOrder.MONTE_ASCENCEUR_GAUCHE_DE_UN_PALET);
            robot.waitForLeftElevator();
            robot.useActuator(ActuatorsOrder.DESCEND_ASCENSEUR_GAUCHE_DE_UN_PALET);
*/

            // la symétrie de la table permet de corriger le droit en gauche (bug ou feature?)




           // robot.useActuator(ActuatorsOrder.MONTE_ASCENCEUR_GAUCHE_DE_UN_PALET);
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
        table.updateTableAfterFixedObstaclesChanges();
    }

    @Override
    public void updateConfig(Config config) {
        super.updateConfig(config);
    }
}
