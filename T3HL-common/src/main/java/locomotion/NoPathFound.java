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

import utils.math.Vec2;

/**
 * Exception levée lorsque le Pathfinding ne trouve pas de chemin
 *
 * @author rem
 */
public class NoPathFound extends Exception {

    /**
     * Point auquel on veut aller
     */
    private Vec2 aim;

    /**
     * Point à partir duquel on part
     */
    private Vec2 start;

    /**
     * @param aim   point de visé du pathfinder
     */
    public NoPathFound(Vec2 start, Vec2 aim) {
        super(start+" -> "+aim);
        this.start = start;
        this.aim = aim;
    }

    public Vec2 getStart() {
        return start;
    }

    /**
     * Getter
     */
    public Vec2 getAim() {
        return aim;
    }
}
