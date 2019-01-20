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

package data.table;

import utils.math.Rectangle;
import utils.math.Vec2;

/**
 * Classe implémentant les obstacles rectangulaires
 *
 * @author rem
 */
public class StillRectangularObstacle extends Obstacle {
    /**
     * Construit un obstacle rectangulaire
     * @param rectangle rectangle représentant l'obstacle
     */
    public StillRectangularObstacle(Rectangle rectangle) {
        super(rectangle);
    }

    /**
     * Construit un obstacle circulaire à partir d'un centre et des dimensions
     * @param center    centre du rectangle
     * @param length    longueur du rectangle
     * @param width     largeur du rectangle
     */
    public StillRectangularObstacle(Vec2 center, int length, int width) {
        super(new Rectangle((center), length, width));
    }

    /**
     * @see Obstacle#clone()
     */
    @Override
    public Obstacle clone() throws CloneNotSupportedException {
        return new StillRectangularObstacle((Rectangle) this.shape.clone());
    }

    /**
     * @see Obstacle#toString()
     */
    @Override
    public String toString() {
        return "Obstacle fixe rectangulaire " + shape.toString();
    }
}
