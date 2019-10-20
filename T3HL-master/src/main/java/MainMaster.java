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

import locomotion.PathFollower;
import main.RobotEntryPoint;
import orders.Speed;
import robot.Master;
import robot.Robot;
import robot.Robots;
import scripts.Match;
import scripts.ScriptManager;
import scripts.ScriptManagerMaster;
import simulator.GraphicalInterface;
import simulator.SimulatedConnectionManager;
import simulator.SimulatorManager;
import simulator.SimulatorManagerLauncher;
import utils.ConfigData;
import utils.HLInstance;
import utils.Offsets;
import utils.communication.SimulatorDebug;
import utils.container.ContainerException;

import java.util.concurrent.TimeUnit;

/**
 * @author nayth, jglrxavpok
 */

public class MainMaster extends RobotEntryPoint implements Offsets {

    private SimulatorManagerLauncher simulatorLauncher;
    // Regardez, c'est GLaDOS!
    private GraphicalInterface interfaceGraphique;

    public static void main(String[] args) throws ContainerException {
        new MainMaster().start();
    }

    private void start() throws ContainerException {
        HLInstance hl = HLInstance.get(Robots.MAIN);
        entryPoint(hl, Master.class, ScriptManagerMaster.class);
    }

    @Override
    protected void initServices(HLInstance hl, Class<? extends Robot> robotClass, Class<? extends ScriptManager> scriptManagerClass) {
        super.initServices(hl, robotClass, scriptManagerClass);
        if(hl.getConfig().get(ConfigData.MODE_MONTHLERY)) {
            try {
                MontlheryController montlheryController = hl.module(MontlheryController.class);
                montlheryController.start();
            } catch (ContainerException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void preLLConnection() throws ContainerException {
        waitForColorSwitch();

        if(hl.getConfig().get(ConfigData.VISUALISATION) || hl.getConfig().get(ConfigData.SIMULATION)) {
            SimulatorDebug debug = hl.module(SimulatorDebug.class);
            if(hl.getConfig().get(ConfigData.SIMULATION)) {
                debug.setSenderPort((int)ConfigData.LL_MASTER_SIMULATEUR.getDefaultValue());
            } else {
                debug.setSenderPort(SimulatedConnectionManager.VISUALISATION_PORT);
            }
        }

        if (hl.getConfig().get(ConfigData.SIMULATION)) {
            initSimulator();
        } else if(hl.getConfig().get(ConfigData.VISUALISATION)) {
            initVisualisateur();
        }
    }


    @Override
    protected void act() {
        if(hl.getConfig().get(ConfigData.MODE_MONTHLERY)) {
            while(true) {
                try {
                    TimeUnit.SECONDS.sleep(5);
                } catch (InterruptedException e) {
                    break;
                }
            }
            return;
        }
        robot.increaseScore(5); // présence de l'expérience
        robot.setRotationSpeed(Speed.ULTRA_SLOW_ALL);

        try {
            Match match = hl.module(Match.class);
            robot.setPositionAndOrientation(match.entryPosition(0), 0.0);

            orderWrapper.waitJumper();
            match.execute();
        } catch (ContainerException e) {
            e.printStackTrace();
        }
    }

    private void initSimulator(){
        simulatorLauncher = new SimulatorManagerLauncher();

        //On set tous les LL qui sont simulés
        simulatorLauncher.setLLMasterPort((int)ConfigData.LL_MASTER_SIMULATEUR.getDefaultValue());

        simulatorLauncher.setSpeedFactor(2f);

        //On set tous les HL qui recevront des messages
        // FIXME/TODO simulatorLauncher.setHLSlavePort((int)ConfigData.HL_SLAVE_SIMULATEUR.getDefaultValue());

        //On set le lidar s'il ne tourne pas
        //simulatorLauncher.setLidarPort((int) ConfigData.LIDAR_DATA_PORT.getDefaultValue());

        try {
            simulatorLauncher.setPathfollowerToShow(hl.module(PathFollower.class), (int)ConfigData.LL_MASTER_SIMULATEUR.getDefaultValue());
        } catch (ContainerException e) {
            e.printStackTrace();
        }
        simulatorLauncher.setColorblindMode(true);
        simulatorLauncher.setIsSimulatingObstacleWithMouse(true);
        simulatorLauncher.launch();
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        simulatorLauncher.waitForLaunchCompletion();
        SimulatorManager simulatorManager = simulatorLauncher.getSimulatorManager();
        interfaceGraphique = simulatorManager.getGraphicalInterface();
    }

    private void initVisualisateur(){
        simulatorLauncher = new SimulatorManagerLauncher();

        simulatorLauncher.setVisualisationMode(Master.class);

        try {
            simulatorLauncher.setPathfollowerToShow(hl.module(PathFollower.class), SimulatedConnectionManager.VISUALISATION_PORT);
        } catch (ContainerException e) {
            e.printStackTrace();
        }
        //On set le lidar s'il ne tourne pas
        //simulatorLauncher.setLidarPort((int) ConfigData.LIDAR_DATA_PORT.getDefaultValue());

        simulatorLauncher.setColorblindMode(true);
        simulatorLauncher.setIsSimulatingObstacleWithMouse(true);
        simulatorLauncher.launch();
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        simulatorLauncher.waitForLaunchCompletion();
        SimulatorManager simulatorManager = simulatorLauncher.getSimulatorManager();
        interfaceGraphique = simulatorManager.getGraphicalInterface();
    }

}
