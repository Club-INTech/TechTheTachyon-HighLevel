import data.Table;
import utils.Container;
import utils.container.ContainerException;

import java.util.HashMap;

public class SimulatorManagerLauncher extends Thread{

    private GraphicalInterface graphicalInterface;
    private SimulatorManager simulatorManager;
    private HashMap<Integer, SimulatedRobot> simulatedRobots = new HashMap<>();
    private HashMap<Integer, SimulatedConnectionManager> simulatedLLConnectionManager = new HashMap<>();
    private HashMap<Integer, SimulatedConnectionManager> simulatedHLConnectionManager = new HashMap<>();

    private Container container;
    private Table table;

    private int[] LLports;
    private int[] HLports;
    private boolean colorblindMode;
    private boolean launched;

    /** Constructeur */
    SimulatorManagerLauncher(){
        this.launched=false;
        this.colorblindMode=false;
        this.LLports=new int[]{};
        this.HLports=new int[]{};
    }

    /** Setter des ports utilisés pour parler au LL */
    void setLLports(int[] LLports){
        if (!this.launched) {
            this.LLports = LLports;
        }
        else{
            System.out.println("SIMULATEUR : Le simulateur est déjà lancé, vous ne pouvez plus changer ses paramètres");
        }
    }

    /** Setter des ports utilisés pour parler entre les HL */
    void setHLports(int[] HLports) {
        if (!this.launched) {
            this.HLports = HLports;
        }
        else{
            System.out.println("SIMULATEUR : Le simulateur est déjà lancé, vous ne pouvez plus changer ses paramètres");
        }
    }

    /** Définition du mode daltonien */
    void setColorblindMode(boolean value){
        if (!this.launched) {
            this.colorblindMode = value;
        }
        else{
            System.out.println("SIMULATEUR : Le simulateur est déjà lancé, vous ne pouvez plus changer ses paramètres");
        }
    }

    /** Getter pour les ports utilisés pour parler au LL */
    public int[] getLLports(){
        return this.LLports;
    }

    /** Getter pour les ports utilisés pour parler entre les HL */
    public int[] getHLports(){
        return this.HLports;
    }

    /** Getter pour savoir si on est en mode colorblind */
    public boolean getColorblindMode(){
        return this.colorblindMode;
    }

    /** Fonction qui crée un Thread pour lancer le simualteur */
    public void launchSimulator(){
        this.start();
    }

    @Override
    /** Run du thread du simulateur */
    public void run() {
        launch();
    }

    /** Lancer le simulateur */
    private void launch() {
        this.launched=true;
        if (this.HLports.length > 2) {
            System.out.println("SIMULATEUR : Le nombre de ports attendus pour le HL (2ème argument) est de 2 ou moins");
            return;
        }

        // On instancie un listener par port, de manière à ce que l'ordre de connexion aux listeners soit sans importance
        for (int port : this.LLports) {
            Thread serverThread = new Thread() {
                @Override
                public void run() {
                    simulatedLLConnectionManager.put(port, new SimulatedConnectionManager(port));
                }
            };
            serverThread.start();
            System.out.println(String.format("Listener LL lancé sur le port %d", port));
        }

        for (int port : this.HLports) {
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
        for (int port : this.LLports) {
            while (this.simulatedLLConnectionManager.get(port) == null || !this.simulatedLLConnectionManager.get(port).isReady()) {
                try {
                    Thread.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        for (int port : this.HLports) {
            while (this.simulatedHLConnectionManager.get(port) == null || !this.simulatedHLConnectionManager.get(port).isReady()) {
                try {
                    Thread.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        // On créer un robot par port
        for (int port : this.LLports) {
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
        this.graphicalInterface = new GraphicalInterface(LLports, HLports, this.simulatedRobots, this.table, this.colorblindMode);
        System.out.println(String.format("Interface graphique instanciée"));

        // On instancie le manager de la simulation (qui va s'occuper de faire les appels à toutes les fonctions)
        this.simulatorManager = new SimulatorManager(LLports, HLports, this.graphicalInterface, this.simulatedLLConnectionManager, this.simulatedHLConnectionManager, this.simulatedRobots);
        System.out.println("Manager instancié");
    }
}
