import java.util.HashMap;

public class LaunchSimulatorManager extends Thread{

    private GraphicalInterface graphicalInterface;
    private SimulatorManager robotManager;
    private HashMap<Integer, SimulatedRobot> simulatedRobots = new HashMap<>();
    private HashMap<Integer, ConnectionManagerSimulator> connectionManagerSimulators = new HashMap<>();

    /** Constructeur
     * @param ports ports sur lesquels on devra se connecter pour parler au simulateur
     */
    public LaunchSimulatorManager(int[] ports) {

        for (int port : ports) {
            Thread serverThread = new Thread() {
                @Override
                public void run() {
                    connectionManagerSimulators.put(port, new ConnectionManagerSimulator(port));
                }
            };
            serverThread.start();
            System.out.println(String.format("Listener lancé sur le port %d", port));
        }

        //On attend que tous les listeners soient connectés
        for (int port : ports){
            while (this.connectionManagerSimulators.get(port) == null || !this.connectionManagerSimulators.get(port).isReady()) {
                try {
                    Thread.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        for (int port : ports) {
            System.out.println(String.format("(%d) Listener connecté",port));
            SimulatedRobot simulatedRobot = new SimulatedRobot();
            this.simulatedRobots.put(port, simulatedRobot);
            System.out.println(String.format("(%d) Robot simulé instancié",port));
        }

        this.graphicalInterface = new GraphicalInterface(ports, this.simulatedRobots);
        System.out.println(String.format("Interface graphique instanciée"));

        this.robotManager = new SimulatorManager(ports, this.graphicalInterface,this.connectionManagerSimulators,this.simulatedRobots);
        System.out.println("Physique instanciée");

        //this.launchTestCode();
    }
    /** Code de test : peut envoyer des messages simulés */
/*
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
    */

}
