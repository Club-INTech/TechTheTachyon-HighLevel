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

import utils.math.Circle;
import utils.math.Vec2;

/**
 * Classe représentant les obstacles circulaires
 *
 * @author rem
 */
public class StillCircularObstacle extends Obstacle {
    /**
     * Constructeur position & rayon
     *
     * @param position  centre du cercle
     * @param ray   rayon du cercle
     */
    public StillCircularObstacle(Vec2 position, int ray) {
        super(new Circle(position, ray));
    }

    /**
     * Constructeur cercle
     * @param circle    cercle
     */
    public StillCircularObstacle(Circle circle) {
        super(circle);
    }

    /**
     * @see Obstacle#clone()
     */
    @Override
    public Obstacle clone() throws CloneNotSupportedException {
        return new StillCircularObstacle((Circle) this.shape.clone());
    }

    /**
     * @see Obstacle#toString()
     */
    @Override
    public String toString() {
        return "Obstacle fixe circulaire " + shape.toString();
    }
}
