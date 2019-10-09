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
import data.table.MobileCircularObstacle;
import orders.OrderWrapper;
import orders.Speed;
import pfg.config.Configurable;
import utils.Log;
import utils.TimeoutError;
import utils.container.Module;
import utils.container.ModuleThread;
import utils.math.*;

import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

/**
 * Module permettant de suivre un chemin et de détecter les problèmes de suivit
 *
 * @author rem
 */
public class PathFollower extends ModuleThread {

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
    @Configurable
    private long locomotionLoopDelay;

    /**
     * Distance de vérification d'adversaire
     */
    @Configurable
    private int locomotionDistanceCheck;

    /**
     * Rayon du robot
     */
    @Configurable
    private int locomotionRadiusCheck;

    /**
     * Actions exécutées en parallèle du mouvement
     */
    private Runnable[] parallelActions;

    /**
     * Timeout avant d'arrêter d'essayer de se déplacer avec un moveLengthwise
     */
    @Configurable
    private long locomotionObstructedTimeout;

    @Configurable
    private boolean simulation;

    /**
     * Est-ce qu'on a du ralentir à cause d'un ennemi dans le chemin?
     */
    private boolean alreadySlow;

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
        SensorState.STUCKED.setData(false);
        SensorState.MOVING.setData(true);
        orderWrapper.moveToPoint(aim);

        Module.waitWhileTrue(SensorState.MOVING::getData, () -> {
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
     * @param parallelActions
     *              actions à effectuer en parallèle du mouvement
     * @throws UnableToMoveException
     *              en cas d'évènents inattendus
     */
    public void moveLengthwise(int distance, boolean expectedWallImpact, final Runnable... parallelActions) throws UnableToMoveException, TimeoutError {
        try {
            SensorState.STUCKED.setData(false);
            Runnable[] parallelActionsLambda = parallelActions;
            int travelledDistance = 0;
            Vec2 start = robotXYO.getPosition().clone();

            // permet de vérifier qu'on essaie pour la première fois: on évite de s'arrêter et de diminuer la vitesse à chaque itération si on rencontre un ennemi plus loin que là où on veut aller
            boolean firstPass = true;
            boolean shouldRetry = true;
            XYO aim = new XYO(start.plusVector(new VectPolar(distance, robotXYO.getOrientation())), robotXYO.getOrientation());
            do {
                int toTravel = distance - travelledDistance;

                try {
                    handleEnemyForward(distance, aim, firstPass);

                    SensorState.MOVING.setData(true);
                    Log.LOCOMOTION.debug("Move lengthwise "+toTravel);
                    this.orderWrapper.moveLenghtwise(toTravel, expectedWallImpact, parallelActionsLambda);
                    parallelActionsLambda = new Runnable[0]; // on ne refait pas les actions en parallèle

                    boolean finalFirstPass = firstPass;
                    Module.waitWhileTrue(SensorState.MOVING::getData, () -> {
                        handleEnemyForward(distance, aim, finalFirstPass);
                        if(expectedWallImpact && simulation) {
                            if(table.isPositionInFixedObstacle(robotXYO.getPosition(), true)) {
                                orderWrapper.immobilise();
                                // permet de déloger le robot de l'obstacle
                                while(table.isPositionInFixedObstacle(robotXYO.getPosition(), true)) {
                                    this.orderWrapper.moveLenghtwise(-Math.signum(distance)*5, false);
                                    try {
                                        Thread.sleep(10);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                        break;
                                    }
                                }
                                orderWrapper.immobilise();
                                SensorState.STUCKED.setData(true);
                            }
                        }
                        if (SensorState.STUCKED.getData() && !expectedWallImpact) {
                            orderWrapper.immobilise();
                            throw new UnableToMoveException(aim, UnableToMoveReason.PHYSICALLY_STUCKED);
                        }
                    });

                } catch (UnableToMoveException e) {
                    if (e.getReason() == UnableToMoveReason.TRAJECTORY_OBSTRUCTED) {
                        Log.LOCOMOTION.critical("Failed to reach position because of someone in front of me! " + e.getMessage());
                    } else {
                        e.printStackTrace();
                    }
                }

                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    break;
                }

                travelledDistance = (int) (start.distanceTo(robotXYO.getPosition()) * Math.signum(distance)); // distance entre la position de départ et la position actuelle
                firstPass = false;
                if(SensorState.STUCKED.getData() && expectedWallImpact) {
                    shouldRetry=false;
                }
            } while ((Math.abs(travelledDistance-distance) >= 5) && shouldRetry);
        } finally {
            orderWrapper.setBothSpeed(Speed.DEFAULT_SPEED);
            if(expectedWallImpact){
                SensorState.STUCKED.setData(false);
                orderWrapper.immobilise();
                Log.LOCOMOTION.critical("UnableToMove");
            }
        }
        Log.LOCOMOTION.debug("End of move lengthwise");
    }

    /**
     * Vérifies si un ennemi est devant là où on veut aller. S'il est plus loin que la position qu'on veut atteindre, on continue. Le robot ralentit alors si 'shouldSlowDown' est à 'true'
     * @param distance la distance qu'on veut parcourir
     * @param aim le point final où veut être
     * @param shouldSlowDown doit-on ralentir s'il y a un ennemi proche?
     * @throws UnableToMoveException si un ennemi bloque complétement le chemin
     */
    private void handleEnemyForward(int distance, XYO aim, boolean shouldSlowDown) throws UnableToMoveException {
        Optional<MobileCircularObstacle> enemy = getEnemyForward(distance);

        boolean direction = distance > 0;
        double orientation = robotXYO.getOrientation();
        if (!direction) {
            orientation = Calculs.modulo(orientation + Math.PI, Math.PI);
        }

        Segment segment = new Segment(robotXYO.getPosition().clone(),
                robotXYO.getPosition().plusVector(new VectPolar(locomotionDistanceCheck, orientation)));

        if (enemy.isPresent()) {
            // on vérifie si l'endroit où on veut aller est plus proche:
            double distanceToAim = aim.getPosition().distanceTo(XYO.getRobotInstance().getPosition());

            if(distanceToAim < locomotionDistanceCheck) { // si c'est plus proche que la distance de vérification
                Vec2 nearDirection = aim.getPosition().minusVector(XYO.getRobotInstance().getPosition());
                nearDirection.setR(distanceToAim);
                nearDirection.plus(XYO.getRobotInstance().getPosition());
                segment.setPointB(nearDirection);
                if( ! getEnemyInSegment(segment).isPresent()) { // la position d'arrivée est plus proche que l'ennemi, on peut encore avancer
                    // on ralentit si on l'a pas déjà fait
                    if(shouldSlowDown) {
                 //       orderWrapper.immobilise();
                        orderWrapper.setBothSpeed(Speed.SLOW_ALL);
                    }
                    return;
                }
            }

            // l'ennemi est vraiment devant nous
            MobileCircularObstacle obstacle = enemy.get();
            Log.LOCOMOTION.warning("Enemy in front of me: "+obstacle);
            Log.LOCOMOTION.warning("Attente de "+ locomotionObstructedTimeout +" ms tant que ça se libère pas...");

            orderWrapper.immobilise();

            // attente de qq secondes s'il y a un ennemi là où on veut aller
            try {
                Module.withTimeout(locomotionObstructedTimeout, () -> {
                    while(getEnemyForward(distanceToAim).isPresent()) {
                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException e) {
                            break;
                        }
                    }
                });
            } catch (TimeoutError timeout) {
                throw new UnableToMoveException("Enemy intersects "+segment+": "+enemy.get().toString()+" ", aim, UnableToMoveReason.TRAJECTORY_OBSTRUCTED);
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
    public void turn(double angle, boolean expectedWallImpact, Runnable... parallelActions) throws UnableToMoveException {
        try {
            SensorState.STUCKED.setData(false);
            // on désactive le lidar pendant qu'on tourne pour éviter d'avoir des "traces" des obstacles lors de la rotation
            SensorState.DISABLE_LIDAR.setData(true);
            XYO aim = new XYO(robotXYO.getPosition().clone(), Calculs.modulo(robotXYO.getOrientation() + angle, Math.PI));
            SensorState.MOVING.setData(true);
            this.orderWrapper.turn(angle, parallelActions);

            Module.waitWhileTrue(SensorState.MOVING::getData, () -> {
                if (isCircleObstructed()) {
                    orderWrapper.immobilise();
                    throw new UnableToMoveException("Current pos: "+robotXYO, aim, UnableToMoveReason.TRAJECTORY_OBSTRUCTED);
                }
                if (SensorState.STUCKED.getData() && !expectedWallImpact) {
                    orderWrapper.immobilise();
                    throw new UnableToMoveException(aim, UnableToMoveReason.PHYSICALLY_STUCKED);
                }
            });
        } finally {
            SensorState.DISABLE_LIDAR.setData(false);
        }
    }

    /**
     * Méthode permettant d'aller jusqu'à un point
     * @param point point à atteindre
     * @throws UnableToMoveException
     *              en cas de blocage mécanique ou d'adversaire
     */
    private void moveToPoint(Vec2 point, Runnable... parallelActions) throws UnableToMoveException {
        if(point==null){
            Log.LOCOMOTION.critical("je ne peux pas me déplacer vers un point null!");
        }
        Vec2 maPosition=robotXYO.getPosition();
        if(maPosition == null){
            Log.LOCOMOTION.critical("ma position est null!");
        }
        Vec2 trajet=point.minusVector(maPosition);
        if(trajet == null){
            Log.LOCOMOTION.critical("Mon trajet est null!");
        }
        double alpha = Calculs.modulo(trajet.getA(), Math.PI);
        XYO aim = new XYO(point, alpha);
        Segment segment = new Segment(new InternalVectCartesian(0,0), new InternalVectCartesian(0,0));
        segment.setPointA(XYO.getRobotInstance().getPosition());
        SensorState.MOVING.setData(true);
        SensorState.STUCKED.setData(false);
        orderWrapper.setBothSpeed(Speed.DEFAULT_SPEED);
        this.orderWrapper.moveToPoint(point, parallelActions);

        try {
            Module.waitWhileTrue(SensorState.MOVING::getData, () -> {
                if (isCircleObstructed()) {
                    orderWrapper.immobilise();
                    throw new UnableToMoveException("Current pos: "+robotXYO, aim, UnableToMoveReason.TRAJECTORY_OBSTRUCTED);
                }
                Vec2 nearDirection = aim.getPosition().minusVector(XYO.getRobotInstance().getPosition());
                nearDirection.setR(this.locomotionDistanceCheck);
                nearDirection.plus(XYO.getRobotInstance().getPosition());
                segment.setPointB(nearDirection);

                Optional<MobileCircularObstacle> enemy = getEnemyInSegment(segment);
                if (enemy.isPresent()) {


                    // on vérifie si l'endroit où on veut aller est plus proche:
                    double distanceToAim = aim.getPosition().distanceTo(XYO.getRobotInstance().getPosition());

                    boolean needsToStop = true;
                    if(distanceToAim < locomotionDistanceCheck) {
                        nearDirection = aim.getPosition().minusVector(XYO.getRobotInstance().getPosition());
                        nearDirection.setR(distanceToAim);
                        nearDirection.plus(XYO.getRobotInstance().getPosition());
                        segment.setPointB(nearDirection);
                        if( ! getEnemyInSegment(segment).isPresent()) { // la position d'arrivée est plus proche que l'ennemi, on peut encore avancer
                            //orderWrapper.immobilise();
                            if(!alreadySlow) {
                                Log.PATHFINDING.debug("Enemy in front, slowing down... (enemy is "+enemy.get()+")");
                                orderWrapper.setBothSpeed(Speed.SLOW_ALL);
                                // on redemande le déplacement
                                this.orderWrapper.moveToPoint(point);
                                alreadySlow = true;
                            }
                            needsToStop = false;
                        }
                    }

                    if(needsToStop) {
                        orderWrapper.immobilise();
                        throw new UnableToMoveException("Enemy intersects "+segment+": "+enemy.get().toString()+" ", aim, UnableToMoveReason.TRAJECTORY_OBSTRUCTED);
                    }
                }
                if (SensorState.STUCKED.getData()) {
                    orderWrapper.immobilise();
                    throw new UnableToMoveException(aim, UnableToMoveReason.PHYSICALLY_STUCKED);
                }
            });
        } finally {
            // on reset la vitesse quoi qu'il arrive
            orderWrapper.setBothSpeed(Speed.DEFAULT_SPEED);
            alreadySlow = false;
        }
    }

    /**
     * Vérifie en fonction de la vitesse s'il y a un adversaire en face du robot
     * @param distance la distance à parcourir
     * @return <pre>Optional.none()</pre> s'il n'y a pas d'ennemi, <pre>Optional.of(&lt;some&gt;)</pre> s'il y en a un
     */
    private Optional<MobileCircularObstacle> getEnemyForward(double distance) {
        // true si l'on va vers l'avant du robot
        boolean direction = distance > 0;
        double orientation = robotXYO.getOrientation();
        if (!direction) {
            orientation = Calculs.modulo(orientation + Math.PI, Math.PI);
        }
        Segment segment = new Segment(robotXYO.getPosition().clone(),
                robotXYO.getPosition().plusVector(new VectPolar(locomotionDistanceCheck, orientation)));
        return getEnemyInSegment(segment);
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
        while (!isInterrupted()) {
            while (pointsQueue.isEmpty()) {
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            try {
                hasNext = true;
                while(hasNext) {
                    try {
                        TimeUnit.MILLISECONDS.sleep(5);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    synchronized (pointsQueue) {
                        aim = pointsQueue.peek();
                        if(aim == null) // fin de la liste
                            break;
                        if(SensorState.MOVING.getData()) { // on bouge encore à cause d'un autre thread, on bouge pas
                            continue;
                        }
                        Log.LOCOMOTION.debug("Move to "+aim+", current pos: "+XYO.getRobotInstance());
                        if(pointsQueue.isEmpty()){
                            Log.LOCOMOTION.critical("je n'ai plus de points dans pointsQueue");
                        }
                        aim = pointsQueue.poll();
                        if(aim == null){
                            Log.LOCOMOTION.critical("Je n'ai pas de but");
                        }
                        this.moveToPoint(aim, parallelActions);
                        parallelActions = new Runnable[0];
                        hasNext = !pointsQueue.isEmpty();
                    }
                }
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
     * Getters & Setters
     */
    void setPointsQueue(ConcurrentLinkedQueue<Vec2> pointsQueue) {
        this.pointsQueue = pointsQueue;
    }

    void setExceptionsQueue(ConcurrentLinkedQueue<UnableToMoveException> exceptionsQueue) {
        this.exceptionsQueue = exceptionsQueue;
    }
}
