package scripts;

import data.XYO;
import locomotion.UnableToMoveException;
import orders.order.ActuatorsOrder;
import pfg.config.Configurable;
import utils.HLInstance;
import utils.Offsets;
import utils.math.Vec2;
import utils.math.VectCartesian;

import static utils.Offsets.ZDD_X_JAUNE;
import static utils.Offsets.ZDD_X_VIOLET;

public class OpenTheGate extends Script {
    @Configurable
    private boolean symetry;

    /**
     * Construit un script
     *
     */
    protected OpenTheGate(HLInstance hl) {
        super(hl);
    }

    @Override
    public void execute(int version) {
        double offX;
        if (symetry){
            offX= Offsets.get(ZDD_X_VIOLET);
        } else {
            offX= Offsets.get(ZDD_X_JAUNE);
        }
        Vec2 zoneDep = new VectCartesian(1500-191-65+offX-20,1040-300);
        Vec2 lowerLeft = new VectCartesian(650, 300);
        Vec2 lowerRight = new VectCartesian(zoneDep.getX(), 300);
        Vec2 upperLeft = new VectCartesian(650, 1300);
        Vec2 upperRight = new VectCartesian(zoneDep.getX(), 1300);

        while (true) {
            try {
                robot.followPathTo(zoneDep, 0);
                turn(Math.PI/2);
                actuators.leftPump.activate(true);
                actuators.leftValve.deactivate(true);
                robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_SOL,true);
                actuators.leftValve.deactivate(true);
                robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_DEPOT,true);
                actuators.leftValve.activate(true);
                actuators.leftElevator.downup(true);
                robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_ASCENSEUR, true);
                actuators.leftValve.deactivate(true);
                robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_AU_DESSUS_PALET, true);
                actuators.leftValve.activate();
                actuators.leftValve.deactivate(true);
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
    public Vec2 entryPosition(int version) {
        return XYO.getRobotInstance().getPosition();
    }

    @Override
    public void finalize(Exception e) {

    }
}
