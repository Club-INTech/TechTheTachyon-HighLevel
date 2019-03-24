import connection.ConnectionManager;
import data.Graphe;
import data.Table;
import data.XYO;
import data.controlers.Listener;
import data.controlers.SensorControler;
import data.graphe.Node;
import data.graphe.Ridge;
import locomotion.PathFollower;
import locomotion.UnableToMoveException;
import orders.OrderWrapper;
import orders.order.Order;
import org.junit.Before;
import org.junit.Test;
import robot.Master;
import scripts.ScriptManagerMaster;
import utils.Container;
import utils.container.ContainerException;
import utils.math.Segment;
import utils.math.Vec2;
import utils.math.VectCartesian;

public class TestPathFollower {

    private static Container container;
    private static Master robot;
    private static ConnectionManager connectionManager;
    private static Listener listener;
    private static OrderWrapper orderWrapper;
    private static Graphe graphe;
    private static PathFollower pathFollower;
    private static SensorControler sensorController;

    @Before
    public void init() {
        container = Container.getInstance("robot.Master");
        try {
            connectionManager = container.getService(ConnectionManager.class);
            orderWrapper = container.getService(OrderWrapper.class);
            listener = container.getService(Listener.class);
            listener.start();
            graphe = container.getService(Graphe.class);
            pathFollower = container.getService(PathFollower.class);
            sensorController = container.getService(SensorControler.class);
            sensorController.start();
            robot = container.getService(Master.class);
        } catch (ContainerException e) {
            e.printStackTrace();
        }
        waitForLL();

        try {
            orderWrapper.sendString("ping");
            Thread.sleep(2000);
            XYO.getRobotInstance().update(0, 0, 0);
            robot.setPositionAndOrientation(XYO.getRobotInstance().getPosition(), XYO.getRobotInstance().getOrientation());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void waitForLL() {
        while(!connectionManager.areConnectionsInitiated()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void simplePath() throws UnableToMoveException, InterruptedException {
        graphe.clear();
        Vec2 pointA = new VectCartesian(0,0);
        Vec2 pointB = new VectCartesian(200,0);
        Vec2 pointC = new VectCartesian(200,200);
        XYO.getRobotInstance().getPosition().setXY(pointA.getX(), pointA.getY());
        Node nodeA = new Node(pointA, true);
        Node nodeB = new Node(pointB, true);
        Node nodeC = new Node(pointC, true);
        graphe.getNodes().add(nodeA);
        graphe.getNodes().add(nodeB);
        graphe.getNodes().add(nodeC);
        Ridge ridgeAB = new Ridge(0, new Segment(pointA, pointB));
        Ridge ridgeBC = new Ridge(0, new Segment(pointB, pointC));
        graphe.getRidges().add(ridgeAB);
        graphe.getRidges().add(ridgeBC);
        nodeA.addNeighbour(nodeB, ridgeAB);
        nodeB.addNeighbour(nodeA, ridgeAB);
        nodeB.addNeighbour(nodeC, ridgeBC);
        nodeC.addNeighbour(nodeB, ridgeBC);
        graphe.setUpdated(true);
        robot.followPathTo(pointC);
    }
}
