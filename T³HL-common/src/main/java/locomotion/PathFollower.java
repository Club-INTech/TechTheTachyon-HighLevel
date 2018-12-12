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

import data.SensorState;
import data.Table;
import data.XYO;
import orders.OrderWrapper;
import pfg.config.Config;
import utils.ConfigData;
import utils.container.Service;
import utils.math.Calculs;
import utils.math.Vec2;
import utils.math.VectPolar;
import utils.math.Circle;
import utils.math.Segment;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Service permettant de suivre un chemin et de détecter les problèmes de suivit
 *
 * @author rem
 */
public class PathFollower extends Thread implements Service {

    /**
     * Order Wrapper
     */
    private OrderWrapper orderWrapper;

    /**
     * Table
     */
    private Table table;

    /**
     * Position & orientation du robot
     */
    private XYO robotXYO;

    /**
     * Queue de communication avec Locomotion lors du suivit d'un chemin
     */
    private ConcurrentLinkedQueue<Vec2> pointsQueue;

    /**
     * Queue de communication avec Locomotion lors du suivit d'un chemin
     */
    private ConcurrentLinkedQueue<UnableToMoveException> exceptionsQueue;

    /**
     * Temps entre 2 vérifications de blocage
     */
    private int LOOP_DELAY;

    /**
     * Distance de vérification d'adversaire
     */
    private int DISTANCE_CHECK;

    /**
     * Rayon du robot
     */
    private int RADIUS_CHECK;

    /**
     * Construit le service de suivit de chemin
     * @param orderWrapper
     *              order wrapper
     * @param table
     *              table
     */
    private PathFollower(OrderWrapper orderWrapper, Table table) {
        this.orderWrapper = orderWrapper;
        this.table = table;
        this.robotXYO = XYO.getRobotInstance();
    }

    /**
     * Méthode permettant d'envoyer l'ordre d'avancer au LL et détecter les anomalies jusqu'à être arrivé
     * @param distance
     *              distance de mouvement
     * @param expectedWallImpact
     *              true si l'on veut ignorer les blocages mécaniques
     * @throws UnableToMoveException
     *              en cas d'évènents inattendus
     */
    public void moveLenghtwise(int distance, boolean expectedWallImpact) throws UnableToMoveException {
        XYO aim = new XYO(robotXYO.getPosition().plusVector(new VectPolar(distance, robotXYO.getOrientation())), robotXYO.getOrientation());

        this.orderWrapper.moveLenghtwise(distance);

        while ((Boolean) SensorState.MOVING.getData()) {
            try {
                Thread.sleep(LOOP_DELAY);
                if (isLineObstructed(distance > 0)) {
                    orderWrapper.immobilise();
                    throw new UnableToMoveException(aim, UnableToMoveReason.TRAJECTORY_OBSTRUCTED);
                }
                if ((Boolean) SensorState.STUCKED.getData() && !expectedWallImpact) {
                    orderWrapper.immobilise();
                    throw new UnableToMoveException(aim, UnableToMoveReason.PHYSICALLY_STUCKED);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Méthode permettant de tourner !
     * @param angle angle absolu vers lequel il faut se tourner
     * @param expectedWallImpact
     *              true si l'on veut ignorer les blocages mécaniques
     * @throws UnableToMoveException
     *              en cas de blocage mécanique ou d'adversaire
     */
    public void turn(double angle, boolean expectedWallImpact) throws UnableToMoveException {
        XYO aim = new XYO(robotXYO.getPosition().clone(), Calculs.modulo(robotXYO.getOrientation() + angle, 2*Math.PI));

        this.orderWrapper.turn(angle);

        while ((Boolean) SensorState.MOVING.getData()) {
            try {
                Thread.sleep(LOOP_DELAY);
                if (isCircleObstructed()) {
                    orderWrapper.immobilise();
                    throw new UnableToMoveException(aim, UnableToMoveReason.TRAJECTORY_OBSTRUCTED);
                }
                if ((Boolean) SensorState.STUCKED.getData() && !expectedWallImpact) {
                    orderWrapper.immobilise();
                    throw new UnableToMoveException(aim, UnableToMoveReason.PHYSICALLY_STUCKED);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Méthode permettant d'aller jusqu'à un point
     * @param point point à atteindre
     * @throws UnableToMoveException
     *              en cas de blocage mécanique ou d'adversaire
     */
    private void moveToPoint(Vec2 point) throws UnableToMoveException {
        XYO aim = new XYO(point, point.minusVector(robotXYO.getPosition()).getA());

        this.orderWrapper.moveToPoint(point);

        while ((Boolean) SensorState.MOVING.getData()) {
            // TODO
        }
    }

    /**
     * Vérifie en fonction de la vitesse s'il y a un adversaire en face du robot
     * @param direction true si l'on va vers l'avant du robot
     * @return  true s'il y a un adversaire devant le robot
     */
    private boolean isLineObstructed(boolean direction) {
        double orientation = robotXYO.getOrientation();
        if (!direction) {
            orientation = Calculs.modulo(orientation + Math.PI, 2*Math.PI);
        }
        Segment seg = new Segment(robotXYO.getPosition().clone(),
                robotXYO.getPosition().plusVector(new VectPolar(DISTANCE_CHECK, orientation)));
        return table.intersectAnyMobileObstacle(seg);
    }

    /**
     * Vérifie s'il y a un adversaire dans le cercle donné
     * @return  true s'il y a un adversaire autour du robot
     */
    private boolean isCircleObstructed() {
        Circle circle = new Circle(robotXYO.getPosition().clone(), RADIUS_CHECK);
        return table.intersectAnyMobileObstacle(circle);
    }

    @Override
    public void run() {
        // TODO : Synchroniser
        Vec2 aim;
        boolean hasNext;
        while (!Thread.currentThread().isInterrupted()) {
            while (this.pointsQueue.peek() == null) {
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            try {
                do {
                    aim = pointsQueue.poll();
                    this.moveToPoint(aim);
                    hasNext = !pointsQueue.isEmpty();
                } while (hasNext);
            } catch (UnableToMoveException e) {
                e.printStackTrace();
                synchronized (this) {
                    this.pointsQueue.clear();
                    exceptionsQueue.add(e);
                }
            }
        }
    }

    @Override
    public void interrupt() {
        // TODO
    }

    /**
     * @see Service#updateConfig(Config)
     */
    @Override
    public void updateConfig(Config config) {
        this.LOOP_DELAY = config.getInt(ConfigData.LOCOMOTION_LOOP_DELAY);
        this.DISTANCE_CHECK = config.getInt(ConfigData.LOCOMOTION_DISTANCE_CHECK);
        this.RADIUS_CHECK = config.getInt(ConfigData.LOCOMOTION_RADIUS_CHECK);
    }

    /**
     * Getters & Setters
     */
    void setPointsQueue(ConcurrentLinkedQueue<Vec2> pointsQueue) {
        this.pointsQueue = pointsQueue;
    }
    void setExceptionsQueue(ConcurrentLinkedQueue<UnableToMoveException> exceptionsQueue) {
        this.exceptionsQueue = exceptionsQueue;
    }
}
