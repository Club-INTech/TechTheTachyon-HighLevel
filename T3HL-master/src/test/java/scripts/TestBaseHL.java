package scripts;

import connection.ConnectionManager;
import data.Table;
import data.XYO;
import data.controlers.DataController;
import data.controlers.LidarController;
import data.controlers.Listener;
import locomotion.PathFollower;
import orders.OrderWrapper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import robot.Master;
import robot.Robot;
import robot.Robots;
import simulator.GraphicalInterface;
import simulator.SimulatedConnectionManager;
import simulator.SimulatorManager;
import simulator.SimulatorManagerLauncher;
import utils.ConfigData;
import utils.HLInstance;
import utils.communication.SimulatorDebug;
import utils.container.ContainerException;
import utils.math.Vec2;

public abstract class TestBaseHL {

    private ConnectionManager connectionManager;
    protected HLInstance hl;
    protected OrderWrapper orderWrapper;
    protected Robot robot;
    protected Table table;

    @Before
    public void initHL() {
    }

    public abstract void initState(HLInstance hl) throws ContainerException;

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
        HLInstance.resetInstance();
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
        hl = HLInstance.get(Robots.MAIN);
        hl.getConfig().override(ConfigData.SIMULATION, simulationMode);
        try {
            connectionManager = hl.module(ConnectionManager.class);
            orderWrapper = hl.module(OrderWrapper.class);
            hl.initModule(DataController.class);

            if(hl.getConfig().get(ConfigData.USING_LIDAR)) {
                hl.initModule(LidarController.class);
            }

            hl.initModule(Listener.class);

            table = hl.module(Table.class);
            table.initObstacles();
            robot = getRobot();

            Vec2 start = startPosition();
            XYO.getRobotInstance().update(start.getX(), start.getY(), startOrientation());
        } catch (ContainerException e) {
            e.printStackTrace();
        }

        boolean visualise = hl.getConfig().getBoolean(ConfigData.VISUALISATION);
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
                    simulatorLauncher.setPathfollowerToShow(hl.module(PathFollower.class), (int)ConfigData.LL_MASTER_SIMULATEUR.getDefaultValue());
                } else {
                    simulatorLauncher.setPathfollowerToShow(hl.module(PathFollower.class), SimulatedConnectionManager.VISUALISATION_PORT);
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
        }

        waitForLLConnection();

        try {
            if(simulationMode || visualise) {
                SimulatorDebug debug = hl.module(SimulatorDebug.class);
                if(simulationMode) {
                    debug.setSenderPort((int)ConfigData.LL_MASTER_SIMULATEUR.getDefaultValue());
                } else {
                    debug.setSenderPort(SimulatedConnectionManager.VISUALISATION_PORT);
                }
            }
            initState(hl);

            Vec2 start = startPosition();
            XYO.getRobotInstance().update(start.getX(), start.getY(), startOrientation());
            robot.setPositionAndOrientation(start, startOrientation());
        } catch (ContainerException e) {
            e.printStackTrace();
        }
    }

    private Robot getRobot() throws ContainerException {
        return hl.module(Master.class);
    }
}
