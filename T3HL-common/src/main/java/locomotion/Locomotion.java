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

import ai.AIModule;
import data.Graphe;
import data.SensorState;
import data.Table;
import data.XYO;
import data.graphe.Node;
import data.table.MobileCircularObstacle;
import data.table.Obstacle;
import pfg.config.Configurable;
import utils.Log;
import utils.TimeoutError;
import utils.container.Module;
import utils.math.Calculs;
import utils.math.Vec2;
import utils.math.InternalVectCartesian;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

/**
 * Module permettant au robot de se déplacer
 *
 * @author rem
 */
public class Locomotion implements Module {

    /**
     * Module de recherche de chemin
     */
    private Pathfinder pathfinder;

    /**
     * Module de suivit de chemin
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

    private AIModule ai;

    /**
     * Seuil de distance par rapport à un point pour savoir si un point est considéré comme dans l'autre robot
     */
    @Configurable
    private int vectorComparisonThreshold;

    @Configurable
    private long locomotionObstructedTimeout;

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
    public void moveLengthwise(int distance) throws UnableToMoveException, TimeoutError {
        moveLengthwise(distance, false);
    }

    /**
     * Méthode permettant au robot d'avancer : bloquant
     * @param distance
     *              distance de translation
     * @param expectedWallImpact
     *              true si l'on veut ignorer les blocages mécaniques
     */
    public void moveLengthwise(int distance, boolean expectedWallImpact, Runnable... runnables) throws UnableToMoveException, TimeoutError {
        pathFollower.moveLengthwise(distance, expectedWallImpact, runnables);
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


        Optional<Obstacle> obstacleBelowPoint = table.findFixedObstacleInPosition(point);
        obstacleBelowPoint.ifPresent(obstacle -> Log.LOCOMOTION.warning("Point d'arrivée " + point + " dans l'obstacle " + obstacle));

        try {
            graphe.writeLock().lock();
            Optional<Obstacle> obstacleBelowPosition = table.findFixedObstacleInPosition(xyo.getPosition());
            // Si on est dans un obstacle, on cherche la position la plus proche valide
            start = obstacleBelowPosition.map(obstacle -> {
                Log.LOCOMOTION.warning("Point de départ " + xyo.getPosition() + " dans l'obstacle " + obstacle);
                return graphe.addProvisoryNode(obstacle.getShape().closestPointToShape(new InternalVectCartesian(0, 500)/*Centre*/));
            }).orElse(graphe.addProvisoryNode(xyo.getPosition().clone()));
            aim = graphe.addProvisoryNode(point);
        } finally {
            graphe.writeLock().unlock();
        }

        Optional<MobileCircularObstacle> mobileObstacleBelowPoint = table.findMobileObstacleInPosition(point);
        mobileObstacleBelowPoint.ifPresent(obstacle -> {
            Log.LOCOMOTION.warning("Point d'arrivée " + point + " dans l'obstacle mobile " + obstacle);
            Log.LOCOMOTION.warning("Attente de "+ locomotionObstructedTimeout +" ms tant que ça se libère pas...");

            // FIXME: gérer le TimeoutError
            // attente de qq secondes s'il y a un ennemi là où on veut aller
            Module.withTimeout(locomotionObstructedTimeout, () -> {
                while(table.isPositionInMobileObstacle(point)) {
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        break;
                    }
                }
            });
        });

        synchronized (pointsQueue) {
            pointsQueue.clear();
        }
        exceptionsQueue.clear();

        pathFollower.setParallelActions(parallelActions);

        // a-t-on rencontré un ennemi lors du parcours du chemin?
        Set<MobileCircularObstacle> encounteredEnemies = new HashSet<>();

        // on attend d'être à l'arrêt
        Log.LOCOMOTION.debug("followPathTo("+aim.getPosition()+")");
        Module.waitWhileTrue(SensorState.MOVING::getData);
        Log.LOCOMOTION.debug("not moving!");

        boolean encounteredException = true; // 'true' pour forcer le calcul
        while (xyo.getPosition().squaredDistanceTo(aim.getPosition()) >= vectorComparisonThreshold * vectorComparisonThreshold || SensorState.MOVING.getData()) {
            try {
                if (encounteredException) {
                    synchronized (pointsQueue) {
                        try {
                            graphe.readLock().lock();
                            encounteredEnemies.clear();
                            start = graphe.addProvisoryNode(xyo.getPosition().clone());
                            path = pathfinder.findPath(start, aim, encounteredEnemies); // FIXME: détecter s'il y a une erreur à cause d'un ennemi
                        }
                        finally {
                            graphe.readLock().unlock();
                        }
                        pointsQueue.clear();
                        Log.PATHFINDING.debug("=== Nouveau chemin (current pos="+XYO.getRobotInstance().getPosition()+" | start="+start.getPosition()+") ===");
                        path.forEach(p -> Log.PATHFINDING.debug("\t"+p));
                        Log.PATHFINDING.debug("=== Fin ===");
                        pointsQueue.addAll(path);
                    }
                    encounteredException = false;
                }
                while (!graphe.isUpdated() && xyo.getPosition().squaredDistanceTo(aim.getPosition()) >= vectorComparisonThreshold * vectorComparisonThreshold) {
                //    System.out.println("xyo: "+xyo.getPosition()+" / aim: "+aim.getPosition());
                    if (exceptionsQueue.peek() != null) {
                        synchronized (exceptionsQueue) {
                            exception = exceptionsQueue.poll();
                        }
                        //exception.printStackTrace();
                        encounteredException = true;
                        if (exception.getReason().equals(UnableToMoveReason.TRAJECTORY_OBSTRUCTED) || exception.getReason().equals(UnableToMoveReason.ENEMY_IN_PATH)) {
                            XYO buddyPos = XYO.getBuddyInstance();
                            Log.PATHFINDING.critical("Trajectory obstructed, recomputing");
                            if(buddyPos.getPosition().distanceTo(exception.getAim().getPosition()) < vectorComparisonThreshold) {
                                Log.PATHFINDING.critical("POTO TODO!! "+buddyPos+" aim: "+exception.getAim());
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
                        Thread.sleep(20);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    if(!SensorState.MOVING.getData()) {
                        try {
                            encounteredException = true;
                            graphe.writeLock().lock();
                            graphe.removeProvisoryNode(start);
                            start = graphe.addProvisoryNode(xyo.getPosition().clone());
                            graphe.update();
                            graphe.setUpdated(true);
                        }
                        finally {
                            graphe.writeLock().unlock();
                        }
                        break;
                    }
                }

                graphe.setUpdated(false);
            } catch (NoPathFound e) {
                // TODO : Compléter
                //e.printStackTrace();
                if( ! encounteredEnemies.isEmpty()) {
//                    throw new UnableToMoveException(e.getMessage()+", enemy is at "+encounteredEnemy.get(), new XYO(aim.getPosition(), 0.0), UnableToMoveReason.ENEMY_IN_PATH);
                    throw new UnableToMoveException(e.getMessage()+", encountered enemies are at "+encounteredEnemies.stream().map(it -> it.getPosition().toString()).collect(Collectors.joining(", ")), new XYO(aim.getPosition(), 0.0), UnableToMoveReason.ENEMY_IN_PATH);
                } else {
                    throw new UnableToMoveException(e.getMessage(), new XYO(aim.getPosition(), 0.0), UnableToMoveReason.NO_PATH);
                }
            } finally {
                synchronized (pointsQueue) {
                    pointsQueue.clear();
                }
                exceptionsQueue.clear();
            }
        }
        synchronized (pointsQueue) {
            pointsQueue.clear();
        }
        exceptionsQueue.clear();

        Log.LOCOMOTION.debug("Attente de la fin du mouvement en followpathto("+aim.getPosition()+")");
        Module.waitWhileTrue(SensorState.MOVING);
    }

    public void setAI(AIModule ai) {
        this.ai = ai;
    }

    public Table getTable() {
        return table;
    }

    public Graphe getGraphe() {
        return graphe;
    }
}
