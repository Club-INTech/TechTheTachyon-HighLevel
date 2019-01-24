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

import connection.ConnectionManager;
import data.Table;
import data.controlers.Listener;
import orders.OrderWrapper;
import robot.Master;
import scripts.*;
import utils.ConfigData;
import utils.Container;
import utils.container.ContainerException;
import utils.math.VectCartesian;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @author nayth
 */
public class Main {

    public static void main(String[] args){
        Container container = Container.getInstance("robot.Master");

        SimulatorManagerLauncher launcher = new SimulatorManagerLauncher();
        launcher.setLLports(new int[]{(int)ConfigData.MASTER_LL_SIMULATEUR.getDefaultValue()});
        launcher.setColorblindMode(true);
        launcher.launchSimulator();


        boolean isMaster = container.getConfig().getBoolean(ConfigData.MASTER);
        try {
            ScriptManagerMaster scriptManager = container.getService(ScriptManagerMaster.class);
            ConnectionManager connectionManager = container.getService(ConnectionManager.class);
            OrderWrapper orderWrapper = container.getService(OrderWrapper.class);
            Listener listener = container.getService(Listener.class);
            listener.start();
            Thread.sleep(2000);

            Master robot = container.getService(Master.class);
            Table table = container.getService(Table.class);

            orderWrapper.moveToPoint(new VectCartesian(1000,1000));

            //Paletsx3 paletx3 = scriptManager.getScript();
        } catch (ContainerException | InterruptedException e) {
            e.printStackTrace();
        }
        //Container.resetInstance();
    }
}
