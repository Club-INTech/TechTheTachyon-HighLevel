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
 * Enum qui contient tous les ordres envoyé au LL qui concernent les hooks
 *
 * @author yousra
 */
public enum HooksOrder implements Order {
    /**Initialiser un hook , c'est dire au LL qu'on veut qu'il y'ait tel hook à tel endroit **/
    INITIALISE_HOOK("nh"),
    /**Permet d'activer un hook, c'est pas parce qu'un hook est configuré qu'il sera activé, peut être on veut l'utiliser une seule fois  */
    ENABLE_HOOK("eh"),
    /**Permet de désactiver un hool*/
    DISABLE_HOOK("dh"),
        ;

    /**Ordre envoyé au LL*/
    private String orderStr;

    /**
     * Constructeur qui ne précise pas la durée des actions
     * @param orderStr ordre envoyé au LL
     */
    HooksOrder(String orderStr){
        this.orderStr = orderStr;
    }

    /**getter de l'ordre
     * @return ordre en string envoyé
     * */
    public String getOrderStr(){
        return this.orderStr;
    }
}
