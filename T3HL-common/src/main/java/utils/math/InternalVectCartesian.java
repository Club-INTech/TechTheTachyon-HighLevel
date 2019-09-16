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
 * Classe héritante de Vec2 dont la fonction est de différencier son constructeur de Vec2
 * @see Vec2
 *
 * @author yousra
 */
public class InternalVectCartesian extends Vec2 {

    /**
     * Constructeur d'un vecteur cartésien pour qu'il n'y ait pas de confusion avec les vecteurs polaires quand on débug
     * @param x
     * @param y
     */
    public InternalVectCartesian(int x, int y){
        super(x, y);
    }

    /**
     * Constructeur d'un vecteur cartésien pour qu'il n'y ait pas de confusion avec les vecteurs polaires quand on débug
     * @param x
     * @param y
     */
    public InternalVectCartesian(float x, float y) {
        super(Math.round(x), Math.round(y));
    }

    public InternalVectCartesian(double x, double y) {
        this((int)Math.round(x), (int)Math.round(y));
    }
}
