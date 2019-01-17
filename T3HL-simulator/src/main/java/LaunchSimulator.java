import orders.order.MotionOrder;

public class LaunchSimulator extends Thread{

    private GraphicalInterface graphicalInterface;
    private String lastMessage;
    private Manager robotManager;
    private SimulatedRobot simulatedRobot;
    private ConnectionManagerSimulator connectionManagerSimulator;

    /** Constructeur
     * @param port port sur lequel on devra se connecter pour parler au simulateur
     */
    public LaunchSimulator(int port, boolean onlyReceivingSimulatedMessages) {
        this.connectionManagerSimulator = new ConnectionManagerSimulator(port, onlyReceivingSimulatedMessages);
        System.out.println("Listener lancé et connecté");
        this.simulatedRobot = new SimulatedRobot();
        System.out.println("Robot simulé instancié");
        this.graphicalInterface = new GraphicalInterface(this.simulatedRobot);
        System.out.println("Interface graphique instanciée");
        this.robotManager = new Manager(this.graphicalInterface, this.connectionManagerSimulator, this.simulatedRobot);
        System.out.println("Physique instanciée");

        this.launchTestCode();
    }

    /** Code de test : peut envoyer des messages simulés */
    private void launchTestCode(){
        try {
            this.connectionManagerSimulator.SIMULATE_receiveMessage(MotionOrder.MOVE_TO_POINT.getOrderStr()+" 1500 1500");
            Thread.sleep(5000);
            this.connectionManagerSimulator.SIMULATE_receiveMessage("conar 1500 1500");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
