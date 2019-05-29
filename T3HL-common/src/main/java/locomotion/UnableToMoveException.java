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

package locomotion;

import data.XYO;
import utils.Log;

/**
 * Exceptions de locomotion
 *
 * @author rem
 */
public class UnableToMoveException extends Exception {

    /**
     * Point & Orientation visé par le robot
     */
    private XYO aim;

    /**
     * Raison du blocage
     */
    private UnableToMoveReason reason;

    /**
     * @param aim
     *              position et orientation visées par le robot
     * @param reason
     *              raison de blocage
     */
    public UnableToMoveException(XYO aim, UnableToMoveReason reason) {
        this("", aim, reason);
    }

    /**
     * @param s
     *              Message d'exception
     * @param aim
     *              position et orientation visées par le robot
     * @param reason
     *              raison de blocage
     */
    public UnableToMoveException(String s, XYO aim, UnableToMoveReason reason) {
        super(s+"["+reason.name()+"] Aim is "+aim.getPosition());
        this.aim = aim;
        this.reason = reason;
    }

    /**
     * Getters & Setters
     */
    public XYO getAim() {
        return aim;
    }
    public UnableToMoveReason getReason() {
        return reason;
    }
}
