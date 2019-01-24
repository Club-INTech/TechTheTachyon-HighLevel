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
 * @see VectCartesian
 * @see Vec2
 *
 * @author youra
 */
public class VectPolar extends Vec2 {

    /**
     * Constructeur d'un vecteur polaire pour qu'il n'y ait pas de confusion avec les vecteurs cartésiens
     * @param r
     * @param a
     */
    public VectPolar(double r, double a){
        super(r,a);
    }
}
