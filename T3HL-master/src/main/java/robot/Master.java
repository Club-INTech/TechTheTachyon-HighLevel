/**
 * Copyright (c) 2018, INTech.
 * this file is part of INTech's HighLevel.
 * <p>
 * INTech's HighLevel is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * INTech's HighLevel is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with it.  If not, see <http://www.gnu.org/licenses/>.
 **/

package robot;

import data.controlers.PanneauModule;
import locomotion.Locomotion;
import orders.OrderWrapper;
import orders.hooks.HookFactory;
import utils.HLInstance;
import utils.communication.SimulatorDebug;

/**
 * robot.Robot principale : on rassemble ici tout ce qui est unique au robot principale
 *
 * @author rem
 */
public class Master extends Robot {

    public Master(HLInstance hl, Locomotion locomotion, OrderWrapper orderWrapper, HookFactory hookFactory, SimulatorDebug simulatorDebug, PanneauModule panneauService) {
        super(hl, locomotion, orderWrapper, hookFactory, simulatorDebug, panneauService);
        createLeftElevator();
        createRightElevator();
    }

}
