package scripts;

import connection.ConnectionManager;
import data.Table;
import data.controlers.Listener;
import data.controlers.SensorControler;
import orders.OrderWrapper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import simulator.GraphicalInterface;
import simulator.SimulatorManager;
import simulator.SimulatorManagerLauncher;
import utils.ConfigData;
import utils.Container;
import utils.communication.KeepAlive;
import utils.container.ContainerException;

public abstract class TestBaseHL {

    private ConnectionManager connectionManager;
    private OrderWrapper orderWrapper;

    @Before
    public void initHL() {
    }

    public abstract void initState(Container container) throws ContainerException;

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
        Container container = Container.getInstance("robot.Master");
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
            ScriptNamesMaster.reInit();
        } catch (ContainerException e) {
            e.printStackTrace();
        }

        if(simulationMode) {
            // init simulator
            SimulatorManagerLauncher simulatorLauncher = new SimulatorManagerLauncher();
            simulatorLauncher.setLLports(new int[]{(int) ConfigData.MASTER_LL_SIMULATEUR.getDefaultValue()});
            simulatorLauncher.setHLports(new int[]{(int)ConfigData.SLAVE_SIMULATEUR.getDefaultValue()});
            simulatorLauncher.setColorblindMode(false);
            simulatorLauncher.setSpeedFactor(1);
            simulatorLauncher.setIsSimulatingObstacleWithMouse(true);
            simulatorLauncher.launch();
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            simulatorLauncher.waitForInterface();
            SimulatorManager simulatorManager = simulatorLauncher.getSimulatorManager();
            GraphicalInterface interfaceGraphique = simulatorManager.getGraphicalInterface();
        }

        waitForLLConnection();

        try {
            initState(container);
            KeepAlive keepAliveService = container.getService(KeepAlive.class);
            keepAliveService.start();
        } catch (ContainerException e) {
            e.printStackTrace();
        }
    }
}
