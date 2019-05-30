package scripts;
import data.*;
import data.synchronization.SynchronizationWithBuddy;
import locomotion.UnableToMoveException;
import orders.order.ActuatorsOrder;
import pfg.config.Config;
import robot.Master;
import robot.Robot;
import utils.*;
import utils.container.ContainerException;
import utils.container.Service;
import utils.math.Calculs;
import utils.math.Vec2;
import utils.math.VectCartesian;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.concurrent.CompletableFuture;

public class X6alter extends Script {
    private ArrayList<VectCartesian> positions;
    private boolean symetry;
    boolean onvaprendrelebleu= false;
    boolean premierPaletPris;
    CompletableFuture<Void> puckStored = null;
    CompletableFuture<Void> elevatorAtRightPlace = null;

    private static final int DISTANCE_INTER_PUCK = 100;
    private static double offsetY ;
    private static double offsetX ;
    private static double offsetTheta;
    private Container container;
    private SynchronizationWithBuddy syncBuddy;
    private long balanceWaitTime;

    float distanceToWall;

    /**
     * Offset avec la planche
     */
    private final int offsetRecalage = 36;

    /*
     * Offset pour corriger la mesure des sicks (différence réel - mesuré)
     */
    private final int offsetSick= 6;

    /**
     * Différence en Y et X entre le sick et le centre du robot
     */
    private final int ySickToRobotCenter=113;

    /**
     * Distance entre les sicks et rapport entre dsick et écart de valeures mesurées pour faire un recalage en rotation
     */

    private final int dsick = 173;
    double rapport ;
    double ecart_mesures_sicks;
    double teta;
    //variable pour le calcul du recalage
    int yEntryPostRecalage = 410-78+15-4-5;

    /**
     * Est-ce qu'on a un recalage sur x6?
     */
    private boolean usingRecalageX6;

    public X6alter(Container container, Master robot, Table table, SynchronizationWithBuddy syncBuddy) {
        super(robot, table);
        this.container = container;
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
    public void execute(Integer version) {
        /*  Donne le côté duquel on commence à prendre les palets selon la position au début du script du robot.
            Autrement dit on divise la demi table en deux et selon cela on choisit de commencer à droite ou à gauche du distributeur
         */

        int i=0;
        loadOffsets();
        double offsetBalance = Offsets.PALETS_X6_BALANCE_Y_JAUNE.get();
        if(symetry) {
            offsetBalance = Offsets.PALETS_X6_BALANCE_Y_VIOLET.get();
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
                offsetTheta =Offsets.PALETSX6_THETA_VIOLET.get();
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
            robot.turn(Math.PI);

            if(version == 4) {
                //On prend le 1er palet
                robot.useActuator(ActuatorsOrder.DESACTIVE_ELECTROVANNE_DROITE);
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
                    robot.turn(Calculs.modulo(Math.PI+Math.PI/16, Math.PI));
                    robot.increaseScore(12);
                    armInPlace.join(); // on attend que le bras soit à la bonne position
                    robot.useActuator(ActuatorsOrder.ACTIVE_ELECTROVANNE_DROITE, true); // on a lâché le palet

                    try {
                        robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_DEPOT, true);
                        syncBuddy.sendBalanceFree();
                        robot.turn(0);
                        robot.moveLengthwise(600,false);
                        if(usingRecalageX6) {
                            recalageX6();
                        }
                        try {
                            robot.turnToPoint(container.getService(Accelerateur.class).entryPosition(Match.ACC_VERSION));
                        } catch (ContainerException e) {
                            e.printStackTrace();
                        }
                    } catch (UnableToMoveException e) {
                        e.printStackTrace();
                    }

                    try {
                        Service.withTimeout(balanceWaitTime, () -> syncBuddy.waitForFreeAccelerator());
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
            } else {
                boolean first = true;

                /**
                 * Booléen qui traque si on a changé de bras
                 */
                boolean hasSwitched = false;
                Iterator<VectCartesian> positionIterator = positions.iterator();
                while(positionIterator.hasNext()) {
                    Log.STRATEGY.debug("#Palets droits= "+robot.getNbPaletsDroits());
                    CompletableFuture<Void> armInPlace = null;
                    if(robot.getNbPaletsDroits() == 5 && !hasSwitched) { // si on a plus de place, on retourne le robot
                        if (!hasSwitched){
                            onvaprendrelebleu = true;
                        }
                        hasSwitched = true;
                        Collections.reverse(positions); // inversion de l'ordre des positions pour parcourir le distributeur dans l'autre sens
                        robot.followPathTo(positions.get(0));
                        robot.turn(0);
                        Log.STRATEGY.debug("Switching side in Palets x6");
                        first = true; // vu qu'on est déjà à la position du palet, on ne se redéplace pas
                    }
                    // on retire la position qu'on est en train de faire
                    positionIterator.next();
                    positionIterator.remove();
                    if(!first) {
                        if(hasSwitched) {
                            if (!onvaprendrelebleu) {
                                robot.turn(0);
                            }
                        } else {
                            robot.turn(Math.PI);
                        }
                    }

                    // Skip le palet bleu
                    //int distance = i == 2 ? 200 : 100;
                    int distance=100;

                    if(hasSwitched) {
                        int finalI = i;
                        // permet de ne pas avoir à recoder les actions du robot si on change de côté
                        robot.invertOrders(robot -> actions(robot, version, finalI, distance));
                    } else {
                        actions(robot, version, i, distance);
                    }
                    first = false;
                    i++;
                }
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
        robot.useActuator(ActuatorsOrder.DESACTIVE_ELECTROVANNE_DROITE, true);

        try {
            robot.turn(Math.PI); // réoriente le robot vers PI
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
                robot.useActuator(ActuatorsOrder.ACTIVE_ELECTROVANNE_DROITE, true);
                if(moveElevator) {
                    robot.useActuator(ActuatorsOrder.DESCEND_ASCENSEUR_DROIT_DE_UN_PALET);
                }
                robot.useActuator(ActuatorsOrder.DESACTIVE_ELECTROVANNE_DROITE);
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

    /**
     * Actions à faire pour une itération du script
     * @param robot le robot
     * @param version la version du script
     * @param i l'indice du palet pris
     * @param distance la distance au prochain palet
     */
    private void actions(Robot robot, int version, int i, int distance) {
        if(version == 0) {
            robot.pushPaletDroit(CouleurPalet.ROUGE);
        }
        else if(version == 1) {
            robot.pushPaletDroit(CouleurPalet.VERT);
        }
        else if(version ==2){
            robot.pushPaletDroit(CouleurPalet.BLEU);
        }
        else if(version == 3){//Pour chaque palet
            switch (i) {
                case 0:robot.pushPaletDroit(CouleurPalet.ROUGE);
                    break;
                case 1:robot.pushPaletDroit(CouleurPalet.VERT);
                    break;
                case 2:robot.pushPaletDroit(CouleurPalet.ROUGE);
                    break;
                case 3:robot.pushPaletDroit(CouleurPalet.ROUGE);
                    break;
                case 4:robot.pushPaletDroit(CouleurPalet.VERT);
                    break;
            }
        }

        // on suppose que l'ascenseur est monté au mx au début
        robot.useActuator(ActuatorsOrder.ACTIVE_LA_POMPE_DROITE);
        robot.useActuator(ActuatorsOrder.DESACTIVE_ELECTROVANNE_DROITE, true);
        robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_DISTRIBUTEUR, true);

        try {
            /*Thread elevatorWaitingThread = new Thread("Thread to wait for right elevator") { // ♪ Musique d'ascenseur ♪
                @Override
                public void run() {
                    if (robot.getNbPaletsDroits() < 5) {
                        robot.useActuator(ActuatorsOrder.DESCEND_MONTE_ASCENCEUR_DROIT_DE_UN_PALET);
                    } else if (robot.getNbPaletsDroits() == 5) {
                        robot.useActuator(ActuatorsOrder.MONTE_DESCEND_ASCENCEUR_DROIT_DE_UN_PALET);
                    }
                    robot.waitForRightElevator();
                    robot.useActuator(ActuatorsOrder.DESCEND_ASCENSEUR_DROIT_DE_UN_PALET);
                }
            };
            elevatorWaitingThread.setDaemon(true);
            elevatorWaitingThread.start();*/

            robot.moveLengthwise(distance, false, () -> {
                robot.useActuator(ActuatorsOrder.DESCEND_ASCENSEUR_DROIT_DE_UN_PALET);
                robot.useActuator(ActuatorsOrder.REMONTE_LE_BRAS_DROIT_DU_DISTRIBUTEUR_VERS_ASCENSEUR, true);
                if (!onvaprendrelebleu) {
                    robot.useActuator(ActuatorsOrder.ACTIVE_ELECTROVANNE_DROITE, true);
                    try {
                        robot.turn(0);
                    } catch (UnableToMoveException e) {
                        e.printStackTrace();
                    }
                }
                onvaprendrelebleu=false;
            });
        } catch (UnableToMoveException e) {
            e.printStackTrace();
        }
    }

    public void recalageX6(/*int yEntry*/) throws UnableToMoveException {
        robot.turn(0);
        robot.computeNewPositionAndOrientation(Sick.NOTHING);
        if(container.getConfig().getString(ConfigData.COULEUR).equals("violet")) {
            ecart_mesures_sicks=Sick.SICK_AVANT_DROIT.getLastMeasure() - Sick.SICK_ARRIERE_DROIT.getLastMeasure();
            rapport = ecart_mesures_sicks / dsick;
            teta = Math.atan(-rapport);
            distanceToWall = (float) (Math.cos(teta)*((Sick.SICK_ARRIERE_DROIT.getLastMeasure() + Sick.SICK_AVANT_DROIT.getLastMeasure()) / 2 + offsetSick + ySickToRobotCenter));
            Log.POSITION.critical("symetrie" + Sick.SICK_ARRIERE_DROIT.getLastMeasure() + " " + Sick.SICK_AVANT_DROIT.getLastMeasure() + " " + distanceToWall);
        }
        else {
            ecart_mesures_sicks=Sick.SICK_AVANT_GAUCHE.getLastMeasure() - Sick.SICK_ARRIERE_GAUCHE.getLastMeasure();
            rapport = ecart_mesures_sicks / dsick;
            teta = Math.atan(-rapport);
            distanceToWall = (float) (Math.cos(teta)*((Sick.SICK_AVANT_GAUCHE.getLastMeasure() + Sick.SICK_ARRIERE_GAUCHE.getLastMeasure()) / 2 + offsetSick + ySickToRobotCenter));
            Log.POSITION.critical("no symetrie" + Sick.SICK_AVANT_GAUCHE.getLastMeasure() + " " + Sick.SICK_ARRIERE_GAUCHE.getLastMeasure() + " " + distanceToWall);
        }
        Vec2 currentPosition = XYO.getRobotInstance().getPosition();
        robot.setPositionAndOrientation(new VectCartesian(currentPosition.getX(), 1543-distanceToWall), Calculs.modulo(teta, Math.PI));
        //robot.gotoPoint(new VectCartesian(currentPosition.getX(), yEntry));
    }


    @Override
    public Vec2 entryPosition(Integer version) {
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
            offsetX = Offsets.PALETSX6_X_VIOLET.get();
            offsetY = Offsets.PALETSX6_Y_VIOLET.get();
        } else {
            offsetX = Offsets.PALETSX6_X_JAUNE.get();
            offsetY = Offsets.PALETSX6_Y_JAUNE.get();
        }
    }

    @Override
    public void executeWhileMovingToEntry(int version) {
        robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_DEPOT,false);
        robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_DEPOT,false);
        if(robot.getNbPaletsDroits()>=1 && robot.getNbPaletsDroits() < 4){ // si l'asc contient 4 palets, on peut plus descendre
            robot.useActuator(ActuatorsOrder.DESCEND_ASCENSEUR_DROIT_DE_UN_PALET, true);
        }
    }

    @Override
    public void finalize(Exception e) {
        // range le bras quand on a fini
        robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_DEPOT,false);
    }
    @Override
    public void updateConfig(Config config) {
        super.updateConfig(config);
        balanceWaitTime = config.getLong(ConfigData.BALANCE_WAIT_TIME);
        symetry = config.getString(ConfigData.COULEUR).equals("violet");
        usingRecalageX6 = ! config.getBoolean(ConfigData.RECALAGE_ACC);
    }
}