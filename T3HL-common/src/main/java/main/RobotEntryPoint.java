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
package main;

import ai.AIService;
import connection.ConnectionManager;
import data.Table;
import data.controlers.LidarControler;
import data.controlers.Listener;
import data.controlers.PanneauService;
import data.controlers.SensorControler;
import locomotion.UnableToMoveException;
import orders.OrderWrapper;
import robot.Robot;
import scripts.ScriptManager;
import utils.ConfigData;
import utils.Container;
import utils.Log;
import utils.MatchTimer;
import utils.communication.KeepAlive;
import utils.container.ContainerException;

/**
 * @author nayth
 */
public abstract class RobotEntryPoint {

    protected Container container;
    protected ScriptManager scriptManager;
    protected ConnectionManager connectionManager;
    protected OrderWrapper orderWrapper;
    protected Listener listener;
    protected SensorControler sensorControler;
    protected LidarControler lidarControler;
    protected Table table;
    protected Robot robot;
    // Regardez, c'est GLaDOS!
    protected AIService ai;
    protected PanneauService panneauService;

    public void entryPoint(Container container, Class<? extends Robot> robotClass, Class<? extends ScriptManager> scriptManagerClass) throws ContainerException {
        this.container = container;
        initServices(container, robotClass, scriptManagerClass);
        preLLConnection();
        waitForLLConnection();
        Log.COMMUNICATION.debug("Connection established, starting match");

        try {
            act();
        } catch (Exception e) {
            e.printStackTrace();
        }
        /// ========== INSERER LE CODE ICI POUR TESTER LES SCRIPTS ========== ///
        while(robot != null) {
            System.out.println("Finished!");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Container.resetInstance();
    }

    protected abstract void preLLConnection() throws ContainerException;

    protected abstract void act() throws UnableToMoveException;

    private void waitForLLConnection() {
        while (!connectionManager.areConnectionsInitiated()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    protected void initServices(Container container, Class<? extends Robot> robotClass, Class<? extends ScriptManager> scriptManagerClass) {
        try {
            // trouve la couleur
            panneauService = container.getService(PanneauService.class);
            if(panneauService.getPaneau() != null) {
                if(panneauService.getPaneau().isViolet()) {
                    container.getConfig().override(ConfigData.COULEUR, "violet");
                } else {
                    container.getConfig().override(ConfigData.COULEUR, "jaune");
                }
            } else {
                Log.STRATEGY.critical("PAS DE PANNEAU");
            }
//            container.getConfig().override(ConfigData.COULEUR, "jaune");
            Log.STRATEGY.debug("Couleur: "+container.getConfig().getString(ConfigData.COULEUR));
            scriptManager = container.getService(scriptManagerClass);
            connectionManager = container.getService(ConnectionManager.class);
            orderWrapper = container.getService(OrderWrapper.class);
            listener = container.getService(Listener.class);
            listener.start();
            sensorControler = container.getService(SensorControler.class);
            sensorControler.start();
            table = container.getService(Table.class);
            table.initObstacles();
            lidarControler = container.getService(LidarControler.class);
            lidarControler.start();
            robot = container.getService(robotClass);
            KeepAlive keepAliveService = container.getService(KeepAlive.class);
            keepAliveService.start();
            ai = container.getService(AIService.class);
            MatchTimer timer = container.getService(MatchTimer.class);
            timer.start();
        } catch (ContainerException e) {
            e.printStackTrace();
        }
    }

}