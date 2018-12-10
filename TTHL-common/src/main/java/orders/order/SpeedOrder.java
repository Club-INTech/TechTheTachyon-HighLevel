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
 * Enum qui contient tous les ordres concernant la vitesse
 *
 * @author yousra
 */
public enum SpeedOrder implements Order {

    SET_TRANSLATION_SPEED("ctv"),
    SET_ROTATIONNAL_SPEED("crv"),
    SET_SPEED("ctrv"),
    ;

    /**
     * Ordre envoyé au LL
     */
    private String orderStr;

    /**
     * Constructeur qui ne précise pas la durée l'action
     * @param orderStr action à faire
     */
    SpeedOrder(String orderStr){
        this.orderStr = orderStr;
    }

    /**
     * Getters
     */
    public String getOrderStr(){
        return this.orderStr;
    }
}
