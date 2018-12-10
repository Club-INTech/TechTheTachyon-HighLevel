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

import utils.math.Vec2;
import utils.math.VectCartesian;

/**
 * Classe représentant les positions des robots (doublons)
 *
 * @author rem
 */
public class XYO {
    /**
     * Instance du robot
     */
    private static XYO robotXYO = null;

    /**
     * Instance du buddy
     */
    private static XYO buddyXYO = null;

    /**
     * Position
     */
    private Vec2 position;

    /**
     * Orientation
     */
    private double orientation;

    /**
     * Constructeur
     */
    public XYO(Vec2 position, double orientation) {
        this.position = position;
        this.orientation = orientation;
    }

    /**
     * Mise à jour des XYO
     * package-private
     */
    public void update(int x, int y, double o) {
        this.position.setXY(x, y);
        this.orientation = o;
    }

    /**
     * Getters
     */
    public static XYO getRobotInstance() {
        if (robotXYO == null) {
            // TODO : Remplir avec l'entryPosition !
        }
        return robotXYO;
    }
    public static XYO getBuddyInstance() {
        // TODO : Décider comment ca fonctionne ! et virer l'instanciation qui sert pour les tests
        if (buddyXYO == null) {
            buddyXYO = new XYO(new VectCartesian(0, 0), 0);
        }
        return buddyXYO;
    }
    public Vec2 getPosition() {
        return position;
    }
    public double getOrientation() {
        return orientation;
    }
}
