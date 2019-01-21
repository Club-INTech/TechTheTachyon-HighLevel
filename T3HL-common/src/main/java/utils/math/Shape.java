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

package utils.math;

/**
 * Il s'agit d'une classe abstraite définissant un cadre pour créer des formes
 *
 * @author yousra, rem
 */
public abstract class Shape implements Cloneable {
    /**
     * Centre de la forme
     */
    protected Vec2 center;

    /**
     * Constructeur
     * @param center    centre
     */
    protected Shape(Vec2 center) {
        this.center =center;
    }

    /**
     * @param segment   segment
     * @return  true s'il y'a intersection avec le segment
     */
    public abstract boolean intersect(Segment segment);

    /**
     * @param point point
     * @return  true si le point se trouve dans la forme
     */
    public abstract boolean isInShape(Vec2 point);

    /**
     * @param point point
     * @return  le point de la forme le plus proche du point donné en paramètre
     */
    public abstract Vec2 closestPointToShape(Vec2 point);

    /**
     * @see Object#clone()
     */
    @Override
    public abstract Shape clone() throws CloneNotSupportedException;

    /**
     * @see Object#equals(Object)
     */
    @Override
    public abstract boolean equals(Object object);

    /**
     * @see Object#hashCode()
     */
    @Override
    public abstract int hashCode();

    /**
     * @see Object#toString()
     */
    @Override
    public abstract String toString();

    /**
     * Getter & Setter
     */
    public Vec2 getCenter(){
        return this.center;
    }
    public void setCenter(Vec2 newCenter) {
        this.center = newCenter;
    }
}
