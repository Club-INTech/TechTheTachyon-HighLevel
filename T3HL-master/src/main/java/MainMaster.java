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

import com.panneau.Panneau;
import com.panneau.TooManyDigitsException;
import data.Sick;
import data.XYO;
import locomotion.PathFollower;
import locomotion.UnableToMoveException;
import main.RobotEntryPoint;
import orders.SymmetrizedActuatorOrderMap;
import orders.order.ActuatorsOrder;
import robot.Master;
import scripts.Match;
import scripts.ScriptManagerMaster;
import simulator.GraphicalInterface;
import simulator.SimulatedConnectionManager;
import simulator.SimulatorManager;
import simulator.SimulatorManagerLauncher;
import utils.ConfigData;
import utils.Container;
import utils.Log;
import utils.communication.SimulatorDebug;
import utils.container.ContainerException;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @author nayth, jglrxavpok
 */
public class MainMaster extends RobotEntryPoint {

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
    protected void preLLConnection() throws ContainerException {
        waitForColorSwitch();

        if(container.getConfig().getBoolean(ConfigData.VISUALISATION) || container.getConfig().getBoolean(ConfigData.SIMULATION)) {
            SimulatorDebug debug = container.getService(SimulatorDebug.class);
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

    private void waitForColorSwitch() {
        if( ! container.getConfig().getBoolean(ConfigData.USING_PANEL)) {
            panneauService.setPanel(null);
            return;
        }
        Panneau panneau = panneauService.getPanneau();
        if(panneau != null) {
            try {
                try {
                    panneau.printScore(5005);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Panneau.teamColor initialColor = panneau.getTeamColor();
                // on attend une première activation du switch
                while(initialColor == panneau.getTeamColor()) {
                    try {
                        panneau.printScore(5005);
                        TimeUnit.MILLISECONDS.sleep(100);
                        panneau.printScore(550);
                        TimeUnit.MILLISECONDS.sleep(100);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                initialColor = panneau.getTeamColor();
                // on attend une deuxième activation du switch ou 5s
                long delay = 5000;
                long start = System.currentTimeMillis();
                while(initialColor == panneau.getTeamColor() && (System.currentTimeMillis() - start) <= delay) {
                    try {
                        panneau.printScore((int) (delay-(System.currentTimeMillis()-start)));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    TimeUnit.MILLISECONDS.sleep(1);
                }
                Log.STRATEGY.warning("Couleur: "+panneau.getTeamColor());
            } catch (InterruptedException | TooManyDigitsException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void act() throws UnableToMoveException {
        XYO.getRobotInstance().update(1500-191, 350, Math.PI );
        robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_ASCENSEUR);
        robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_ASCENSEUR);
        robot.setPositionAndOrientation(XYO.getRobotInstance().getPosition(), XYO.getRobotInstance().getOrientation());

        for (int i=0; i<2; i++) {
            if (container.getConfig().getString(ConfigData.COULEUR).equals("violet")) {
                robot.computeNewPositionAndOrientation(Sick.LOWER_LEFT_CORNER_TOWARDS_0);
            } else {
                robot.computeNewPositionAndOrientation(Sick.LOWER_RIGHT_CORNER_TOWARDS_PI);
            }
            robot.turn(Math.PI);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        robot.turn(Math.PI/2);
        // la symétrie de la table permet de corriger le droit en gauche (bug ou feature?)
        table.removeTemporaryObstacle(table.getPaletRougeDroite());
        table.removeTemporaryObstacle(table.getPaletVertDroite());
        table.removeTemporaryObstacle(table.getPaletBleuDroite());
        table.removeAllChaosObstacles();
        orderWrapper.waitJumper();

        /*
        //TEST ROTATION
        for (int i=0; i<100; i++) {
            robot.turn(Math.PI);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            robot.turn(0);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }*/


      /* //TEST TRANSLATION
        for (int i=0; i<100; i++) {
            robot.moveLengthwise(100, false);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            robot.moveLengthwise(-100, false);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        int i=0;
        while(i<1000000000){
            try {
                Thread.sleep(1);
                i+=1;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Sleep 1 WOLOLO");
        }*/


        try {
            container.getService(Match.class).goToThenExecute(0);
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

        simulatorLauncher.setVisualisationMode(Master.class);

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
