package scripts;

import connection.Connection;
import connection.ConnectionManager;
import data.Table;
import data.XYO;
import data.controlers.Listener;
import data.controlers.SensorControler;
import locomotion.PathFollower;
import orders.OrderWrapper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import robot.Master;
import robot.Robot;
import simulator.GraphicalInterface;
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
    private Container container;
    protected OrderWrapper orderWrapper;
    protected Robot robot;

    @Before
    public void initHL() {
    }

    public abstract void initState(Container container) throws ContainerException;

    public abstract Vec2 startPosition();

    @Test
    public void simulate() {
        setup(true);
        action();
    }

    @Test
    public void runOnRobot() {
        setup(false);
        action();
    }

    public abstract void action();

    @After
    public void cleanup() {
        Container.resetInstance();
        ScriptNamesMaster.cleanup();
    }

    private void waitForLLConnection() {
        while(!connectionManager.areConnectionsInitiated()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void setup(boolean simulationMode) {
        ConfigData.SIMULATION.setDefaultValue(simulationMode);
        container = Container.getInstance("robot.Master");
        try {
//            ScriptManagerMaster scriptManager = container.getService(ScriptManagerMaster.class);
            connectionManager = container.getService(ConnectionManager.class);
            orderWrapper = container.getService(OrderWrapper.class);
            Listener listener = container.getService(Listener.class);
            listener.start();
            SensorControler sensorControler = container.getService(SensorControler.class);
            sensorControler.start();
            Table table = container.getService(Table.class);
            table.initObstacles();
            robot = getRobot();
            ScriptNamesMaster.reInit();

        } catch (ContainerException e) {
            e.printStackTrace();
        }

        if(simulationMode) {
            // init simulator
            SimulatorManagerLauncher simulatorLauncher = new SimulatorManagerLauncher();
            simulatorLauncher.setLLMasterPort((int) ConfigData.LL_MASTER_SIMULATEUR.getDefaultValue());
            simulatorLauncher.setHLSlavePort((int)ConfigData.HL_SLAVE_SIMULATEUR.getDefaultValue());
            simulatorLauncher.setColorblindMode(false);
            simulatorLauncher.setSpeedFactor(1);
            try {
                simulatorLauncher.setPathfollowerToShow(container.getService(PathFollower.class), (int)ConfigData.LL_MASTER_SIMULATEUR.getDefaultValue());
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
            if(simulationMode) {
                SimulatorDebug debug = container.getService(SimulatorDebug.class);
                debug.setSenderPort((int)ConfigData.LL_MASTER_SIMULATEUR.getDefaultValue());
            }
            initState(container);

            Vec2 start = startPosition();
            XYO.getRobotInstance().update(start.getX(), start.getY(), 0.0 /* TODO Angle ?*/);
            robot.setPositionAndOrientation(XYO.getRobotInstance().getPosition(), XYO.getRobotInstance().getOrientation());

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
