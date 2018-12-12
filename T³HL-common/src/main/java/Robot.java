/**
 * Copyright (c) 2018, INTech.
 * this file is part of INTech's HighLevel.
 *
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

import data.XYO;
import locomotion.Locomotion;
import locomotion.UnableToMoveException;
import orders.OrderWrapper;
import orders.order.ActuatorsOrder;
import orders.Speed;
import pfg.config.Config;
import utils.container.Service;
import utils.math.Vec2;

/**
 * Classe regroupant tout les services et fonctionnalitées de base du robot
 * TODO : Compléter
 *
 * @author rem
 */
public abstract class Robot implements Service {

    /**
     * Service qui permet au robot de bouger
     */
    protected Locomotion locomotion;

    /**
     * Service qui permet au robot d'envoyer des ordres au LL
     */
    protected OrderWrapper orderWrapper;

    /**
     * Position et Orientation du robot
     */
    protected XYO xyo;

    /**
     * @param locomotion
     *              service de mouvement du robot
     * @param orderWrapper
     *              service d'envoie d'ordre vers le LL
     */
    protected Robot(Locomotion locomotion, OrderWrapper orderWrapper) {
        this.locomotion = locomotion;
        this.orderWrapper = orderWrapper;
    }

    /**
     * Permet au robot d'aller jusqu'à un point donnée
     * @param point
     *              le point visé
     * @throws UnableToMoveException
     *              en cas de problème de blocage/adversaire
     */
    public void moveToPoint(Vec2 point) throws UnableToMoveException {
        this.locomotion.moveToPoint(point);
    }

    /**
     * Permet au robot d'avancer/recluer en ligne droite
     * @param distance
     *              la distance à parcourir, négative si l'on veut aller en arrière
     * @param expectedWallImpact
     *              true si l'on s'attend à un blocage mécanique (lorsque l'on veut se caler contre le mur par exemple)
     * @throws UnableToMoveException
     *              en cas de problèmes de blocage/adversaire
     */
    public void moveLengthwise(int distance, boolean expectedWallImpact) throws UnableToMoveException {
        this.locomotion.moveLenghtwise(distance, expectedWallImpact);
    }

    /**
     * Permet au robot de tourner sur lui-même
     * @param angle
     *              angle absolue vers lequel on veut se tourner
     * @throws UnableToMoveException
     *              en cas de problème de blocage/adversaire
     */
    public void turn(double angle) throws UnableToMoveException {
        this.locomotion.turn(angle);
    }

    /**
     * Permet au robot d'utiliser un actionneur
     * @param order
     *              l'ordre que l'on veut executer
     */
    protected void useActuator(ActuatorsOrder order) {
        this.orderWrapper.useActuator(order);
    }

    /**
     * Change la vitesse du LL
     * @param speed
     *              la vitesse souhaitée
     */
    public void setSpeed(Speed speed) {
        this.orderWrapper.setBothSpeed(speed);
    }

    /**
     * Change la position du LL
     * @param pos
     *              position souhaitée
     * @param orientation
     *              orientation souhaitée
     */
    public void setPositionAndOrientation(Vec2 pos, double orientation) {
        this.orderWrapper.setPositionAndOrientation(pos, orientation);
    }
    // And so on...

    @Override
    public void updateConfig(Config config) {

    }
}
