import data.Table;
import data.table.Obstacle;
import utils.Container;
import utils.container.ContainerException;

import java.util.ArrayList;
import java.util.HashMap;

public class LaunchSimulatorManager extends Thread{

    private GraphicalInterface graphicalInterface;
    private SimulatorManager simulatorManager;
    private HashMap<Integer, SimulatedRobot> simulatedRobots = new HashMap<>();
    private HashMap<Integer, ConnectionManagerSimulator> connectionManagerSimulators = new HashMap<>();

    private Container container;
    private ArrayList<Obstacle> obstacles;
    private Table table;

    /** Constructeur
     * @param ports ports sur lesquels on devra se connecter pour parler au simulateur
     */
    public LaunchSimulatorManager(int[] ports) {

        // On instancie un listener par port, de manière à ce que l'ordre de connexion aux listeners soit sans importance
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

        // On attend que tous les listeners soient connectés
        for (int port : ports){
            while (this.connectionManagerSimulators.get(port) == null || !this.connectionManagerSimulators.get(port).isReady()) {
                try {
                    Thread.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        // On créer un robot par port
        for (int port : ports) {
            System.out.println(String.format("(%d) Listener connecté",port));
            SimulatedRobot simulatedRobot = new SimulatedRobot();
            this.simulatedRobots.put(port, simulatedRobot);
            System.out.println(String.format("(%d) Robot simulé instancié",port));
        }

        //Récupération des obstacles
        this.container = Container.getInstance("Master");
        try {
            this.table = this.container.getService(Table.class);
            table.initObstacles();
        } catch (ContainerException e) {
            e.printStackTrace();
        }


        // On instancie l'interface graphique
        this.graphicalInterface = new GraphicalInterface(ports, this.simulatedRobots, this.table);
        System.out.println(String.format("Interface graphique instanciée"));

        // On instancie le manager de la simulation (qui va s'occuper de faire les appels à toutes les fonctions)
        this.simulatorManager = new SimulatorManager(ports, this.graphicalInterface, this.connectionManagerSimulators,this.simulatedRobots);
        System.out.println("Manager instancié");

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
