package scripts;

import connection.ConnectionManager;
import data.Table;
import data.XYO;
import data.controlers.LidarControler;
import data.controlers.Listener;
import data.controlers.DataControler;
import locomotion.PathFollower;
import orders.OrderWrapper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import robot.Master;
import robot.Robot;
import simulator.GraphicalInterface;
import simulator.SimulatedConnectionManager;
import simulator.SimulatorManager;
import simulator.SimulatorManagerLauncher;
import utils.ConfigData;
import utils.Container;
import utils.communication.KeepAlive;
import utils.communication.SimulatorDebug;
import utils.container.ContainerException;
import utils.math.Vec2;

public abstract class TestBaseHL {

    private ConnectionManager connectionManager;
    protected Container container;
    protected OrderWrapper orderWrapper;
    protected Robot robot;
    protected Table table;

    @Before
    public void initHL() {
    }

    public abstract void initState(Container container) throws ContainerException;

    public abstract Vec2 startPosition();

    public double startOrientation() {
        return 0.0;
    }

    @Test
    public void simulate() throws Exception {
        setup(true);
        action();
    }

    @Test
    public void runOnRobot() throws Exception {
        setup(false);
        action();
    }

    public abstract void action() throws Exception;

    @After
    public void cleanup() {
        Container.resetInstance();
    }

    private void waitForLLConnection() {
        System.out.println("Waiting for connections...");
        while(!connectionManager.areMandatoryConnectionsInitiated()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Everything connected!");
    }

    protected void setup(boolean simulationMode) {
        ConfigData.SIMULATION.setDefaultValue(simulationMode);
        container = Container.getInstance("Master");
        try {
//            ScriptManagerMaster scriptManager = container.getService(ScriptManagerMaster.class);
            connectionManager = container.getService(ConnectionManager.class);
            orderWrapper = container.getService(OrderWrapper.class);
            DataControler sensorControler = container.getService(DataControler.class);
            sensorControler.start();

            if((boolean)ConfigData.USING_LIDAR.getDefaultValue()) {
                LidarControler lidarControler = container.getService(LidarControler.class);
                lidarControler.start();
            }

            Listener listener = container.getService(Listener.class);
            listener.start();

            table = container.getService(Table.class);
            table.initObstacles();
            robot = getRobot();

            Vec2 start = startPosition();
            XYO.getRobotInstance().update(start.getX(), start.getY(), startOrientation());
        } catch (ContainerException e) {
            e.printStackTrace();
        }

        boolean visualise = container.getConfig().getBoolean(ConfigData.VISUALISATION);
        if(simulationMode || visualise) {
            // init simulator
            SimulatorManagerLauncher simulatorLauncher = new SimulatorManagerLauncher();
            if(simulationMode) {
                simulatorLauncher.setLLMasterPort((int) ConfigData.LL_MASTER_SIMULATEUR.getDefaultValue());
                simulatorLauncher.setHLSlavePort((int)ConfigData.HL_SLAVE_SIMULATEUR.getDefaultValue());
                simulatorLauncher.setSpeedFactor(1);
            } else {
                simulatorLauncher.setVisualisationMode(Master.class);
            }
            simulatorLauncher.setColorblindMode(false);
            try {
                if(simulationMode) {
                    simulatorLauncher.setPathfollowerToShow(container.getService(PathFollower.class), (int)ConfigData.LL_MASTER_SIMULATEUR.getDefaultValue());
                } else {
                    simulatorLauncher.setPathfollowerToShow(container.getService(PathFollower.class), SimulatedConnectionManager.VISUALISATION_PORT);
                }
            } catch (ContainerException e) {
                e.printStackTrace();
            }
            simulatorLauncher.setIsSimulatingObstacleWithMouse(true);
            simulatorLauncher.launch();
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            simulatorLauncher.waitForLaunchCompletion();
            SimulatorManager simulatorManager = simulatorLauncher.getSimulatorManager();
            GraphicalInterface interfaceGraphique = simulatorManager.getGraphicalInterface();
        }

        waitForLLConnection();

        try {
            if(simulationMode || visualise) {
                SimulatorDebug debug = container.getService(SimulatorDebug.class);
                if(simulationMode) {
                    debug.setSenderPort((int)ConfigData.LL_MASTER_SIMULATEUR.getDefaultValue());
                } else {
                    debug.setSenderPort(SimulatedConnectionManager.VISUALISATION_PORT);
                }
            }
            initState(container);

            Vec2 start = startPosition();
            XYO.getRobotInstance().update(start.getX(), start.getY(), startOrientation());
            robot.setPositionAndOrientation(start, startOrientation());

            KeepAlive keepAliveService = container.getService(KeepAlive.class);
            keepAliveService.start();
        } catch (ContainerException e) {
            e.printStackTrace();
        }
    }

    private Robot getRobot() throws ContainerException {
        return container.getService(Master.class);
    }
}
