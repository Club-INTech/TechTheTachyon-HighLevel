/**
 * Copyright (c) 2018, INTech.
 * this file is part of INTech's HighLevel.

 * INTech's HighLevel is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * INTech's HighLevel is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with it.  If not, see <http://www.gnu.org/licenses/>.
 **/

package locomotion;

import ai.AIService;
import data.Graphe;
import data.Table;
import data.XYO;
import data.graphe.Node;
import data.table.Obstacle;
import pfg.config.Config;
import utils.ConfigData;
import utils.Log;
import utils.container.Service;
import utils.math.Calculs;
import utils.math.Vec2;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Service permettant au robot de se déplacer
 *
 * @author rem
 */
public class Locomotion implements Service {

    /**
     * Service de recherche de chemin
     */
    private Pathfinder pathfinder;

    /**
     * Service de suivit de chemin
     */
    private PathFollower pathFollower;

    /**
     * Table
     */
    private Table table;

    /**
     * Graphe
     */
    private Graphe graphe;

    /**
     * Position & Orientation du robot
     */
    private XYO xyo;

    /**
     * Files de communication avec le PathFollower
     */
    private ConcurrentLinkedQueue<Vec2> pointsQueue;
    private ConcurrentLinkedQueue<UnableToMoveException> exceptionsQueue;

    private AIService ai;

    /**
     * Seuil de distance par rapport à un point pour savoir si un point est considéré comme dans l'autre robot
     */
    private int compareThreshold;

    /**
     * Construit le service de locmotion
     * @param table
     *              table
     * @param graphe
     *              graphe permettant de trouver les chemins
     * @param pathFollower
     *              service de suivit de chemin
     */
    private Locomotion(Table table, Graphe graphe, Pathfinder pathfinder, PathFollower pathFollower) {
        this.table = table;
        this.graphe = graphe;
        this.pathFollower = pathFollower;
        this.pathfinder = pathfinder;
        this.xyo = XYO.getRobotInstance();
        this.pointsQueue = new ConcurrentLinkedQueue<Vec2>();
        this.exceptionsQueue = new ConcurrentLinkedQueue<UnableToMoveException>();
        pathFollower.setPointsQueue(pointsQueue);
        pathFollower.setExceptionsQueue(exceptionsQueue);
        pathFollower.start();
    }

    /**
     * Méthode permettant au robot d'avancer : bloquant
     * @param distance  distance de translation
     */
    public void moveLenghtwise(int distance) throws UnableToMoveException {
        pathFollower.moveLenghtwise(distance, false);
    }

    /**
     * Méthode permettant au robot d'avancer : bloquant
     * @param distance
     *              distance de translation
     * @param expectedWallImpact
     *              true si l'on veut ignorer les blocages mécaniques
     */
    public void moveLenghtwise(int distance, boolean expectedWallImpact) throws UnableToMoveException {
        pathFollower.moveLenghtwise(distance, expectedWallImpact);
    }

    /**
     * Méthode permettant au robot de tourner
     * @param angle angle absolu vers lequel il faut se tourner
     */
    public void turn(double angle) throws UnableToMoveException {
        pathFollower.turn(angle, false);
    }

    /**
     * Méthode permettant au robot de tourner
     * @param angle angle relatif de rotation
     */
    public void turnRelative(double angle) throws UnableToMoveException {
        angle = Calculs.modulo(angle + xyo.getOrientation(), Math.PI);
        pathFollower.turn(angle, false);
    }

    /**
     * Tournes le robot vers le point et l'atteint en ligne droite. Si ça mange un mur c'est votre faute
     * @param point là où on veut aller
     * @throws UnableToMoveException
     *          Quand problème de déplacement
     */
    public void gotoPoint(Vec2 point) throws UnableToMoveException {
        pathFollower.gotoPoint(point);
    }

    /**
     * Méthode permettant au robot de se déplacer jusqu'à un point de la table
     * @param point point à atteindre
     */
    public void followPathTo(Vec2 point, Runnable... parallelActions) throws UnableToMoveException {
        // TODO : Synchroniser
        Node start;
        Node aim;
        Node next;
        LinkedList<Vec2> path;
        UnableToMoveException exception;

        /* Algo :
         *  1. Différencier point/position dans un obstacle & hors-obstacle : gestion dans le PF ?
         *  2. Créer noeuds provisoires (gestion par le graphe)
         *  3. Tant que l'on est pas arrivé,
         *      Calculer le chemin entre le point suivant et le point d'arriver
         *      Mettre à jour le chemin
         *      Tant que le graphe n'est pas mis à jour
         *          Si exception du PathFollower
         *              Si raison est blocage mécanique, rethrow exception
         *              Si raison est trajectoire
         *                  Si c'est l'adversaire
         *                      Detruire le point de départ
         *                      Créer le nouveau point de départ (position)
         *                      Point suivant est point de départ
         *                  Si c'est ton pote
         *                      TODO
         *      Signaler un graphe non mis à jour
         *   4. Clean le graphe : point d'arrivé & de départ
         */

        Optional<Obstacle> obstacleBelowPosition = table.findFixedObstacleInPosition(xyo.getPosition());
        obstacleBelowPosition.ifPresent(obstacle -> Log.LOCOMOTION.warning("Points de départ " + xyo.getPosition() + " dans l'obstacle " + obstacle));
        Optional<Obstacle> obstacleBelowPoint = table.findFixedObstacleInPosition(point);
        obstacleBelowPoint.ifPresent(obstacle -> Log.LOCOMOTION.warning("Points d'arrivée " + point + " dans l'obstacle " + obstacle));

        try {
            graphe.writeLock().lock();
            start = graphe.addProvisoryNode(xyo.getPosition().clone());
            aim = graphe.addProvisoryNode(point);
        } finally {
            graphe.writeLock().unlock();
        }
        pointsQueue.clear();
        exceptionsQueue.clear();

        pathFollower.setParallelActions(parallelActions);

        while (xyo.getPosition().squaredDistanceTo(aim.getPosition()) >= compareThreshold*compareThreshold) {
            try {
                try {
                    graphe.readLock().lock();
                    path = pathfinder.findPath(start, aim);
                }
                finally {
                    graphe.readLock().unlock();
                }


                // on s'assure de bien avoir une liste non vide (s'il y a un chemin) dans PathFollower à la prochaine itération
                // ce thread pourrait être interrompu entre le addAll et le clear ;(
                // ça a pas beaucoup de conséquences dans notre cas mais si on peut sauver 20ms au HL, c'est pas mal (cf Thread.sleep(20) de PathFollower)
                synchronized (pointsQueue) {
                    pointsQueue.clear();
                    pointsQueue.addAll(path);
                }
                while (!graphe.isUpdated() && xyo.getPosition().squaredDistanceTo(aim.getPosition()) >= compareThreshold*compareThreshold) {
                //    System.out.println("xyo: "+xyo.getPosition()+" / aim: "+aim.getPosition());
                    if (exceptionsQueue.peek() != null) {
                        exception = exceptionsQueue.poll();
                        if (exception.getReason().equals(UnableToMoveReason.TRAJECTORY_OBSTRUCTED)) {
                            XYO buddyPos = XYO.getBuddyInstance();
                            if(buddyPos.getPosition().distanceTo(exception.getAim().getPosition()) < compareThreshold) {

                                // TODO: c'est ton pote, on fait quoi?
                            }
                            else { // c'est pas ton pote!
                                try {
                                    graphe.writeLock().lock();
                                    graphe.removeProvisoryNode(start);
                                    start = graphe.addProvisoryNode(xyo.getPosition().clone());
                                    graphe.update();
                                    graphe.setUpdated(true);
                                    if(ai != null)
                                        ai.getAgent().reportMovementError(exception);
                                }
                                finally {
                                    graphe.writeLock().unlock();
                                }
                            }
                        }
                    }

                    try {
                        Thread.sleep(5);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                graphe.setUpdated(false);
            } catch (NoPathFound e) {
                // TODO : Compléter
                e.printStackTrace();
                throw new UnableToMoveException(e.getMessage(), new XYO(aim.getPosition(), 0.0), UnableToMoveReason.NO_PATH);
            }
        }
        pointsQueue.clear();
        exceptionsQueue.clear();
    }

    public void setAI(AIService ai) {
        this.ai = ai;
    }

    /**
     * @see Service#updateConfig(Config)
     */
    @Override
    public void updateConfig(Config config) {
        this.compareThreshold = config.getInt(ConfigData.VECTOR_COMPARISON_THRESHOLD);
    }

    public Table getTable() {
        return table;
    }

    public Graphe getGraphe() {
        return graphe;
    }
}
