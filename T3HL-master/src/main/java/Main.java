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
import scripts.Script;
import scripts.ScriptManagerMaster;
import scripts.ScriptNamesMaster;
import utils.ConfigData;
import utils.Container;
import utils.container.ContainerException;

/**
 * @author nayth
 */
public class Main {

    public static void main(String[] args){
        Container container;
        String hierarchy;
        /*
        try {
            hierarchy = Files.readAllLines(Paths.get("../config/hierarchy.txt")).get(0);
        } catch (IOException e) {
            hierarchy=null;
            e.printStackTrace();
        }
        */
        container = Container.getInstance("robot.Master");
        ScriptManagerMaster scriptManager;
        Script paletsx3 = ScriptNamesMaster.PALETS3.getScript();
        Script paletsx6 = ScriptNamesMaster.PALETS6.getScript();
        Script accelerateur = ScriptNamesMaster.ACCELERATEUR.getScript();
        Script zone_depart_palets = ScriptNamesMaster.PALETS_ZONE_DEPART.getScript();
        Script zone_chaos_palets = ScriptNamesMaster.PALETS_ZONE_CHAOS.getScript();


        SimulatorManagerLauncher launcher = new SimulatorManagerLauncher();
        launcher.setLLports(new int[]{(int)ConfigData.MASTER_LL_SIMULATEUR.getDefaultValue()});
        launcher.setColorblindMode(true);
        launcher.launchSimulator();

        boolean isMaster = container.getConfig().getBoolean(ConfigData.MASTER);
        try {
            scriptManager = container.getService(ScriptManagerMaster.class);
            ConnectionManager connectionManager = container.getService(ConnectionManager.class);
            OrderWrapper orderWrapper = container.getService(OrderWrapper.class);
            Listener listener = container.getService(Listener.class);
            listener.start();
            Thread.sleep(2000);

            Master robot = container.getService(Master.class);
            Table table = container.getService(Table.class);

            table.initObstacles();

            zone_depart_palets.goToThenExecute(1);
            zone_chaos_palets.goToThenExecute(1);
            paletsx6.goToThenExecute(1);
            paletsx3.goToThenExecute(1);
            accelerateur.goToThenExecute(1);

        } catch (ContainerException | InterruptedException e) {
            e.printStackTrace();
        }
        Container.resetInstance();
    }
}
