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

package orders.hooks;


import lowlevel.order.Order;
import orders.Speed;
import orders.order.SpeedOrders;
import utils.math.Vec2;
import utils.math.InternalVectCartesian;

/**
 * Contient le nom des hooks et leurs paramètres associés
 * ATTENTION à ne pas mettre deux hooks avec le meme id !!
 *
 * @author yousra
 */
public enum HookNames {

    // Example :
    SPEED_DOWN(1, new InternalVectCartesian(50, 50), 5 ,0,Math.PI, SpeedOrders.SetSpeed.compileWith(50.f)),
    ;

    /**
     * L'ordre à appliquer
     */
    private final Order order;
    /**
     * Position de trigger du hook
     */
    private Vec2 position;

    /**
     * Tolérence sur la position
     */
    private int tolerency; //en mm

    /**
     * Id du hook, utile pour pouvoir l'activer/désactivé manuellement
     */
    private int id;

     /**
      * Orientation du robot sur laquelle se déclanche le hook
      */
    private double orientation;

     /**
      * Tolérance en angle
      */
    private double tolerencyAngle; //en radians

    /**
     * Constructeur
     * @param id    identifiant du hook
     * @param position
     *              position de déclanchement du hook
     * @param tolerency
     *              tolérance en position
     * @param orientation
     *              orientation de déclanchement du hook
     * @param tolerencyAngle
     *              tolérence en angle
     * @param order ordre à executer
     */
    HookNames(int id, Vec2 position, int tolerency, double orientation, double tolerencyAngle, Order order){
        this.id = id;
        this.position = position;
        this.tolerency = tolerency;
        this.order = order;
        this.orientation=orientation;
        this.tolerencyAngle=tolerencyAngle;
    }

    /** Getters & Setters */
    public Order getOrder() {
        return order;
    }
    public Vec2 getPosition() {
        return position;
    }
    public int getTolerency(){
        return tolerency;
    }
    public int getId() {
        return id;
    }
    public double getOrientation() {
        return orientation;
    }
    public double getTolerencyAngle() {
        return tolerencyAngle;
    }
}


