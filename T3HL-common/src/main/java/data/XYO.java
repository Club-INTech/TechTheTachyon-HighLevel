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

import utils.Log;
import utils.math.Vec2;
import utils.math.InternalVectCartesian;

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
        Log.POSITION.debug(String.format("XYO updated : %d, %d, %5f", x, y, o), 1);
        this.position.setXY(x, y);
        this.orientation = o;
    }

    /**
     * Getters
     */
    public static XYO getRobotInstance() {
        if (robotXYO == null) {
            robotXYO = new XYO(new InternalVectCartesian(200000,200000), Math.PI);
        }
        return robotXYO;
    }
    public static XYO getBuddyInstance() {
        // c'est instancié la première fois qu'on veut y accéder.
        // la position est mise à jour lors des communications avec le buddy
        if (buddyXYO == null) {
            buddyXYO = new XYO(new InternalVectCartesian(100000, 100000), 0);
        }
        return buddyXYO;
    }
    public Vec2 getPosition() {
        return position;
    }
    public double getOrientation() {
        return orientation;
    }

    @Override
    public String toString() {
        return String.format("XYO (x,y)=%s, o=%6f", this.position, this.orientation);
    }

    @Override
    public XYO clone() {
        return new XYO(this.getPosition().clone(), this.orientation);
    }
}
