package scripts;
import data.CouleurPalet;
import data.SensorState;
import data.Sick;
import data.Table;
import locomotion.UnableToMoveException;
import orders.order.ActuatorsOrder;
import pfg.config.Config;
import robot.Master;
import robot.Robot;
import utils.ConfigData;
import utils.Log;
import utils.math.Calculs;
import utils.math.Vec2;
import utils.math.VectCartesian;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

public class PaletsX6 extends Script {
    private ArrayList<VectCartesian> positions;
    private boolean symetry;
    boolean onvaprendrelebleu= false;

    private static final int DISTANCE_INTER_PUCK = 100;

    private static int offsetY = -4;

    private static final Vec2 positionBalance = new VectCartesian(200,1204+10+5+offsetY+20);

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
        /*position des 6 palets ( position dans le tableau positionS ) */
        this.positions = new ArrayList<>();
    }

    @Override
    public void execute(Integer version) {
            /*  Donne le côté duquel on commence à prendre les palets selon la position au début du script du robot.
                Autrement dit on divise la demi table en deux et selon cela on choisit de commencer à droite ou à gauche du distributeur
             */
            int i=0;
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
                positions.add(new VectCartesian(1000, 1204+10+5+offsetY));
                positions.add(new VectCartesian(900, 1204+10+5+offsetY));
                positions.add(new VectCartesian(800 , 1204+10+5+offsetY));
                positions.add(new VectCartesian(700 , 1204+10+5+offsetY));
                positions.add(new VectCartesian(600, 1204+10+5+offsetY));
                positions.add(new VectCartesian(500, 1204+10+5+offsetY));
            }
        try {
            if(symetry) {
                robot.turn(0);
                robot.computeNewPositionAndOrientation(Sick.UPPER_LEFT_CORNER_TOWARDS_0);
            } else {
                robot.turn(Math.PI);
                robot.computeNewPositionAndOrientation(Sick.UPPER_RIGHT_CORNER_TOWARDS_PI);
            }
            robot.followPathTo(positions.get(0));
            robot.turn(Math.PI);

            if(version == 4) {
                // on prend les 3 palets à droite qu'on met dans l'ascenseur droit
                for (int j = 0; j < 3; j++) {
                    if(j == 2) {
                        grabPuck(robot, DISTANCE_INTER_PUCK*2, true); // skip le palet bleu
                    } else {
                        grabPuck(robot, DISTANCE_INTER_PUCK, true);
                    }

                    // on ajoute le palet dans l'ascenseur
                    switch (j) {
                        case 0:
                            robot.pushPaletDroit(CouleurPalet.ROUGE);
                            break;
                        case 1:
                            robot.pushPaletDroit(CouleurPalet.VERT);
                            break;
                        case 2:
                            robot.pushPaletDroit(CouleurPalet.ROUGE);
                            break;
                    }
                }

                // on prend les 2 autres palets
                for (int j = 0; j < 2; j++) {
                    // invert order pour utiliser la partie gauche du robot
                    if(j == 1) {
                        grabPuck(robot, -DISTANCE_INTER_PUCK*2, true); // retourne devant le bleu
                    } else {
                        grabPuck(robot, DISTANCE_INTER_PUCK, true);
                    }
                    switch (j) {
                        case 0:
                            robot.pushPaletDroit(CouleurPalet.ROUGE);
                            break;
                        case 1:
                            robot.pushPaletDroit(CouleurPalet.VERT);
                            break;
                    }
                }
                // on prend le palet bleu
                grabPuck(robot, 0, false);

                // on va à la balance
                robot.followPathTo(positionBalance);
                // on dépose le bleu
                robot.turn(Calculs.modulo(Math.PI+Math.PI/16, Math.PI));
                robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_BALANCE, true);
                robot.useActuator(ActuatorsOrder.ACTIVE_ELECTROVANNE_DROITE, true); // on a lâché le palet
                robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_ASCENSEUR, false);
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
                    if(robot.getNbPaletsDroits() == 3 && !hasSwitched) { // si on a plus de place, on retourne le robot
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
     * @param ungrab 'true' si on lâche le palet dans l'ascenseur
     */
    private void grabPuck(Robot robot, int moveDistance, boolean ungrab) {
        robot.useActuator(ActuatorsOrder.ACTIVE_LA_POMPE_DROITE);
        robot.useActuator(ActuatorsOrder.DESACTIVE_ELECTROVANNE_DROITE, true);

        robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_DISTRIBUTEUR_SANS_REESSAI, true);

        // on s'assure que l'électrovanne est vraiment bien ouverte
        robot.useActuator(ActuatorsOrder.DESACTIVE_ELECTROVANNE_DROITE, true);

        try {
            robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_DEPOT, true);
            if(moveDistance == 0) {
                robot.useActuator(ActuatorsOrder.DESCEND_ASCENSEUR_DROIT_DE_UN_PALET);
                if(ungrab) {
                    robot.useActuator(ActuatorsOrder.ACTIVE_ELECTROVANNE_DROITE, true);
                }
            } else {
                robot.moveLengthwise(moveDistance, false, () -> {
                    robot.useActuator(ActuatorsOrder.DESCEND_ASCENSEUR_DROIT_DE_UN_PALET);
                    if(ungrab) {
                        robot.useActuator(ActuatorsOrder.ACTIVE_ELECTROVANNE_DROITE, true);
                    }
                });
            }
        } catch (UnableToMoveException e) {
            e.printStackTrace();
        }
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

    @Override
    public Vec2 entryPosition(Integer version) {
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
        else if (version == 3 || version == 4) {
            return new VectCartesian(1500-191-65,1204+10+5+offsetY);
        }
        return null;
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
        symetry = config.getString(ConfigData.COULEUR).equals("violet");
    }
}