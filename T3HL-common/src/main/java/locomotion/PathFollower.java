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
import data.SensorState;
import data.Table;
import data.XYO;
import data.table.MobileCircularObstacle;
import orders.OrderWrapper;
import pfg.config.Config;
import utils.ConfigData;
import utils.Log;
import utils.container.Service;
import utils.container.ServiceThread;
import utils.math.*;

import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Service permettant de suivre un chemin et de détecter les problèmes de suivit
 *
 * @author rem
 */
public class PathFollower extends ServiceThread {

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
     * Actions exécutées en parallèle du mouvement
     */
    private Runnable[] parallelActions;

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
     * Méthode permettant d'envoyer l'ordre 'goto' au LL. ça réfléchit pas, ça fonce tout droit
     * @param aim le point à atteindre
     */
    public void gotoPoint(Vec2 aim) throws UnableToMoveException {
        SensorState.MOVING.setData(true);
        orderWrapper.gotoPoint(aim);

        waitWhileTrue(SensorState.MOVING::getData, () -> {
            if (SensorState.STUCKED.getData()) {
                orderWrapper.immobilise();
                throw new UnableToMoveException(new XYO(aim, 0.0), UnableToMoveReason.PHYSICALLY_STUCKED);
            }
        });
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
    public void moveLenghtwise(int distance, boolean expectedWallImpact, Runnable... parallelActions) throws UnableToMoveException {
        XYO aim = new XYO(robotXYO.getPosition().plusVector(new VectPolar(distance, robotXYO.getOrientation())), robotXYO.getOrientation());
        SensorState.MOVING.setData(true);
        this.orderWrapper.moveLenghtwise(distance, parallelActions);

        waitWhileTrue(SensorState.MOVING::getData, () -> {
            Optional<MobileCircularObstacle> enemy = getEnemyForward(distance > 0);
            if (enemy.isPresent()) {
                orderWrapper.immobilise();
                throw new UnableToMoveException("Enemy pos is "+enemy.get().toString()+" ", aim, UnableToMoveReason.TRAJECTORY_OBSTRUCTED);
            }
            if (SensorState.STUCKED.getData() && !expectedWallImpact) {
                orderWrapper.immobilise();
                throw new UnableToMoveException(aim, UnableToMoveReason.PHYSICALLY_STUCKED);
            }
        });
    }

    /**
     * Méthode permettant de tourner !
     * @param angle angle absolu vers lequel il faut se tourner
     * @param expectedWallImpact
     *              true si l'on veut ignorer les blocages mécaniques
     * @throws UnableToMoveException
     *              en cas de blocage mécanique ou d'adversaire
     */
    public void turn(double angle, boolean expectedWallImpact, Runnable... parallelActions) throws UnableToMoveException {
        XYO aim = new XYO(robotXYO.getPosition().clone(), Calculs.modulo(robotXYO.getOrientation() + angle, Math.PI));
        SensorState.MOVING.setData(true);
        this.orderWrapper.turn(angle, parallelActions);

        waitWhileTrue(SensorState.MOVING::getData, () -> {
            if (isCircleObstructed()) {
                orderWrapper.immobilise();
                throw new UnableToMoveException("Current pos: "+robotXYO, aim, UnableToMoveReason.TRAJECTORY_OBSTRUCTED);
            }
            if (SensorState.STUCKED.getData() && !expectedWallImpact) {
                orderWrapper.immobilise();
                throw new UnableToMoveException(aim, UnableToMoveReason.PHYSICALLY_STUCKED);
            }
        });
    }

    /**
     * Méthode permettant d'aller jusqu'à un point
     * @param point point à atteindre
     * @throws UnableToMoveException
     *              en cas de blocage mécanique ou d'adversaire
     */
    private void moveToPoint(Vec2 point, Runnable... parallelActions) throws UnableToMoveException {
        XYO aim = new XYO(point, Calculs.modulo(point.minusVector(robotXYO.getPosition()).getA(), Math.PI));
        Segment segment = new Segment(new VectCartesian(0,0), new VectCartesian(0,0));
        segment.setPointA(XYO.getRobotInstance().getPosition());
        SensorState.MOVING.setData(true);
        this.orderWrapper.moveToPoint(point, parallelActions);

        waitWhileTrue(SensorState.MOVING::getData, () -> {
            if (isCircleObstructed()) {
                orderWrapper.immobilise();
                throw new UnableToMoveException("Current pos: "+robotXYO, aim, UnableToMoveReason.TRAJECTORY_OBSTRUCTED);
            }
            Vec2 nearDirection = aim.getPosition().minusVector(XYO.getRobotInstance().getPosition());
            nearDirection.setR(this.DISTANCE_CHECK);
            nearDirection.plus(XYO.getRobotInstance().getPosition());
            segment.setPointB(nearDirection);

            Optional<MobileCircularObstacle> enemy = getEnemyInSegment(segment);
            if (enemy.isPresent()) {
                orderWrapper.immobilise();
                throw new UnableToMoveException("Enemy intersects "+segment+": "+enemy.get().toString()+" ", aim, UnableToMoveReason.TRAJECTORY_OBSTRUCTED);
            }
            if (SensorState.STUCKED.getData()) {
                orderWrapper.immobilise();
                throw new UnableToMoveException(aim, UnableToMoveReason.PHYSICALLY_STUCKED);
            }
        });
    }

    /**
     * Vérifie en fonction de la vitesse s'il y a un adversaire en face du robot
     * @param direction true si l'on va vers l'avant du robot
     * @return <pre>Optional.none()</pre> s'il n'y a pas d'ennemi, <pre>Optional.of(&lt;some&gt;)</pre> s'il y en a un
     */
    private Optional<MobileCircularObstacle> getEnemyForward(boolean direction) {
        double orientation = robotXYO.getOrientation();
        if (!direction) {
            orientation = Calculs.modulo(orientation + Math.PI, Math.PI);
        }
        Segment seg = new Segment(robotXYO.getPosition().clone(),
                robotXYO.getPosition().plusVector(new VectPolar(DISTANCE_CHECK, orientation)));
        return getEnemyInSegment(seg);
    }

    /**
     * Vérifie s'il y a un adversaire sur le segment
     * @return  true s'il y a un adversaire sur le segment
     */
    private boolean isSegmentObstructed(Segment segment) {
        return table.intersectAnyMobileObstacle(segment);
    }

    private Optional<MobileCircularObstacle> getEnemyInSegment(Segment segment) {
        synchronized (table.getMobileObstacles()) {
            return table.getMobileObstacles().stream()
                    .filter(it -> it.intersect(segment))
                    .findFirst();
        }
    }

    /**
     * Vérifie s'il y a un adversaire dans le cercle donné
     * @return  true s'il y a un adversaire autour du robot
     */
    private boolean isCircleObstructed() {
       /* Circle circle = new Circle(robotXYO.getPosition().clone(), RADIUS_CHECK);
        return table.intersectAnyMobileObstacle(circle);*/
       return false;
    }

    @Override
    public void run() {
        // TODO : Vérification que la synchronisation est correcte (qu'il n'y ai pas de deadlock).
        // TODO cont. : En théorie, l'utilisation des ConcurrentLinkedList fait qu'il n'y en a pas besoin
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
                    synchronized (pointsQueue) {
                        aim = pointsQueue.poll();
                        Log.LOCOMOTION.debug("Move to "+aim+", current pos: "+XYO.getRobotInstance());
                        this.moveToPoint(aim, parallelActions);
                        parallelActions = new Runnable[0];
                        hasNext = !pointsQueue.isEmpty();
                    }
                } while (hasNext);
            } catch (UnableToMoveException e) {
                e.printStackTrace();
                synchronized (pointsQueue) {
                    this.pointsQueue.clear();
                }
                synchronized (exceptionsQueue) {
                    exceptionsQueue.add(e);
                }
            }
        }
    }

    /**
     * Setter des actions à exécuter en parallèle du mouvement. Attention! Le champ est reset à un tableau vide dès qu'un mouvement est fini
     * @param parallelActions
     */
    public void setParallelActions(Runnable[] parallelActions) {
        this.parallelActions = parallelActions;
    }

    @Override
    public void interrupt() {
        // TODO
    }

    public ConcurrentLinkedQueue<Vec2> getQueue() {
        return pointsQueue;
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
