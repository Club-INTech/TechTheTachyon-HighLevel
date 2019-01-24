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
    private HashMap<Integer, SimulatedConnectionManager> simulatedLLConnectionManager = new HashMap<>();
    private HashMap<Integer, SimulatedConnectionManager> simulatedHLConnectionManager = new HashMap<>();

    private Container container;
    private Table table;

    /** Constructeur
     * @param LLports ports sur lesquels on devra se connecter pour parler au LL simulé
     * @param HLports ports sur lesquels on devra se connecter pour parler à l'autre HL
     */
    public LaunchSimulatorManager(int[] LLports, int[] HLports) {
        if (HLports.length > 2) {
            System.out.println("SIMULATOR : Le nombre de ports attendus pour le HL (2ème argument) est de 2 ou moins");
            return;
        }

        // On instancie un listener par port, de manière à ce que l'ordre de connexion aux listeners soit sans importance
        for (int port : LLports) {
            Thread serverThread = new Thread() {
                @Override
                public void run() {
                    simulatedLLConnectionManager.put(port, new SimulatedConnectionManager(port));
                }
            };
            serverThread.start();
            System.out.println(String.format("Listener LL lancé sur le port %d", port));
        }

        for (int port : HLports) {
            Thread serverThread = new Thread() {
                @Override
                public void run() {
                    simulatedHLConnectionManager.put(port, new SimulatedConnectionManager(port));
                }
            };
            serverThread.start();
            System.out.println(String.format("Listener HL lancé sur le port %d", port));
        }

        // On attend que tous les listeners soient connectés
        for (int port : LLports) {
            while (this.simulatedLLConnectionManager.get(port) == null || !this.simulatedLLConnectionManager.get(port).isReady()) {
                try {
                    Thread.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        for (int port : HLports) {
            while (this.simulatedHLConnectionManager.get(port) == null || !this.simulatedHLConnectionManager.get(port).isReady()) {
                try {
                    Thread.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        // On créer un robot par port
        for (int port : LLports) {
            System.out.println(String.format("(%d) Listener connecté", port));
            SimulatedRobot simulatedRobot = new SimulatedRobot();
            this.simulatedRobots.put(port, simulatedRobot);
            System.out.println(String.format("(%d) Robot simulé instancié", port));
        }

        //Récupération des obstacles
        this.container = Container.getInstance("Master");
        try {
            this.table = this.container.getService(Table.class);
        } catch (ContainerException e) {
            e.printStackTrace();
        }


        // On instancie l'interface graphique
        this.graphicalInterface = new GraphicalInterface(LLports, HLports, this.simulatedRobots, this.table);
        System.out.println(String.format("Interface graphique instanciée"));

        // On instancie le manager de la simulation (qui va s'occuper de faire les appels à toutes les fonctions)
        this.simulatorManager = new SimulatorManager(LLports, HLports, this.graphicalInterface, this.simulatedLLConnectionManager, this.simulatedHLConnectionManager, this.simulatedRobots);
        System.out.println("Manager instancié");
    }
}
