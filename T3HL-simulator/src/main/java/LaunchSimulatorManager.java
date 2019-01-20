import orders.order.MotionOrder;

public class LaunchSimulatorManager extends Thread{

    private GraphicalInterface graphicalInterface;
    private SimulatorManager robotManager;
    private SimulatedRobot simulatedRobot;
    private ConnectionManagerSimulator connectionManagerSimulator;

    /** Constructeur
     * @param port port sur lequel on devra se connecter pour parler au simulateur
     */
    public LaunchSimulatorManager(int port) {
        Thread serverThread = new Thread(){
            @Override
            public void run() {
                connectionManagerSimulator = new ConnectionManagerSimulator(port);
            }
        };
        serverThread.start();
        System.out.println("Listener lancé");


        //On attend que le listener soit connecté
        while (connectionManagerSimulator==null || !connectionManagerSimulator.isReady()){
            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("Listener connecté");
        this.simulatedRobot = new SimulatedRobot();
        System.out.println("Robot simulé instancié");
        this.graphicalInterface = new GraphicalInterface(this.simulatedRobot);
        System.out.println("Interface graphique instanciée");
        this.robotManager = new SimulatorManager(this.graphicalInterface, connectionManagerSimulator, this.simulatedRobot);
        System.out.println("Physique instanciée");

        this.launchTestCode();
    }

    /** Code de test : peut envoyer des messages simulés */
    private void launchTestCode(){
        try {
            this.connectionManagerSimulator.SIMULATE_receiveMessage(MotionOrder.MOVE_TO_POINT.getOrderStr()+" 1500 2000");
            Thread.sleep(5000);
            this.connectionManagerSimulator.SIMULATE_receiveMessage(MotionOrder.MOVE_TO_POINT.getOrderStr()+" -1500 0");
            Thread.sleep(5000);
            this.connectionManagerSimulator.SIMULATE_receiveMessage(MotionOrder.MOVE_TO_POINT.getOrderStr()+" -0 0");
            Thread.sleep(5000);
            this.connectionManagerSimulator.SIMULATE_receiveMessage("conar 1500 1500");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
