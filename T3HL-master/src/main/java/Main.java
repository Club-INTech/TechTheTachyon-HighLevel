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

import ai.AIService;
import ai.MoveToPointAction;
import ai.ScriptAction;
import ai.goap.Action;
import ai.goap.ActionGraph;
import ai.goap.Agent;
import ai.goap.EnvironmentInfo;
import com.panneau.TooManyDigitsException;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.i2c.I2CFactory;
import connection.ConnectionManager;
import data.Graphe;
import data.Sick;
import data.Table;
import data.XYO;
import data.controlers.LidarControler;
import data.controlers.Listener;
import com.panneau.Panneau;
import data.controlers.PanneauService;
import data.controlers.SensorControler;
import data.graphe.Node;
import locomotion.PathFollower;
import locomotion.Pathfinder;
import orders.OrderWrapper;
import robot.Master;
import scripts.Script;
import scripts.ScriptManager;
import scripts.ScriptManagerMaster;
import scripts.ScriptNamesMaster;
import simulator.GraphicalInterface;
import simulator.SimulatorManager;
import simulator.SimulatorManagerLauncher;
import utils.ConfigData;
import utils.Container;
import utils.communication.KeepAlive;
import utils.communication.SimulatorDebug;
import utils.container.ContainerException;
import utils.math.Vec2;
import utils.math.VectCartesian;

import java.io.IOException;
import java.rmi.UnexpectedException;
import java.util.HashMap;
import java.util.Map;

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
    private static LidarControler lidarControler;
    private static Table table;
    private static Master robot;
    private static SimulatorManagerLauncher simulatorLauncher;
    // Regardez, c'est GLaDOS!
    private static AIService ai;
    private static GraphicalInterface interfaceGraphique;
    private static MontlheryController controller;
    private static PanneauService panneauService;

    public static void main(String[] args) {
        initServices();
        if (container.getConfig().getBoolean(ConfigData.SIMULATION)) {
            initSimulator();
        }
/*
        try {
            panneauService.getPaneau().printScore(505);
        }catch (IOException | TooManyDigitsException e){
            e.printStackTrace();
        }
*/
        try {
            initAI();
        } catch (ContainerException e) {
            e.printStackTrace();
        }

        boolean isMaster = container.getConfig().getBoolean(ConfigData.MASTER);
        try {
            Script paletsx3 = ScriptNamesMaster.PALETS3.getScript();
            Script paletsx6 = ScriptNamesMaster.PALETS6.getScript();
            Script accelerateur = ScriptNamesMaster.ACCELERATEUR.getScript();
            Script zone_depart_palets = ScriptNamesMaster.PALETS_ZONE_DEPART.getScript();
            Script zone_chaos_palets = ScriptNamesMaster.PALETS_ZONE_CHAOS.getScript();
            Script goldenium = ScriptNamesMaster.GOLDENIUM.getScript();

            //waitForLLConnection();

            try {
                panneauService.getPaneau().printScore(42);
            }catch( IOException | TooManyDigitsException e){
                e.printStackTrace();
            }

            /// ========== INSERER LE CODE ICI POUR TESTER LES SCRIPTS ========== ///
            int i=0;
            while (i<1000) {
                try {
                    panneauService.getPaneau().printScore(i);
                }catch(IOException | TooManyDigitsException e){
                    e.printStackTrace();
                }
                ++i;
                Thread.sleep(500);
            }
//*/
            XYO.getRobotInstance().update(1500-191, 550, Math.PI);
        // FIXME    robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_ASCENSEUR);
       // FIXME    robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_GAUCHE_A_LA_POSITION_ASCENSEUR);
            robot.setPositionAndOrientation(XYO.getRobotInstance().getPosition(), XYO.getRobotInstance().getOrientation());
            robot.computeNewPositionAndOrientation(Sick.LOWER_RIGHT_CORNER_TOWARDS_PI);
            table.removeAllChaosObstacles();
            orderWrapper.waitJumper();
            zone_depart_palets.goToThenExecute(1);
            paletsx6.goToThenExecute(1);
            accelerateur.goToThenExecute(0);


            /// ========== INSERER LE CODE ICI POUR TESTER LES SCRIPTS ========== ///
            while(robot != null) {
                System.out.println("Finished!");
                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Container.resetInstance();
    }

    private static void waitForLLConnection() {
        while (!connectionManager.areConnectionsInitiated()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static void initAI() throws ContainerException {
        // TODO: mettre la création du graphe à un autre endroit

        Table tableSansZoneDepart = new Table();
        Graphe grapheSansZoneDepart = new Graphe(tableSansZoneDepart);
        Pathfinder pathfinder = new Pathfinder(grapheSansZoneDepart);
        grapheSansZoneDepart.updateConfigNoInit(container.getConfig());
        tableSansZoneDepart.updateConfig(container.getConfig());
        tableSansZoneDepart.initObstacles();
        tableSansZoneDepart.removeFixedObstacleNoReInit(tableSansZoneDepart.getPaletBleuDroite());
        tableSansZoneDepart.removeFixedObstacleNoReInit(tableSansZoneDepart.getPaletRougeDroite());
        tableSansZoneDepart.removeFixedObstacleNoReInit(tableSansZoneDepart.getPaletVertDroite());
        tableSansZoneDepart.updateTableAfterFixedObstaclesChanges();

        Action paletsX6Action = new ScriptAction(ScriptNamesMaster.PALETS6, 0) {
            {
                effects.put("PaletsX6", true);
            }
        };

        Action paletsX3Action = new ScriptAction(ScriptNamesMaster.PALETS3, 0) {
            {
                effects.put("PaletsX3", true);
            }
        };

        Action zoneDepart = new ScriptAction(ScriptNamesMaster.PALETS_ZONE_DEPART, 0) {
            {
                effects.put("ZoneDepart", true);
            }

            @Override
            public boolean modifiesTable() {
                return true;
            }

            @Override
            protected void applyChangesToEnvironment(EnvironmentInfo info) {
                super.applyChangesToEnvironment(info);
                info.getSpectre().switchTableModel(tableSansZoneDepart, grapheSansZoneDepart, pathfinder);
            }
        };

        Action accelerateur = new ScriptAction(ScriptNamesMaster.ACCELERATEUR, 0) {
            {
                effects.put("Accelerateur", true);
            }
        };
        ActionGraph graph = ai.getGraph();
        Agent agent = ai.getAgent();
        graph.node(paletsX6Action);
        graph.node(paletsX3Action);
        graph.node(zoneDepart);
        graph.node(accelerateur);

        // TODO: remove, test only
        int nMoves = 20;//8-4;
        Graphe grapheDeBase = table.getGraphe();
        for (int i = 0; i < nMoves; i++) {
            int randomIndex = (int) Math.floor(Math.random()*grapheDeBase.getNodes().size());
            Node n = grapheDeBase.getNodes().get(randomIndex);
            double x = n.getPosition().getX();
            double y = n.getPosition().getY();
            Vec2 pos = new VectCartesian((int)x, (int)y);
            int finalI = i;
            Action action = new MoveToPointAction(pos) {
                @Override
                protected void applyChangesToEnvironment(EnvironmentInfo info) {
                    super.applyChangesToEnvironment(info);
                    int movesDone = (int) info.getState().get("movesDone");
                    info.getState().put("movesDone", movesDone+1);
                }

                @Override
                public String toString() {
                    return "MoveTo("+this.aim+") #"+(finalI +1)+" (done: "+executed+")";
                }
            };
            graph.node(action);
        }

        Map<String, Object> goalState = new HashMap<>();
        goalState.put("PaletsX6", true);
        goalState.put("PaletsX3", true);
        goalState.put("ZoneDepart", true);
        goalState.put("Accelerateur", true);


        goalState.put("movesDone", nMoves);
        EnvironmentInfo goal = new EnvironmentInfo(new XYO(new VectCartesian(0,0),0.0), goalState, null);
        agent.setCurrentGoal(goal);
    }

    private static void initServices(){
        container = Container.getInstance("Master");

        try {
            // trouve la couleur
            panneauService = container.getService(PanneauService.class);
            if(panneauService.getPaneau().isViolet()) {
                container.getConfig().override(ConfigData.COULEUR, "violet");
            } else {
                container.getConfig().override(ConfigData.COULEUR, "jaune");
            }
            scriptManager = container.getService(ScriptManagerMaster.class);
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
            robot = container.getService(Master.class);
            if((boolean) ConfigData.SIMULATION.getDefaultValue()) {
                SimulatorDebug debug = container.getService(SimulatorDebug.class);
                debug.setSenderPort((int)ConfigData.LL_MASTER_SIMULATEUR.getDefaultValue());
            }
            KeepAlive keepAliveService = container.getService(KeepAlive.class);
            keepAliveService.start();
            controller = new MontlheryController(robot, orderWrapper);
            ai = container.getService(AIService.class);
        } catch (ContainerException e) {
            e.printStackTrace();
        }
    }

    private static void initSimulator(){
        simulatorLauncher = new SimulatorManagerLauncher();

        //On set tous les LL qui sont simulés
        simulatorLauncher.setLLMasterPort((int)ConfigData.LL_MASTER_SIMULATEUR.getDefaultValue());

        //On set tous les HL qui recevront des messages
        simulatorLauncher.setHLSlavePort((int)ConfigData.HL_SLAVE_SIMULATEUR.getDefaultValue());

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

}
