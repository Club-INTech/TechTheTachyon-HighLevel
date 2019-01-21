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
 * On fait des calculs à l'aide des méthodes de cette classe
 *
 * @author yousra
 */
public class Calculs {

    /**Cette méthode retourne le nombre congru à un nombre modulo un module
     * @param number nombre dont on veut calculer le nombre qui lui est congru
     * @param module le module*/

    public static double modulo(double number, double module){
        number = number%(2*module);
        if (number > module){
            number -= 2*module;
        }else if(number < -module){
            number += 2*module;
        }
        return number;
    }

    /**Cette méthode détermine si x est bien entre y et z
     * @param x nombre dont on veut savoir s'il est entre les deux autres nombres spécifiés
     *@param y plus petit nombre
     * @param z plus grand nombre*/

    public static boolean isBetween(double x, double y, double z) {
        //On inverse y et z si jamais on se trompe de paramètres
        if(y>z)
        {
            double t=z;
            z=y;
            y=t;
        }
        return x>=y && x<=z;
    }
}
