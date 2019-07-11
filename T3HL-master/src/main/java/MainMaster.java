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
import robot.Master;
import robot.Robot;
import scripts.Match;
import scripts.ScriptManager;
import scripts.ScriptManagerMaster;
import simulator.GraphicalInterface;
import simulator.SimulatedConnectionManager;
import simulator.SimulatorManager;
import simulator.SimulatorManagerLauncher;
import utils.ConfigData;
import utils.Container;
import utils.Log;
import utils.Offsets;
import utils.communication.SimulatorDebug;
import utils.container.ContainerException;
import utils.math.Vec2;
import utils.math.VectCartesian;

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
        Container container = Container.getInstance("Master");
        entryPoint(container, Master.class, ScriptManagerMaster.class);
    }

    @Override
    protected void initServices(Container container, Class<? extends Robot> robotClass, Class<? extends ScriptManager> scriptManagerClass) {
        super.initServices(container, robotClass, scriptManagerClass);
        if(container.getConfig().getBoolean(ConfigData.MODE_MONTHLERY)) {
            try {
                MontlheryController montlheryController = container.module(MontlheryController.class);
                montlheryController.start();
            } catch (ContainerException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void preLLConnection() throws ContainerException {
        waitForColorSwitch();

        if(container.getConfig().getBoolean(ConfigData.VISUALISATION) || container.getConfig().getBoolean(ConfigData.SIMULATION)) {
            SimulatorDebug debug = container.module(SimulatorDebug.class);
            if(container.getConfig().getBoolean(ConfigData.SIMULATION)) {
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
        if(container.getConfig().get(ConfigData.MODE_MONTHLERY)) {
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
        robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_DEPOT);
        robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_DEPOT);
        robot.setRotationSpeed(Speed.ULTRA_SLOW_ALL);

        boolean openTheGate = container.getConfig().get(ConfigData.OPEN_THE_GATE);
        if(openTheGate) {
            double offX;
            boolean symetry = container.getConfig().get(ConfigData.SYMETRY);
            if (symetry){
                offX= Offsets.get(ZDD_X_VIOLET);
            } else {
                offX= Offsets.get(ZDD_X_JAUNE);
            }
            Vec2 zoneDep = new VectCartesian(1500-191-65+offX-20,1040-300);

            robot.setPositionAndOrientation(zoneDep, Math.PI/2);
        } else {

            Vec2 newPos = new VectCartesian(1500-191, 350);
            robot.setPositionAndOrientation(newPos, Math.PI);

            if (container.getConfig().get(ConfigData.COULEUR).equals("violet")) {
                Log.TABLE.critical("Couleur pour le recalage : violet");
                robot.computeNewPositionAndOrientation(Sick.LOWER_LEFT_CORNER_TOWARDS_0);
            } else {
                Log.TABLE.critical("Couleur pour le recalage : jaune");
                robot.computeNewPositionAndOrientation(Sick.LOWER_RIGHT_CORNER_TOWARDS_PI);
            }


        /*robot.turn(Math.PI/2);
        robot.useActuator(ActuatorsOrder.ACTIVE_LA_POMPE_GAUCHE);
        robot.useActuator(ActuatorsOrder.DESACTIVE_ELECTROVANNE_GAUCHE);
        //robot.gotoPoint(new VectCartesian(1500-250,707));
        robot.gotoPoint(new VectCartesian(-410,410-78+15-4-5));
        robot.turn(Math.PI);

        robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_ASCENSEUR, true);
        robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_ACCELERATEUR, true);
        robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_ACCELERATEUR_DEPOT_7_PALETS, true);
        robot.useActuator(ActuatorsOrder.ACTIVE_ELECTROVANNE_GAUCHE);
        robot.useActuator(ActuatorsOrder.DESACTIVE_LA_POMPE_GAUCHE);*/
        }

        robot.setRotationSpeed(Speed.DEFAULT_SPEED);
        // la symétrie de la table permet de corriger le droit en gauche (bug ou feature?)

        if( ! container.getConfig().get(ConfigData.OPEN_THE_GATE)) {
            table.removeTemporaryObstacle(table.getPaletVertDroite());
        }
        table.removeTemporaryObstacle(table.getPaletRougeDroite());
        table.removeTemporaryObstacle(table.getPaletBleuDroite());
        table.removeAllChaosObstacles();


        try {
            double offX = container.getConfig().get(ConfigData.SYMETRY) ? Offsets.get(ZDD_X_VIOLET) : Offsets.get(ZDD_X_JAUNE);
            double offY = container.getConfig().get(ConfigData.SYMETRY) ? Offsets.get(ZDD_Y_VIOLET) : Offsets.get(ZDD_Y_JAUNE);

            Match match = container.module(Match.class);

            if( ! openTheGate) {
                Vec2 entryPos = new VectCartesian(1254+offX,900-198+offY);
                robot.gotoPoint(entryPos);
                Vec2 currentPosition = XYO.getRobotInstance().getPosition();
                double angleToStart;
                if (container.getConfig().get(ConfigData.SYMETRY)) {
                    angleToStart=Math.atan2(745- currentPosition.getY(),1000 - currentPosition.getX()-3);
                }
                else {
                    angleToStart=Math.atan2(750- currentPosition.getY(),1000 - currentPosition.getX());
                }
                System.out.println("angleToStart : " + angleToStart);
                robot.turn(angleToStart-Math.PI/2);
            }

            orderWrapper.waitJumper();
            /*Vec2 entryPos2 = new VectCartesian(1244+offX,900-195);
            robot.gotoPoint(entryPos2);
            robot.turn(angleToStart-Math.PI/2);*/

            //robot.gotoPoint(new VectCartesian(0,1000));


            match.execute(0);
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
            simulatorLauncher.setPathfollowerToShow(container.module(PathFollower.class), (int)ConfigData.LL_MASTER_SIMULATEUR.getDefaultValue());
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
            simulatorLauncher.setPathfollowerToShow(container.module(PathFollower.class), SimulatedConnectionManager.VISUALISATION_PORT);
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
