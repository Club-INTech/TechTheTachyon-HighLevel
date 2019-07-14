package scripts;

import data.CouleurPalet;
import data.SensorState;
import data.Sick;
import data.XYO;
import data.synchronization.SynchronizationWithBuddy;
import locomotion.UnableToMoveException;
import orders.order.ActuatorsOrder;
import pfg.config.Configurable;
import robot.Robot;
import utils.Container;
import utils.Log;
import utils.Offsets;
import utils.TimeoutError;
import utils.container.ContainerException;
import utils.container.Module;
import utils.math.Calculs;
import utils.math.Vec2;
import utils.math.VectCartesian;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

public class PaletsX6 extends Script implements Offsets {
    private ArrayList<VectCartesian> positions;

    @Configurable
    private boolean symetry;
    boolean premierPaletPris;
    CompletableFuture<Void> puckStored = null;
    CompletableFuture<Void> elevatorAtRightPlace = null;

    private static final int DISTANCE_INTER_PUCK = 100;
    private static double offsetY ;
    private static double offsetX ;
    private static double offsetTheta;
    private SynchronizationWithBuddy syncBuddy;
    @Configurable
    private long balanceWaitTime;

    /**
     * Est-ce qu'on a un recalage sur x6?
     */
    @Configurable
    private boolean recalageAcc;

    public PaletsX6(Container container, SynchronizationWithBuddy syncBuddy) {
        super(container);
        this.syncBuddy = syncBuddy;
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
        /*position des 6 palets ( position dans le tableau positionS ) */
        this.positions = new ArrayList<>();
    }

    @Override
    public void execute(int version) {
        /*  Donne le côté duquel on commence à prendre les palets selon la position au début du script du robot.
            Autrement dit on divise la demi table en deux et selon cela on choisit de commencer à droite ou à gauche du distributeur
         */

        double offsetZddX = Offsets.get(ZDD_POST_BALANCE_X_JAUNE);
        double offsetZddY = Offsets.get(ZDD_POST_BALANCE_Y_JAUNE);
        if(symetry) {
            offsetZddX = Offsets.get(ZDD_POST_BALANCE_X_VIOLET);
            offsetZddY = Offsets.get(ZDD_POST_BALANCE_Y_VIOLET);
        }
        Vec2 positionZoneDepart = new VectCartesian(1500-250+offsetZddX, 500+offsetZddY);

        int i=0;
        loadOffsets();
        double offsetBalance = Offsets.get(PALETS_X6_BALANCE_Y_JAUNE);
        if(symetry) {
            offsetBalance = Offsets.get(PALETS_X6_BALANCE_Y_VIOLET);
        }
        Vec2 positionBalance = new VectCartesian(200,1204+10+5+offsetY+20+offsetBalance);

        //Position pour le côté droit
        //difference de ~100  entre chaque palet
        if (version == 0) { //rouge droite
            // version pour juste les rouges
            positions.add(new VectCartesian(905, 1206));
            positions.add(new VectCartesian(805 , 1206));
            positions.add(new VectCartesian(597, 1206));
        } else if (version == 1) {  // version pour juste les verts
            positions.add(new VectCartesian(905, 1206));
            positions.add(new VectCartesian(505, 1206));
        } else if (version == 2) {  //// version pour juste le bleu
            positions.add(new VectCartesian(834, 1206));
        }//version pour prendre les palets à la suite sauf le bleu
        else if (version == 3 || version == 4) {
            positions.add(new VectCartesian(1000 + offsetX, 1204 + 10 + 5 + offsetY)); // rouge (0)
            positions.add(new VectCartesian(900 + offsetX, 1204 + 10 + 5 + offsetY)); // vert (1)
            positions.add(new VectCartesian(800 + offsetX, 1204 + 10 + 5 + offsetY)); // rouge (2)
            positions.add(new VectCartesian(700 + offsetX, 1204 + 10 + 5 + offsetY)); // bleu (3)
            positions.add(new VectCartesian(600 + offsetX, 1204 + 10 + 5 + offsetY)); // rouge (4)
            positions.add(new VectCartesian(500 + offsetX, 1204 + 10 + 5 + offsetY)); // vert (5)
        }
        robot.useActuator(ActuatorsOrder.ACTIVE_LA_POMPE_DROITE);
        premierPaletPris = false;
        try {
            robot.turn(Math.PI);
            if(symetry) {
                offsetTheta = Offsets.get(PALETSX6_THETA_VIOLET);
                robot.computeNewPositionAndOrientation(Sick.UPPER_LEFT_CORNER_TOWARDS_0);
                // remplacement de la position dans le HL
                XYO.getRobotInstance().update(XYO.getRobotInstance().getPosition().getX(), XYO.getRobotInstance().getPosition().getY(), XYO.getRobotInstance().getOrientation()+ offsetTheta);

                Log.LOCOMOTION.debug("New position with SICKs: "+XYO.getRobotInstance().getPosition());
                // remplacement de la position dans le LL
                robot.setPositionAndOrientation(XYO.getRobotInstance().getPosition(), XYO.getRobotInstance().getOrientation()+ offsetTheta);
            } else {
                robot.computeNewPositionAndOrientation(Sick.UPPER_RIGHT_CORNER_TOWARDS_PI);
            }
            robot.followPathTo(positions.get(0));
            turn(Math.PI);

            if(version == 4) {
                //On prend le 1er palet
                actuators.rightValve.desactivate();
                robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_DISTRIBUTEUR_SANS_REESSAI, true);
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                grabPuckGoto(robot, positions.get(1), false, true, false);
                robot.pushPaletDroit(CouleurPalet.ROUGE);

                //On prend le 2è palet
                grabPuckGoto(robot, positions.get(2), false, true, false);
                robot.pushPaletDroit(CouleurPalet.VERT);

                //On prend le 4è palet
                grabPuckGoto(robot, positions.get(4), false, true, false); // skip le palet bleu
                robot.pushPaletDroit(CouleurPalet.ROUGE);

                //On prend le 5è palet
                grabPuckGoto(robot, positions.get(5), false, true, false);
                robot.pushPaletDroit(CouleurPalet.ROUGE);

                //On prend le 6ème palet

                grabPuck(robot, -DISTANCE_INTER_PUCK * 2, false, false, false); // retourne devant le bleu
                robot.pushPaletDroit(CouleurPalet.VERT);

                //On prend le 4ème palet (bleu)
                grabPuck(robot, 0, true, false, false);

                long balanceStart = System.currentTimeMillis();
                // On va à la balance
                try {
                    try {
                        SensorState.DISABLE_ENNEMIES_OTHER_SIDE.setData(true);
                    } catch (TimeoutError error) {
                        error.printStackTrace();
                    }
                    table.removeAllTemporaryObstacles();
                    robot.followPathTo(positionBalance);

                    // On dépose le bleu
                    // on tourne en même temps qu'on lève le bras
                    CompletableFuture<Void> armInPlace = async("Dépose bleu dans la balance", () -> {
                        robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_BALANCE, true);
                    });
                    turn(Calculs.modulo(Math.PI+Math.PI/16, Math.PI));
                    robot.increaseScore(12);
                    armInPlace.join(); // on attend que le bras soit à la bonne position
                    actuators.rightValve.activate(true);

                    try {
                        robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_DEPOT, true);
                        syncBuddy.sendBalanceFree();

                      //  robot.turn(0);
                      //  robot.moveLengthwise(600,false);
                        // on va dans notre zone de départ pour libérer le chemin
                        try {
                            robot.followPathTo(positionZoneDepart);
                            robot.turn(Math.PI);
                            if(!recalageAcc) {
                                recalageX6();
                            }
                            robot.turnToPoint(container.module(Accelerateur.class).entryPosition(Match.ACC_VERSION));
                        } catch (ContainerException e) {
                            e.printStackTrace();
                        }
                    } catch (UnableToMoveException e) {
                        e.printStackTrace();
                    }

                    try {
                        Module.withTimeout(balanceWaitTime, () -> syncBuddy.waitForFreeAccelerator());
                    } catch (TimeoutError error) {
                        error.printStackTrace();
                    }
                } finally {
                    SensorState.DISABLE_ENNEMIES_OTHER_SIDE.setData(false);
                }
                long balanceEnd = System.currentTimeMillis();
                long elapsed = balanceEnd-balanceStart;
                Log.STRATEGY.warning("Balance took "+ formatTime(elapsed));
                //robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_ASCENSEUR, false);
                // fin du script
            }
        } catch (UnableToMoveException e) {
            e.printStackTrace();
            // TODO
        }
    }

    /**
     * Actions à faire pour une itération de prise de palet
     * @param robot le robot
     * @param moveDistance la distance au prochain palet
     */
    private void grabPuck(Robot robot, int moveDistance, boolean blue, boolean moveElevator, boolean lastInChain) throws UnableToMoveException {
        Log.STRATEGY.critical("Entrée dans grabPuck");
        if(puckStored != null) {
            Log.STRATEGY.warning("Attente de puckStored");
            puckStored.join();
            Log.STRATEGY.warning("Fin d'attente de puckStored");
        }
        //robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_DISTRIBUTEUR_SANS_REESSAI, true);
        actuators.rightValve.desactivate(true);

        try {
            turn(Math.PI); // réoriente le robot vers PI
            storePuck(blue, moveElevator, lastInChain);

            if (moveDistance != 0) {
                robot.moveLengthwise(moveDistance, false);
            }
        } catch (UnableToMoveException e) {
            e.printStackTrace();
        }

        Log.STRATEGY.critical("Sortie de grabPuck");
    }

    private void storePuck(boolean blue, boolean moveElevator, boolean lastInChain) {
        puckStored = async("Dépôt", () -> {
            if(blue) {
                robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_TIENT_BLEU, true);
            } else {
                robot.useActuator(ActuatorsOrder.REMONTE_LE_BRAS_DROIT_DU_DISTRIBUTEUR_VERS_ASCENSEUR, true);
                actuators.rightValve.activate(true);
                if(moveElevator) {
                    actuators.rightElevator.downup();
                }
                actuators.rightValve.desactivate();
                robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_DISTRIBUTEUR_SANS_REESSAI, true);
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(lastInChain) {
                    robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_DERNIER_PALET, true);
                }
            }
        });
    }

    /**
     * Actions à faire pour une itération de prise de palet
     * @param robot le robot
     */
    private void grabPuckGoto(Robot robot, Vec2 pos, boolean blue, boolean moveElevator, boolean lastInChain) throws UnableToMoveException {
        Log.STRATEGY.critical("Entrée dans grabPuckGoto");
        if(puckStored != null) {
            Log.STRATEGY.warning("Attente de puckStored");
            puckStored.join();
            Log.STRATEGY.warning("Fin d'attente de puckStored");
        }
        //robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_DISTRIBUTEUR_SANS_REESSAI, true);
        storePuck(blue, moveElevator, lastInChain);
        robot.gotoPoint(pos);
        Log.STRATEGY.critical("Sortie dans grabPuckGoto");
    }

    public void recalageX6(/*int yEntry*/) throws UnableToMoveException {
        if (symetry) {
            robot.computeNewPositionAndOrientation(Sick.LOWER_LEFT_CORNER_TOWARDS_0);
        } else {
            Log.TABLE.critical("Couleur pour le recalage : jaune");
            robot.computeNewPositionAndOrientation(Sick.LOWER_RIGHT_CORNER_TOWARDS_PI);
        }
    }

    @Override
    public Vec2 entryPosition(int version) {
        loadOffsets();
        //position de départ directement au niveau du palet
        if (version == 0) {
            //Shape positionEntree = new Circle(new VectCartesian(1500-280,1206), 5);
            return new VectCartesian(1000,1206);
        }
        else if (version == 1) {
            return new VectCartesian(905,1206);
        }
        else if (version == 2) {
            return new VectCartesian(834,1206);
        }
        else if (version == 3 || version == 4 || version == 5) {
            System.err.println("OFFSET Y: "+offsetY);
            return new VectCartesian(1500-191-65+offsetX, 1204+10+5+offsetY);
        }
        return null;
    }

    private void loadOffsets() {
        if(symetry) {
            offsetX = Offsets.get(PALETSX6_X_VIOLET);
            offsetY = Offsets.get(PALETSX6_Y_VIOLET);
        } else {
            offsetX = Offsets.get(PALETSX6_X_JAUNE);
            offsetY = Offsets.get(PALETSX6_Y_JAUNE);
        }
    }

    @Override
    public void executeWhileMovingToEntry(int version) {
        robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_DEPOT,false);
        robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_DEPOT,false);
        if(robot.getNbPaletsDroits()>=1 && robot.getNbPaletsDroits() < 4){ // si l'asc contient 4 palets, on peut plus descendre
            actuators.rightElevator.down(true);
        }
    }

    @Override
    public void finalize(Exception e) {
        // range le bras quand on a fini
        robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_DEPOT,false);
    }
}