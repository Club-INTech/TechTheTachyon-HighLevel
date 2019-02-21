import data.Table;
import locomotion.PathFollower;
import utils.Container;
import utils.container.ContainerException;
import utils.math.Vec2;

import java.util.ArrayList;
import java.util.HashMap;

public class SimulatorManagerLauncher extends Thread{

    //Attributs qui peuvent être modifiés par l'utilisateur avant le lancement
    private int[] LLports;
    private int[] HLports;
    private ArrayList<Vec2> pointsToDraw;
    private float speedFactor;
    private boolean colorblindMode;
    private boolean isSimulatingObstacleWithMouse;

    //Attributs internes
    private GraphicalInterface graphicalInterface;
    private SimulatorManager simulatorManager;
    private HashMap<Integer, SimulatedRobot> simulatedRobots = new HashMap<>();
    private HashMap<Integer, SimulatedConnectionManager> simulatedLLConnectionManager = new HashMap<>();
    private HashMap<Integer, SimulatedConnectionManager> simulatedHLConnectionManager = new HashMap<>();
    private Container container;
    private Table table;

    // PathFollower à montrer si non nul (permet de connaître le chemin du robot)
    private PathFollower pathfollowerToShow;
    private int pathfollowerToShowPort;

    //Permet de savoir si cette instance est démarrée
    private boolean isLaunched = false;

    //Permet de savoir si cette instance a fini de faire son travail
    private boolean hasFinished = false;

    /* ============================================= Constructeur ============================================= */
    /** Constructeur */
    SimulatorManagerLauncher(){
        this.initDefaultPassedParameters();
    }

    /* ================================== Passage et initialisation de paramètres ============================= */
    /** Méthode instanciant tous les attributs nécessaires au bon fonctionnement d'un robot simulé
     *  Les attributs définits à NULL sont des attributs qu'il faut SET obligatoirement
     */
    private void initDefaultPassedParameters(){
        this.LLports=new int[]{};
        this.HLports=new int[]{};
        this.pointsToDraw = new ArrayList<Vec2>();
        this.speedFactor=1;
        this.colorblindMode=false;
        this.isSimulatingObstacleWithMouse=false;
    }

    public void setPathfollowerToShow(PathFollower follower, int port) {
        this.pathfollowerToShow = follower;
        this.pathfollowerToShowPort = port;
    }

    /** Définit si on simule un obstacle avec la souris dans l'interface graphique */
    void setIsSimulatingObstacleWithMouse(boolean value){
        if (canParametersBePassed()) {
            this.isSimulatingObstacleWithMouse = value;
        }
    }

    /** Setter des ports utilisés pour parler au LL */
    void setLLports(int[] LLports){
        if (canParametersBePassed()) {
            this.LLports = LLports;
        }
    }

    /** Setter des ports utilisés pour parler entre les HL */
    void setHLports(int[] HLports) {
        if (canParametersBePassed()) {
            this.HLports = HLports;
        }
    }

    /** Définition du mode daltonien */
    void setColorblindMode(boolean value){
        if (canParametersBePassed()) {
            this.colorblindMode = value;
        }
    }

    /** Définit le facteur de vitesse de la simulation */
    void setSpeedFactor(float speedFactor){
        if (canParametersBePassed()) {
            this.speedFactor = speedFactor;
        }
    }

    /** Permet de savoir si on a lancé le simulateur */
    private boolean canParametersBePassed(){
        if (this.isLaunched){
            System.out.println("SIMULATEUR : On ne peut pas passer de paramètres à l'interface graphique lorsqu'elle est déjà lancée");
            return false;
        }
        else {
            return true;
        }
    }

    /* ======================================== Lancement de l'instance ======================================== */
    /** Fonction qui crée un Thread pour lancer le simualteur */
    void launch(){
        this.start();
        System.out.println("Lanceur de simulateur démarré");
    }

    @Override
    /** Run du thread du simulateur */
    public void run() {
        launchSimulatorManager();
    }

    /** Lancer le simulateur */
    private void launchSimulatorManager() {
        this.isLaunched=true;
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

        // On attend que tous les listeners permettant la communication entre le HL et le LL d'un même robot soient connectés
        for (int port : this.LLports) {
            while (this.simulatedLLConnectionManager.get(port) == null || !this.simulatedLLConnectionManager.get(port).isReady()) {
                try {
                    Thread.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        //On attend que tous les listeners permettant la communication entre les HL soient connectés
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

            //On instancie un robot simulé pour chaque LL instancié
            SimulatedRobot simulatedRobot = new SimulatedRobot(port);
            simulatedRobot.setSimulatedLLConnectionManager(this.simulatedLLConnectionManager.get(port));
            simulatedRobot.setSpeedFactor(this.speedFactor);
            simulatedRobot.launch();

            //On ajoute le robot simulé dans la liste de tous les robots simulés
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
        this.graphicalInterface = new GraphicalInterface();
        this.graphicalInterface.setPathfollowerToShow(pathfollowerToShow, pathfollowerToShowPort);
        this.graphicalInterface.setTable(this.table);
        this.graphicalInterface.setSimulatedRobots(this.simulatedRobots);
        this.graphicalInterface.setColorblindMode(this.colorblindMode);
        this.graphicalInterface.setIsDrawingPoints(true);
        this.graphicalInterface.setDrawingPaths(true);
        this.graphicalInterface.setDrawingGraph(true);
        this.graphicalInterface.setIsCreatingObstacleWithMouse(this.isSimulatingObstacleWithMouse);
        this.graphicalInterface.launch();
        System.out.println("Interface graphique instanciée");

        // On instancie le manager de la simulation (qui va s'occuper de faire les appels à toutes les fonctions)
        this.simulatorManager = new SimulatorManager();
        this.simulatorManager.setLLports(this.LLports);
        this.simulatorManager.setHLports(this.HLports);
        this.simulatorManager.setSimulatedLLConnectionManagers(this.simulatedLLConnectionManager);
        this.simulatorManager.setSimulatedHLConnectionManagers(this.simulatedHLConnectionManager);
        this.simulatorManager.setGraphicalInterface(this.graphicalInterface);
        this.simulatorManager.setSimulatedRobots(this.simulatedRobots);
        this.simulatorManager.launch();
        System.out.println("Manager de simulation instancié");

        //On indique que tout a bien été instancié
        this.hasFinished = true;
    }

    /* ================================================ Getters ================================================= */
    /** Getter du manager de la simulation */
    SimulatorManager getSimulatorManager(){
        if (!this.hasFinished){
            System.out.println("SIMULATEUR : Le lanceur de simulateur n'a pas fini lancer le simulateur");
            return null;
        }
        else{
            return this.simulatorManager;
        }
    }

}
