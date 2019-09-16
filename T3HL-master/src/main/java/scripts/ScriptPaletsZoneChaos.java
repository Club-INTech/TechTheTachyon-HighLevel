package scripts;

import data.CouleurPalet;
import data.GameState;
import data.PaletsZoneChaos;
import data.SensorState;
import locomotion.UnableToMoveException;
import orders.order.ActuatorsOrder;
import pfg.config.Config;
import pfg.config.Configurable;
import utils.ConfigData;
import utils.HLInstance;
import utils.container.Module;
import utils.math.Vec2;
import utils.math.InternalVectCartesian;

import java.util.Arrays;

// TODO


public class ScriptPaletsZoneChaos extends Script {


    private int xEntry = 900;
    private int yEntry = 1055;
    private Vec2[] positions = new InternalVectCartesian[4];

    @Configurable
    private boolean symetry;
    int robotRay = 190;
    int rayonPalet = 38;

    public ScriptPaletsZoneChaos(HLInstance hl) {
        super(hl);
    }

    @Override
    public void execute(int version) {

        while (!(boolean) (GameState.POSITIONS_CHAOS_RECUES.getData())) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (symetry) {

            positions[0] = new InternalVectCartesian(PaletsZoneChaos.RED_1_ZONE_CHAOS_PURPLE.getPosition().getX() + (robotRay + rayonPalet ), PaletsZoneChaos.RED_1_ZONE_CHAOS_PURPLE.getPosition().getY());
            positions[1] = new InternalVectCartesian(PaletsZoneChaos.RED_2_ZONE_CHAOS_PURPLE.getPosition().getX() + (robotRay + rayonPalet ), PaletsZoneChaos.RED_2_ZONE_CHAOS_PURPLE.getPosition().getY());
            positions[2] = new InternalVectCartesian(PaletsZoneChaos.GREEN_ZONE_CHAOS_PURPLE.getPosition().getX() + (robotRay + rayonPalet ), PaletsZoneChaos.GREEN_ZONE_CHAOS_PURPLE.getPosition().getY());
            positions[3] = new InternalVectCartesian(PaletsZoneChaos.BLUE_ZONE_CHAOS_PURPLE.getPosition().getX() + (robotRay + rayonPalet ), PaletsZoneChaos.GREEN_ZONE_CHAOS_PURPLE.getPosition().getY());
        } else {
            positions[0] = new InternalVectCartesian(PaletsZoneChaos.RED_1_ZONE_CHAOS_YELLOW.getPosition().getX() + (robotRay + rayonPalet), PaletsZoneChaos.RED_1_ZONE_CHAOS_YELLOW.getPosition().getY());
            positions[1] = new InternalVectCartesian(PaletsZoneChaos.RED_2_ZONE_CHAOS_YELLOW.getPosition().getX() + (robotRay + rayonPalet), PaletsZoneChaos.RED_2_ZONE_CHAOS_YELLOW.getPosition().getY());
            positions[2] = new InternalVectCartesian(PaletsZoneChaos.GREEN_ZONE_CHAOS_YELLOW.getPosition().getX() + (robotRay + rayonPalet), PaletsZoneChaos.GREEN_ZONE_CHAOS_YELLOW.getPosition().getY());
            positions[3] = new InternalVectCartesian(PaletsZoneChaos.BLUE_ZONE_CHAOS_YELLOW.getPosition().getX() + (robotRay + rayonPalet), PaletsZoneChaos.GREEN_ZONE_CHAOS_YELLOW.getPosition().getY());
        }
/**
 * On trie les palets selon l'axe X pour les prendre de droite à gauche
 */
        Arrays.sort(positions, (v1, v2) -> {
            if (v1.getX() < v2.getX()) {
                return 1;
            } else if (v1.getX() > v2.getX()) {
                return -1;
            } else {
                return Integer.compare(v1.getY(), v2.getY());
            }
        });


        try {
            //table.removeAllChaosObstacles();
            actuators.leftValve.activate();
            for (Vec2 position : positions) {
                followPathTo(position);
                turn(Math.PI / 2);
                // tant que l'ascenseur gauche bouge on continue pas, sinon on risque de pas avoir un ascenseur à une bonne position (en théorie ici c'est bon mais on sait jamais)
                Module.waitWhileTrue(SensorState.LEFT_ELEVATOR_MOVING::getData);
                actuators.leftElevator.down();
                robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_SOL, true);
                actuators.leftValve.deactivate(true);
                robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_DEPOT, true);
                actuators.leftValve.activate();

                SensorState.LEFT_ELEVATOR_MOVING.setData(true); // on dit que l'ascenseur gauche bouge pour pas le refaire bouger son mouvement dans la prochaine itération
                if (robot.getNbPaletsGauches() < 4) {
                    actuators.leftElevator.downup();
                } else {
                    actuators.leftElevator.updown();
                }


                // à défaut de savoir la couleur, au moins on cassera pas les ascenseurs
                robot.pushPaletGauche(CouleurPalet.ROUGE);
            }
            actuators.leftPump.deactivate();
        } catch (UnableToMoveException e) {
            e.printStackTrace();
        }

    }

    @Override
    public Vec2 entryPosition(int version) {
        return new InternalVectCartesian(xEntry, yEntry);
    }

    @Override
    public void finalize(Exception e) {
        table.removeAllChaosObstacles();
    }

    @Override
    public void updateConfig(Config config) {
        super.updateConfig(config);
        robotRay = config.get(ConfigData.ROBOT_RAY) + 1;
    }
}
