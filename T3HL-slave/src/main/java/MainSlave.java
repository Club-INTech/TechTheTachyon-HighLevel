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

import data.Sick;
import data.XYO;
import locomotion.PathFollower;
import locomotion.UnableToMoveException;
import main.RobotEntryPoint;
import orders.Speed;
import orders.order.ActuatorsOrder;
import robot.Slave;
import scripts.MatchSlave;
import scripts.PaletsX6Slave;
import scripts.ScriptManagerSlave;
import scripts.ScriptNamesSlave;
import simulator.GraphicalInterface;
import simulator.SimulatedConnectionManager;
import simulator.SimulatorManager;
import simulator.SimulatorManagerLauncher;
import utils.ConfigData;
import utils.Container;
import utils.communication.SimulatorDebug;
import utils.container.ContainerException;
import utils.math.Calculs;
import utils.math.Vec2;
import utils.math.VectCartesian;

/**
 * @author nayth, jglrxavpok
 */
public class MainSlave extends RobotEntryPoint {

    private SimulatorManagerLauncher simulatorLauncher;
    // Regardez, c'est GLaDOS!
    private GraphicalInterface interfaceGraphique;

    public static void main(String[] args) throws ContainerException {
        new MainSlave().start();
    }

    private void start() throws ContainerException {
        Container container = Container.getInstance("Slave");
        entryPoint(container, Slave.class, ScriptManagerSlave.class);
    }

    @Override
    protected void preLLConnection() throws ContainerException {
        if(container.getConfig().getBoolean(ConfigData.VISUALISATION) || container.getConfig().getBoolean(ConfigData.SIMULATION)) {
            SimulatorDebug debug = container.getService(SimulatorDebug.class);
            if(container.getConfig().getBoolean(ConfigData.SIMULATION)) {
                // TODO: LL Slave Simulateur?
                debug.setSenderPort((int)ConfigData.LL_MASTER_SIMULATEUR.getDefaultValue());
            } else {
                debug.setSenderPort(SimulatedConnectionManager.VISUALISATION_PORT);
            }
        }

        if (container.getConfig().getBoolean(ConfigData.SIMULATION)) {
            initSimulator();
        } else if(container.getConfig().getBoolean(ConfigData.VISUALISATION)) {
            initVisualisateur();
        }
    }

    @Override
    protected void act() throws UnableToMoveException {


        table.removeAllChaosObstacles();
        robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DU_SECONDAIRE_A_LA_POSITION_ASCENSEUR,true);

        robot.setRotationSpeed(Speed.SLOW_ALL);
        Vec2 newPos = new VectCartesian(1500-242, 145+58);
        // position de démarrage, on s'oriente pour pouvoir prendre le palet rouge
       // Vec2 pos = new VectCartesian(1500-300-10, 300+100+10); ça change en symétrie
        Vec2 pos = new VectCartesian(1500-300-10, 500);
        Vec2 posSansSick = new VectCartesian(1399, 450);


        double targetAngle;
        //Pour aller à la bonne position de départ
        /*if(container.getConfig().getString(ConfigData.COULEUR).equals("violet")) { // symétrie
            XYO.getRobotInstance().update(newPos.getX(), newPos.getY(), Math.PI/2);
            //robot.setPositionAndOrientation(newPos, Math.PI);
            targetAngle = Math.PI / 2;
        } else {
            //s'oriente vers PI/2 avant de se recaler
            //XYO.getRobotInstance().update(newPos.getX(), newPos.getY(), Math.PI);
            XYO.getRobotInstance().update(newPos.getX(), newPos.getY(), Math.PI/2);
            //robot.setPositionAndOrientation(newPos, Math.PI);
            targetAngle = -Math.PI/2;
        }
        robot.computeNewPositionAndOrientation(Sick.SECONDAIRE);
/*
        robot.gotoPoint(pos);
        /*
        for (int i = 0; i < 5; i++) {
            if (XYO.getRobotInstance().getPosition().distanceTo(pos) >= 5) {
                robot.gotoPoint(pos);
            } else {
                break;
            }
        }
        */
        //Pour se tourner vers la bonne orientation
        /*
        for (int i = 0; i < 5; i++) {
            if(Math.abs(Calculs.modulo(XYO.getRobotInstance().getOrientation()-targetAngle, Math.PI)) >= 2*Math.PI/180.0f ) { //2°
                robot.turn(targetAngle);
            } else {
                break;
            }
        }
        */

        robot.setPositionAndOrientation(pos, -Math.PI/2);
        orderWrapper.waitJumper();

        try {
            container.getService(MatchSlave.class).goToThenExecute(0);
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
            simulatorLauncher.setPathfollowerToShow(container.getService(PathFollower.class), (int)ConfigData.LL_MASTER_SIMULATEUR.getDefaultValue());
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
        SimulatorManager simulatorManager = simulatorLauncher.getSimulatorManager();
        interfaceGraphique = simulatorManager.getGraphicalInterface();
    }

    private void initVisualisateur(){
        simulatorLauncher = new SimulatorManagerLauncher();

        simulatorLauncher.setVisualisationMode(Slave.class);

        try {
            simulatorLauncher.setPathfollowerToShow(container.getService(PathFollower.class), SimulatedConnectionManager.VISUALISATION_PORT);
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
