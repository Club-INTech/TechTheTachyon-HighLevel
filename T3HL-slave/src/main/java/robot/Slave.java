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

package robot;

import com.panneau.Panneau;
import data.controlers.PanneauService;
import locomotion.Locomotion;
import orders.OrderWrapper;
import orders.hooks.HookFactory;
import pfg.config.Config;
import utils.Container;
import utils.communication.SimulatorDebug;

/**
 * robot.Robot secondaire !
 *
 * @author rem
 */
public class Slave extends Robot {

    public Slave(Container container, Locomotion locomotion, OrderWrapper orderWrapper, HookFactory hookFactory, SimulatorDebug simulatorDebug, PanneauService panneauService) {
        super(container, locomotion, orderWrapper, hookFactory, simulatorDebug, panneauService);
        createRightElevator();
    }

    @Override
    public void updateConfig(Config config) {
        super.updateConfig(config);
    }
}
