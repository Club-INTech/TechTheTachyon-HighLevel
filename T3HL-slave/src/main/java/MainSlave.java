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

import data.XYO;
import locomotion.PathFollower;
import main.RobotEntryPoint;
import robot.Robots;
import robot.Slave;
import scripts.MatchSlave;
import scripts.ScriptManagerSlave;
import simulator.SimulatedConnectionManager;
import simulator.SimulatorManagerLauncher;
import utils.ConfigData;
import utils.HLInstance;
import utils.communication.SimulatorDebug;
import utils.container.ContainerException;
import utils.math.InternalVectCartesian;
import utils.math.Vec2;

/**
 * @author nayth, jglrxavpok
 */
public class MainSlave extends RobotEntryPoint {

    private SimulatorManagerLauncher simulatorLauncher;

    public static void main(String[] args) throws ContainerException {
        new MainSlave().start();
    }

    private void start() throws ContainerException {
        HLInstance hl = HLInstance.get(Robots.SECONDARY);
        entryPoint(hl, Slave.class, ScriptManagerSlave.class);
    }

    @Override
    protected void preLLConnection() throws ContainerException {
        waitForColorSwitch();
        if(hl.getConfig().getBoolean(ConfigData.VISUALISATION) || hl.getConfig().getBoolean(ConfigData.SIMULATION)) {
            SimulatorDebug debug = hl.module(SimulatorDebug.class);
            if(hl.getConfig().getBoolean(ConfigData.SIMULATION)) {
                // TODO: LL Slave Simulateur?
                debug.setSenderPort((int)ConfigData.LL_MASTER_SIMULATEUR.getDefaultValue());
            } else {
                debug.setSenderPort(SimulatedConnectionManager.VISUALISATION_PORT);
            }
        }

        if (hl.getConfig().getBoolean(ConfigData.SIMULATION)) {
            initSimulator();
        } else if(hl.getConfig().getBoolean(ConfigData.VISUALISATION)) {
            initVisualisateur();
        }
    }

    @Override
    protected void act() {
        Vec2 newPos = new InternalVectCartesian(1500 -297,400);
        // position de démarrage, on s'oriente pour pouvoir prendre le palet rouge

        //Pour aller à la bonne position de départ
        if(hl.getConfig().get(ConfigData.SYMETRY)) { // symétrie
            XYO.getRobotInstance().update(newPos.getX(), newPos.getY(), Math.PI/2);
            robot.setPositionAndOrientation(newPos, Math.PI/2);
        }
        else {
            //s'oriente vers PI/2 avant de se recaler
            XYO.getRobotInstance().update(newPos.getX(), newPos.getY(), -Math.PI/2);
            //XYO.getRobotInstance().update(newPos.getX(), newPos.getY(), -Math.PI/2);
            robot.setPositionAndOrientation(newPos, -Math.PI/2);
        }

        orderWrapper.waitJumper();

        try {
            hl.module(MatchSlave.class).execute(0);
        } catch (ContainerException e) {
            e.printStackTrace();
        }
    }

    private void initSimulator(){
        simulatorLauncher = new SimulatorManagerLauncher();

        //On set tous les LL qui sont simulés
        simulatorLauncher.setLLMasterPort((int)ConfigData.LL_MASTER_SIMULATEUR.getDefaultValue());

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
        simulatorLauncher.setSpeedFactor(2);
        simulatorLauncher.setIsSimulatingObstacleWithMouse(true);
        simulatorLauncher.launch();
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        simulatorLauncher.waitForLaunchCompletion();
    }

    private void initVisualisateur(){
        simulatorLauncher = new SimulatorManagerLauncher();

        simulatorLauncher.setVisualisationMode(Slave.class);

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
    }

}
