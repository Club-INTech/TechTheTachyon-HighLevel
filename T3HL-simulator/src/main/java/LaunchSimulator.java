import orders.order.MotionOrder;

public class LaunchSimulator extends Thread{

    private GraphicalInterface graphicalInterface;
    private String lastMessage;
    private RobotManager robotManager;
    private SimulatedRobot simulatedRobot;
    private ConnectionManagerSimulator connectionManagerSimulator;

    /** Constructeur
     * @param port port sur lequel on devra se connecter pour parler au simulateur
     */
    public LaunchSimulator(int port) {
        this.connectionManagerSimulator = new ConnectionManagerSimulator(port);
        this.simulatedRobot = new SimulatedRobot();
        this.graphicalInterface = new GraphicalInterface(this.simulatedRobot);
        this.robotManager = new RobotManager(this.graphicalInterface, this.connectionManagerSimulator, this.simulatedRobot);


        this.launchTestCode();
    }

    private void launchTestCode(){
        this.connectionManagerSimulator.SIMULATE_receiveMessage(MotionOrder.MOVE_TO_POINT.getOrderStr()+" 100 100");
    }

}
