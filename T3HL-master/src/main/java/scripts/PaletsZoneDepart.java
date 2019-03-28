package scripts;

import data.Table;
import locomotion.UnableToMoveException;
import orders.order.ActuatorsOrder;
import pfg.config.Config;
import robot.Master;
import utils.math.Circle;
import utils.math.Shape;
import utils.math.Vec2;
import utils.math.VectCartesian;

public class PaletsZoneDepart extends Script {

    private static final int DISTANCE_INTERPALET = 300;
    private int xEntry = 1350;
    private int yEntry = 450;
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
        try {
            System.out.println("HELLO");
            robot.turn(Math.PI / 2);
            robot.useActuator(ActuatorsOrder.ACTIVE_LA_POMPE_GAUCHE, true); // on attent que le vide se fasse
            robot.useActuator(ActuatorsOrder.DESACTIVE_ELECTROVANNE_GAUCHE, true);
            for (Vec2 position : positions) {
                if (premierPaletPris) {
                    //robot.moveLengthwise(DISTANCE_INTERPALET, false);
                    robot.followPathTo(position);
                } else {
                    premierPaletPris = true;
                }
                robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_SOL);
                robot.useActuator(ActuatorsOrder.DESACTIVE_ELECTROVANNE_GAUCHE, true);
                robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_ASCENSEUR);
                robot.useActuator(ActuatorsOrder.ACTIVE_ELECTROVANNE_GAUCHE, true); // on attend que le vide se casse
                robot.useActuator(ActuatorsOrder.DESCEND_ASCENSEUR_GAUCHE_DE_UN_PALET);
                // FIXME ((Master) robot).pushPaletGauche();
            }
            robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_INTERMEDIAIRE);
            // ""recalage""
            robot.useActuator(ActuatorsOrder.ACTIVE_ELECTROVANNE_GAUCHE, true);  // on attend que le vide se cassen
            robot.waitForLeftElevator();
            robot.useActuator(ActuatorsOrder.DESCEND_ASCENSEUR_GAUCHE_DE_UN_PALET);
            robot.waitForLeftElevator();
            robot.useActuator(ActuatorsOrder.MONTE_ASCENCEUR_GAUCHE_DE_UN_PALET);
            robot.waitForLeftElevator();
            robot.useActuator(ActuatorsOrder.DESCEND_ASCENSEUR_GAUCHE_DE_UN_PALET);

            // TODO: juste pour le testo
            robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_DISTRIBUTEUR);
            robot.useActuator(ActuatorsOrder.DESACTIVE_LA_POMPE_GAUCHE, true);

            table.removeFixedObstacleNotReInit(table.getPaletRougeDroite());
            table.removeFixedObstacleNotReInit(table.getPaletVertDroite());
            table.removeFixedObstacleNotReInit(table.getPaletBleuDroite());

            table.updateTableAfterFixedObstaclesChanges();
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
    }

    @Override
    public void updateConfig(Config config) {

    }
}
