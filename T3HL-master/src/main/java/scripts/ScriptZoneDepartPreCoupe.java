package scripts;

import data.CouleurPalet;
import data.Table;
import data.table.Obstacle;
import locomotion.UnableToMoveException;
import orders.order.ActuatorsOrder;
import pfg.config.Config;
import robot.Master;
import robot.Robot;
import utils.math.Circle;
import utils.math.Shape;
import utils.math.Vec2;
import utils.math.VectCartesian;

import java.util.ArrayList;
import java.util.List;

public class ScriptZoneDepartPreCoupe extends Script{


        private static final int DISTANCE_INTERPALET = 300;

        private int xEntry = 1500-191-65+20;//1350;
        private int yEntry = 430;
        private Vec2[] positions = new VectCartesian[]{
                new VectCartesian(xEntry, yEntry),
                new VectCartesian(xEntry,yEntry+300),
                new VectCartesian(xEntry,yEntry+600)
        };

        public ScriptZoneDepartPreCoupe(Master robot, Table table) {
            super(robot, table);
        }

        @Override
        public void execute(Integer version) {
            try {

                // la symétrie de la table permet de corriger le droit en gauche (bug ou feature?)
//                table.removeFixedObstacleNoReInit(table.getPaletBleuDroite());

                /*robot.turn(Math.PI/2);
                robot.moveLengthwise(DISTANCE_INTERPALET*2 + 240, false);
                robot.turn(Math.PI);
                robot.moveLengthwise(DISTANCE_INTERPALET*3, false);
                robot.turn(-Math.PI/2);
                robot.moveLengthwise(240,false);
                robot.turn(0);
                robot.moveLengthwise(DISTANCE_INTERPALET*2,false);
                robot.turn(Math.PI);
                robot.moveLengthwise(DISTANCE_INTERPALET*2,false);
                robot.turn(-Math.PI);
                robot.moveLengthwise(DISTANCE_INTERPALET, false);
                robot.turn(0);
                robot.moveLengthwise(DISTANCE_INTERPALET*2, false);
                robot.turn(Math.PI);
                robot.moveLengthwise(DISTANCE_INTERPALET*2, false);
                robot.turn(-Math.PI/2);
                robot.moveLengthwise(DISTANCE_INTERPALET, false);
                robot.turn(0);
                robot.moveLengthwise(DISTANCE_INTERPALET*2, false);
*/
                List<Vec2> devant = new ArrayList<>();
                List<Vec2> pos = new ArrayList<>();
                Vec2 devantDelta = new VectCartesian(-DISTANCE_INTERPALET*3, 0f);
                devant.add(table.getPaletBleuDroite().getPosition().plusVector(devantDelta));
                devant.add(table.getPaletVertDroite().getPosition().plusVector(devantDelta));
                devant.add(table.getPaletRougeDroite().getPosition().plusVector(devantDelta));

                pos.add(table.getPaletBleuDroite().getPosition());
                pos.add(table.getPaletVertDroite().getPosition());
                pos.add(table.getPaletRougeDroite().getPosition());
                for(int i = 0;i<3;i++) {
                    robot.followPathTo(pos.get(i));
                    robot.followPathTo(devant.get(i));
                    switch (i) {
                        case 0:
                            table.removeTemporaryObstacle(table.getPaletBleuDroite());
                            break;

                        case 1:
                            table.removeTemporaryObstacle(table.getPaletVertGauche());
                            break;

                        case 2:
                            table.removeTemporaryObstacle(table.getPaletVertGauche());
                            break;
                    }
                    robot.followPathTo(pos.get(i));
                }
                table.removeTemporaryObstacle(table.getPaletVertDroite());
                table.removeTemporaryObstacle(table.getPaletBleuDroite());
                table.removeTemporaryObstacle(table.getPaletRougeDroite());
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
            super.updateConfig(config);
        }


}
