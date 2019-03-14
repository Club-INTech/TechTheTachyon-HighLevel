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

package data.table;

import utils.math.Segment;
import utils.math.Shape;
import utils.math.Vec2;

/**
 * Classe abstraite définissant la représentation d'un obstacle dans le code.
 *
 * @author rem
 */
public abstract class Obstacle {
    /**
     * Forme de l'obstacle
     */
    protected Shape shape;

    /**
     * Construit un obstacle
     */
    protected Obstacle(Shape shape) {
        this.shape = shape;
    }

    /**
     * Méthode utile pour les collision
     * @return  true si le point se trouve dans l'obstacle
     */
    public boolean isInObstacle(Vec2 point) {
        return this.shape.isInShape(point);
    }

    /**
     * Méthode servant à construire et mettre à jour le Graphe
     * @param segment   le segment à tester
     * @return  true si le segment intersecte l'obstacle
     */
    public boolean intersect(Segment segment) {
        return this.shape.intersect(segment);
    }

    /**
     * @see Object#clone()
     */
    @Override
    public abstract Obstacle clone() throws CloneNotSupportedException;

    /**
     * @see Object#equals(Object)
     */
    @Override
    public boolean equals(Object object) {
        if (object instanceof Obstacle) {
            return this.shape.equals(((Obstacle) object).shape);
        }
        return false;
    }

    /**
     * @see Object#hashCode()
     */
    @Override
    public int hashCode() {
        return this.shape.hashCode();
    }

    /**
     * @see Object#toString()
     */
    @Override
    public abstract String toString();

    /**
     * Getter
     */
    public Vec2 getPosition() {
        return shape.getCenter();
    }
    public void setPosition(Vec2 position){
        this.shape.setCenter(position);
    }
    public Shape getShape() {
        return shape;
    }
}
