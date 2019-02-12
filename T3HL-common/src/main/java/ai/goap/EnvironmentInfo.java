/**
 * Copyright (c) 2019, INTech.
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

package ai.goap;

import ai.SpectreRobot;
import data.Table;
import data.XYO;
import utils.math.Vec2;

import java.util.HashMap;
import java.util.Map;

/**
 * Classe représentant toutes les informations sur l'environnement d'un agent. Utilisé par les actions et les états pour savoir ce qu'ils doivent faire
 */
public class EnvironmentInfo {
    private final Map<String, Object> state;
    private final XYO xyo;
    private final SpectreRobot robot;

    public EnvironmentInfo(XYO xyo, Map<String, Object> state, SpectreRobot robot) {
        this.state = state;
        this.xyo = xyo;
        this.robot = robot;
    }

    public Map<String, Object> getState() {
        return state;
    }

    public XYO getXYO() {
        return xyo;
    }

    public Vec2 getCurrentPosition() {
        return xyo.getPosition();
    }

    public double getCurrentAngle() {
        return xyo.getOrientation();
    }

    public EnvironmentInfo copyWithEffects(Map<String, Object> effects) {
        Map<String, Object> newState = new HashMap<>(state);
        newState.putAll(effects);
        XYO newXYO = null;
        if(xyo != null) {
            newXYO = new XYO(xyo.getPosition(), xyo.getOrientation());
        }
        return new EnvironmentInfo(newXYO, newState, robot == null ? null : robot.deepCopy());
    }

    public boolean isMetByState(EnvironmentInfo other) {
        for (Map.Entry<String, Object> entry : state.entrySet()) {
            if(!other.state.get(entry.getKey()).equals(entry.getValue()))
                return false;
        }
        return true;
    }

    public SpectreRobot getSpectre() {
        return robot;
    }
}
