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

import com.pi4j.io.gpio.*;
import connection.ConnectionManager;
import data.Table;
import data.XYO;
import data.controlers.Listener;
import data.table.Obstacle;
import orders.OrderWrapper;
import robot.Master;
import scripts.Script;
import scripts.ScriptManager;
import scripts.ScriptManagerMaster;
import scripts.ScriptNamesMaster;
import utils.ConfigData;
import utils.Container;
import utils.container.ContainerException;
import utils.math.VectCartesian;

import java.util.ArrayList;

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


        SimulatorManagerLauncher launcher = new SimulatorManagerLauncher();
        launcher.setLLports(new int[]{(int)ConfigData.MASTER_LL_SIMULATEUR.getDefaultValue()});
        launcher.setHLports(new int[]{(int)ConfigData.SLAVE_SIMULATEUR.getDefaultValue()});
        launcher.setColorblindMode(true);
        launcher.launchSimulator();

        /**
         * Pour l'électron
         */
        //On check l'username pour savoir si on est sur la Raspberry Pi
        if (System.getProperty("user.name").equals("pi")){
            final GpioController gpio = GpioFactory.getInstance();
            final GpioPinDigitalOutput pin = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_01, "ESP32", PinState.LOW);
            pin.setShutdownOptions(true, PinState.LOW);
        }


        boolean isMaster = container.getConfig().getBoolean(ConfigData.MASTER);
        try {
            ScriptManager scriptManager = container.getService(ScriptManagerMaster.class);
            Table table = container.getService(Table.class);
            table.initObstacles();

            Script paletsx3 = ScriptNamesMaster.PALETS3.getScript();
            Script paletsx6 = ScriptNamesMaster.PALETS6.getScript();
            Script accelerateur = ScriptNamesMaster.ACCELERATEUR.getScript();
            Script zone_depart_palets = ScriptNamesMaster.PALETS_ZONE_DEPART.getScript();
            Script zone_chaos_palets = ScriptNamesMaster.PALETS_ZONE_CHAOS.getScript();
            ConnectionManager connectionManager = container.getService(ConnectionManager.class);
            OrderWrapper orderWrapper = container.getService(OrderWrapper.class);
            Listener listener = container.getService(Listener.class);
            listener.start();
            Thread.sleep(2000);

            Master robot = container.getService(Master.class);

            robot.setPositionAndOrientation(XYO.getRobotInstance().getPosition(), XYO.getRobotInstance().getOrientation());
            orderWrapper.moveToPoint(new VectCartesian(1000,1000));
            orderWrapper.turn(Math.PI);
            zone_depart_palets.goToThenExecute(1);

            table.removeFixedObstacle(table.paletRougeDroite);
            table.removeFixedObstacle(table.paletVertDroite);


            zone_chaos_palets.goToThenExecute(1);

            /**
             * Si tout les palets de la zone de chaos ont été récupérer
             */
            table.removeFixedObstacle(table.zoneChaosDroite);

            paletsx6.goToThenExecute(1);
            paletsx3.goToThenExecute(1);
            accelerateur.goToThenExecute(1);

        } catch (ContainerException | InterruptedException e) {
            e.printStackTrace();
        }
        Container.resetInstance();
    }
}
