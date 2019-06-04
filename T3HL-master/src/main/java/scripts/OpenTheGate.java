package scripts;

import data.Table;
import data.XYO;
import locomotion.UnableToMoveException;
import orders.order.ActuatorsOrder;
import pfg.config.Config;
import robot.Master;
import utils.ConfigData;
import utils.math.Vec2;
import utils.math.VectCartesian;

import static utils.Offsets.ZDD_X_JAUNE;
import static utils.Offsets.ZDD_X_VIOLET;

public class OpenTheGate extends Script {
    private boolean symetry;

    /**
     * Construit un script
     *
     * @param robot le robot
     * @param table
     */
    protected OpenTheGate(Master robot, Table table) {
        super(robot, table);
    }

    @Override
    public void execute(Integer version) {
        double offX;
        if (symetry){
            offX= ZDD_X_VIOLET.get();
        } else {
            offX= ZDD_X_JAUNE.get();
        }
        Vec2 zoneDep = new VectCartesian(1500-191-65+offX-20,1040-300);
        Vec2 lowerLeft = new VectCartesian(650, 300);
        Vec2 lowerRight = new VectCartesian(zoneDep.getX(), 300);
        Vec2 upperLeft = new VectCartesian(650, 1300);
        Vec2 upperRight = new VectCartesian(zoneDep.getX(), 1300);

        while (true) {
            try {
                robot.followPathTo(zoneDep, 0);
                robot.turn(Math.PI/2);
                robot.useActuator(ActuatorsOrder.ACTIVE_LA_POMPE_GAUCHE, true); // on attent que le vide se fasse
                robot.useActuator(ActuatorsOrder.DESACTIVE_ELECTROVANNE_GAUCHE, true);
                robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_SOL,true);
                robot.useActuator(ActuatorsOrder.DESACTIVE_ELECTROVANNE_GAUCHE, true);
                robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_DEPOT,true);
                robot.useActuator(ActuatorsOrder.ACTIVE_ELECTROVANNE_GAUCHE, true);
                robot.useActuator(ActuatorsOrder.DESCEND_MONTE_ASCENCEUR_GAUCHE_DE_UN_PALET,false);
                robot.waitForLeftElevator();
                robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_ASCENSEUR, true);
                robot.useActuator(ActuatorsOrder.DESACTIVE_ELECTROVANNE_GAUCHE, true);
                robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_AU_DESSUS_PALET, true);
                robot.useActuator(ActuatorsOrder.ACTIVE_ELECTROVANNE_GAUCHE);
                robot.useActuator(ActuatorsOrder.DESACTIVE_LA_POMPE_GAUCHE, true); // on attent que le vide se fasse
                robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_DEPOT, true);
            } catch (UnableToMoveException e) {
                e.printStackTrace();
            }

            try {
                robot.followPathTo(upperRight, 0);
            } catch (UnableToMoveException e) {
                e.printStackTrace();
            }
            try {
                robot.followPathTo(upperLeft, 0);
            } catch (UnableToMoveException e) {
                e.printStackTrace();
            }
            try {
                robot.followPathTo(lowerLeft, 0);
            } catch (UnableToMoveException e) {
                e.printStackTrace();
            }
            try {
                robot.followPathTo(lowerRight, 0);
            } catch (UnableToMoveException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public Vec2 entryPosition(Integer version) {
        return XYO.getRobotInstance().getPosition();
    }

    @Override
    public void updateConfig(Config config) {
        super.updateConfig(config);
        symetry = config.getString(ConfigData.COULEUR).equals("violet");
    }

    @Override
    public void finalize(Exception e) {

    }
}
