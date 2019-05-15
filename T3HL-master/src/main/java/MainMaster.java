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

import com.panneau.LEDs;
import com.panneau.Panneau;
import com.panneau.TooManyDigitsException;
import data.Sick;
import locomotion.PathFollower;
import locomotion.UnableToMoveException;
import main.RobotEntryPoint;
import orders.Speed;
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
import utils.math.Vec2;
import utils.math.VectCartesian;

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

    protected void waitForAllConnectionsReady() {
        int ledCount = container.getConfig().getInt(ConfigData.LED_COUNT);
        int trainLength = 5;
        LEDs.RGBColor[] colors = new LEDs.RGBColor[trainLength+1];
        for (int i = 0; i < trainLength+1; i++) {
            float intensity = (float)(trainLength-i)/trainLength+1;
            colors[i] = new LEDs.RGBColor(intensity, 0.0f, 0.0f);
        }
        LEDs leds = null;
        if(panneauService.getPanneau() != null) {
            leds = panneauService.getPanneau().getLeds();
            leds.fillColor(LEDs.RGBColor.NOIR); // on éteint la bande
        }
        int index = 0;
        while (!connectionManager.areConnectionsInitiated()) {
            try {
                if(leds != null) {
                    /*for(int i = 0;i < trainLength;i++) {
                        leds.set((index+i) % ledCount, colors[i]);
                    }
                    leds.set((index+trainLength) % ledCount, LEDs.RGBColor.NOIR);*/
                    leds.set((index+1 % ledCount), colors[2]);
                    leds.set(index % ledCount, LEDs.RGBColor.NOIR);
                }
                System.out.println("index: "+index);

                index++;
                index %= ledCount;
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
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
                Panneau.TeamColor initialColor = panneau.getTeamColor();
                LEDs leds = panneau.getLeds();
                LEDs.RGBColor waitingColor1 = new LEDs.RGBColor(0.5f, 0.5f, 0.0f);
                LEDs.RGBColor waitingColor2 = new LEDs.RGBColor(0.5f, 0.0f, 0.5f);

                // on attend une première activation du switch
                while(initialColor == panneau.getTeamColor()) {
                    try {
                        panneau.printScore(5005);
                        leds.fillColor(waitingColor1);
                        TimeUnit.MILLISECONDS.sleep(100);
                        panneau.printScore(550);
                        leds.fillColor(waitingColor2);
                        TimeUnit.MILLISECONDS.sleep(100);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                resetColorToTeamColor(panneau);

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

                resetColorToTeamColor(panneau);
                Log.STRATEGY.warning("Couleur: "+panneau.getTeamColor());
            } catch (InterruptedException | TooManyDigitsException e) {
                e.printStackTrace();
            }
        }
    }

    private void resetColorToTeamColor(Panneau panneau) {
        LEDs leds = panneau.getLeds();
        switch (panneau.getTeamColor()) {
            case JAUNE:
                leds.fillColor(LEDs.RGBColor.JAUNE);
                break;

            case VIOLET:
                leds.fillColor(LEDs.RGBColor.MAGENTA);
                break;
        }
    }

    @Override
    protected void act() throws UnableToMoveException {
        robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_ASCENSEUR);
        robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_ASCENSEUR);
        robot.setRotationSpeed(Speed.ULTRA_SLOW_ALL);

        Vec2 newPos = new VectCartesian(1500-191, 350);
        robot.setPositionAndOrientation(newPos, Math.PI);

        if (container.getConfig().getString(ConfigData.COULEUR).equals("violet")) {
            Log.TABLE.critical("Couleur pour le recalage : violet");
            robot.computeNewPositionAndOrientation(Sick.LOWER_LEFT_CORNER_TOWARDS_0);
        } else {
            Log.TABLE.critical("Couleur pour le recalage : jaune");
            robot.computeNewPositionAndOrientation(Sick.LOWER_RIGHT_CORNER_TOWARDS_PI);
        }
        robot.turn(Math.PI/2);

        robot.setRotationSpeed(Speed.DEFAULT_SPEED);
        // la symétrie de la table permet de corriger le droit en gauche (bug ou feature?)
        table.removeTemporaryObstacle(table.getPaletRougeDroite());
        table.removeTemporaryObstacle(table.getPaletVertDroite());
        table.removeTemporaryObstacle(table.getPaletBleuDroite());
        table.removeAllChaosObstacles();

        orderWrapper.waitJumper();

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
