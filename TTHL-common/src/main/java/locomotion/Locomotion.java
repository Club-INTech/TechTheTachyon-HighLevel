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

import data.Graphe;
import data.Table;
import data.XYO;
import data.graphe.Node;
import pfg.config.Config;
import utils.Log;
import utils.container.Service;
import utils.math.Calculs;
import utils.math.Vec2;

import java.util.ArrayList;
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
        angle = Calculs.modulo(angle + xyo.getOrientation(), 2*Math.PI);
        pathFollower.turn(angle, false);
    }

    /**
     * Méthode permettant au robot de se déplacer jusqu'à un point de la table
     * @param point point à atteindre
     */
    public void moveToPoint(Vec2 point) throws UnableToMoveException {
        // TODO : Synchroniser
        Node start;
        Node aim;
        Node next;
        ArrayList<Vec2> path;
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
        if (table.isPositionInFixedObstacle(point) || table.isPositionInFixedObstacle(xyo.getPosition())) {
            Log.LOCOMOTION.warning("Points de départ " + xyo.getOrientation() + " ou d'arriver " + point + " dans un obstacle");
        }

        start = graphe.addProvisoryNode(xyo.getPosition());
        next = start;
        aim = graphe.addProvisoryNode(point);
        pointsQueue.clear();
        exceptionsQueue.clear();

        while (xyo.getPosition().equals(aim.getPosition())) {
            try {
                path = pathfinder.findPath(next, aim);
                pointsQueue.clear();
                pointsQueue.addAll(path);
                while (!graphe.isUpdated()) {
                    if (exceptionsQueue.peek() != null) {
                        exception = exceptionsQueue.poll();
                        if (exception.getReason().equals(UnableToMoveReason.TRAJECTORY_OBSTRUCTED)) {
                            // TODO : Gérer les cas ou les points d'arrivé et de départ sont dans des obstacles
                            graphe.removeProvisoryNode(start);
                            start = graphe.addProvisoryNode(xyo.getPosition());
                            next = start;
                        }
                    }
                }
                graphe.setUpdated(false);
            } catch (NoPathFound e) {
                // TODO : Compéter
            }
        }
        graphe.removeProvisoryNode(start);
        graphe.removeProvisoryNode(aim);
        pointsQueue.clear();
        exceptionsQueue.clear();
    }

    /**
     * @see Service#updateConfig(Config)
     */
    @Override
    public void updateConfig(Config config) {}
}
