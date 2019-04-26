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

package data;

/**
 * Correspond à une base de données des variables à propos du main.robot
 *
 * @author william
 */
public enum RobotState {

    //Exemples
    BRAS_AVANT_DEPLOYE(true),
    BRAS_ARRIERE_DEPLOYE(true),
    CURRENT_SCRIPT_NAME(""),
    CURRENT_SCRIPT_VERSION(0),
    ;
    private Object valueObject;
    RobotState(Object valueObject){
        this.valueObject=valueObject;
    }

    /** Renvoie la valeur de la variable */
    public synchronized Object getData(){
        return this.valueObject;
    }

    /** Affecte une valeur à la variable */
    public synchronized void setData(Object value){
        this.valueObject=value;
    }
}
