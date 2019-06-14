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

package orders.order;

/**
 * Enum qui contient tous les ordres envoyés au LL concernant les mouvements du robot
 *
 * @author yousra
 */
public enum MotionOrder implements Order {
    /**Avancer*/
    MOVE_LENTGHWISE("d"),
    /**Tourner*/
    TURN("t"),
    /** Aller jusqu'à un point */
    MOVE_TO_POINT("goto"),
    /**S'arrêter*/
    STOP("stop"),
    /** On force l'arrêt */
    FORCE_STOP("sstop"),
    /** Couper l'asserv en rotation */
    NO_ROTATION_CONTROL("cr0"),
    /** Activer l'asserv en rotation */
    ROTATION_CONTROL("cr1"),
    ;

    /**Ordre envoyé au LL*/
    private String orderStr;

    /**
     * Constructeur qui ne précise pas la durée de l'action
     * @param orderStr : order envoyé au LL
     */
    MotionOrder(String orderStr){
        this.orderStr = orderStr;
    }

    /**
     * Getter de l'ordre
     * @return l'ordre en string envoyé
     */
    public String getOrderStr(){
        return this.orderStr;
    }
}
