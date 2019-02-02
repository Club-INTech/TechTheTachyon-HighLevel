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
import data.controlers.SensorControler;
import locomotion.UnableToMoveException;
import orders.OrderWrapper;
import robot.Master;
import scripts.Script;
import scripts.ScriptManager;
import scripts.ScriptManagerMaster;
import scripts.ScriptNamesMaster;
import utils.ConfigData;
import utils.Container;
import utils.container.ContainerException;
import utils.math.Vec2;
import utils.math.VectCartesian;

/**
 * @author nayth
 */
public class Main {

    private static Container container;
    private static ScriptManager scriptManager;
    private static ConnectionManager connectionManager;
    private static OrderWrapper orderWrapper;
    private static Listener listener;
    private static SensorControler sensorControler;
    private static Table table;
    private static Master robot;
    private static SimulatorManagerLauncher simulatorLauncher;

    public static void main(String[] args){
        initServices();
        if (container.getConfig().getBoolean(ConfigData.SIMULATION)) {
            initSimulator();
        }

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
            Script paletsx3 = ScriptNamesMaster.PALETS3.getScript();
            Script paletsx6 = ScriptNamesMaster.PALETS6.getScript();
            Script accelerateur = ScriptNamesMaster.ACCELERATEUR.getScript();
            Script zone_depart_palets = ScriptNamesMaster.PALETS_ZONE_DEPART.getScript();
            Script zone_chaos_palets = ScriptNamesMaster.PALETS_ZONE_CHAOS.getScript();
            Thread.sleep(2000);
            robot.setPositionAndOrientation(XYO.getRobotInstance().getPosition(), XYO.getRobotInstance().getOrientation());
            Thread.sleep(1000);

            Vec2[] tabVecs = {new VectCartesian(0,1000), new VectCartesian(500,500)};
            simulatorLauncher.setPointsToDraw(tabVecs);

            try {
                robot.moveToPoint(new VectCartesian(0,1000));
                robot.turn(Math.PI);
            } catch (UnableToMoveException e) {
                e.printStackTrace();
            }


            simulatorLauncher.addPointsToDraw(new Vec2[]{new VectCartesian(0,750), new VectCartesian(0,500), new VectCartesian(0, 250)});
            zone_depart_palets.goToThenExecute(1);
            simulatorLauncher.clearPointsToDraw();

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

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Container.resetInstance();
    }

    private static void initServices(){
        container = Container.getInstance("robot.Master");
        try {
            scriptManager = container.getService(ScriptManagerMaster.class);
            connectionManager = container.getService(ConnectionManager.class);
            orderWrapper = container.getService(OrderWrapper.class);
            listener = container.getService(Listener.class);
            listener.start();
            sensorControler = container.getService(SensorControler.class);
            sensorControler.start();
            table = container.getService(Table.class);
            table.initObstacles();
            robot = container.getService(Master.class);
        } catch (ContainerException e) {
            e.printStackTrace();
        }
    }

    private static void initSimulator(){
        simulatorLauncher = new SimulatorManagerLauncher();
        simulatorLauncher.setLLports(new int[]{(int)ConfigData.MASTER_LL_SIMULATEUR.getDefaultValue()});
        simulatorLauncher.setHLports(new int[]{(int)ConfigData.SLAVE_SIMULATEUR.getDefaultValue()});
        simulatorLauncher.setColorblindMode(false);
        simulatorLauncher.setSpeedFactor(1);
        simulatorLauncher.launch();
    }

}
