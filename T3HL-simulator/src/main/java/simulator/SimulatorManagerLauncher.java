package simulator;

import data.Table;
import locomotion.PathFollower;
import robot.Robot;
import robot.Robots;
import utils.ConfigData;
import utils.HLInstance;
import utils.container.ContainerException;
import utils.math.Vec2;

import java.util.ArrayList;
import java.util.HashMap;

public class SimulatorManagerLauncher extends Thread{

    //Attributs qui peuvent être modifiés par l'utilisateur avant le lancement
    private int LLMasterPort;
    private int LLSlavePort;
    private int HLMasterPort;
    private int HLSlavePort;
    private int lidarPort;
    private ArrayList<Vec2> pointsToDraw;
    private float speedFactor;
    private boolean colorblindMode;
    private boolean isSimulatingObstacleWithMouse;

    //Attributs internes
    private GraphicalInterface graphicalInterface;
    private SimulatorManager simulatorManager;
    private HashMap<Integer, IRobot> simulatedRobots = new HashMap<>();
    private HashMap<Integer, SimulatedConnectionManager> simulatedLLConnectionManager = new HashMap<>();
    private HashMap<Integer, SimulatedConnectionManager> simulatedHLConnectionManager = new HashMap<>();
    private SimulatedConnectionManager lidarConnection;
    private HLInstance hl;
    private Table table;

    // PathFollower à montrer si non nul (permet de connaître le chemin du robot)
    private PathFollower pathfollowerToShow;
    private int pathfollowerToShowPort;

    //Permet de savoir si cette instance est démarrée
    private boolean isLaunched = false;

    //Permet de savoir si cette instance a fini de faire son travail
    private boolean finished = false;

    /**
     * Permet de savoir si on est en mode visualisation
     */
    private boolean visualisationMode;

    /**
     * La classe du service représentant le robot (permet de tester le master et le slave)
     */
    private Class<? extends Robot> robotClass;

    /* ============================================= Constructeur ============================================= */
    /** Constructeur */
    public SimulatorManagerLauncher(){
        this.initDefaultPassedParameters();
    }

    /* ================================== Passage et initialisation de paramètres ============================= */
    /** Méthode instanciant tous les attributs nécessaires au bon fonctionnement d'un robot simulé
     *  Les attributs définits à NULL sont des attributs qu'il faut SET obligatoirement
     */
    private void initDefaultPassedParameters(){
        this.LLMasterPort=0;
        this.LLSlavePort=0;
        this.HLMasterPort=0;
        this.HLSlavePort=0;
        this.lidarPort=0;
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
    public void setIsSimulatingObstacleWithMouse(boolean value){
        if (canParametersBePassed()) {
            this.isSimulatingObstacleWithMouse = value;
        }
    }

    /** Setter du port utilisé pour parler au LL master */
    public void setLLMasterPort(int LLMasterPort){
        if (canParametersBePassed()){
            this.LLMasterPort=LLMasterPort;
        }
    }

    /** Setter du port utilisé pour parler au LL slave */
    public void setLLSlavePort(int LLSlavePort){
        if (canParametersBePassed()){
            this.LLSlavePort=LLSlavePort;
        }
    }

    /** Setter du port utilisé pour parler au HL master */
    public void setHLMasterPort(int HLMasterPort){
        if (canParametersBePassed()){
            this.HLMasterPort=HLMasterPort;
        }
    }

    /** Setter du port utilisé pour parler au HL slave */
    public void setHLSlavePort(int HLSlavePort){
        if (canParametersBePassed()){
            this.HLSlavePort=HLSlavePort;
        }
    }

    /** Setter du port utilisé pour parler au HL slave */
    void setLidarPort(int lidarPort){
        if (canParametersBePassed()){
            this.lidarPort=lidarPort;
        }
    }

    /** Définition du mode daltonien */
    public void setColorblindMode(boolean value){
        if (canParametersBePassed()) {
            this.colorblindMode = value;
        }
    }

    /** Définit le facteur de vitesse de la simulation */
    public void setSpeedFactor(float speedFactor){
        if (canParametersBePassed()) {
            this.speedFactor = speedFactor;
        }
    }

    /**
     * Active le mode visualisation
     */
    public void setVisualisationMode(Class<? extends Robot> robotClass) {
        if(canParametersBePassed()) {
            this.robotClass = robotClass;
            visualisationMode = true;
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
    public void launch(){
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

        // On instancie les listeners sur plusieurs threads
        // de manière à ce que l'ordre de connexion aux listeners soit sans importance

        // Instanciation du listener LL du Master
        if (this.LLMasterPort != 0) {
            new Thread() {
                @Override
                public void run() {
                    simulatedLLConnectionManager.put(LLMasterPort, new SimulatedConnectionManager(LLMasterPort));
                }
            }.start();
            System.out.println(String.format("Listener LL-Master lancé sur le port %d", this.LLMasterPort));
        }

        // Instanciation du listener LL du Slave
        if (this.LLSlavePort != 0) {
            new Thread() {
                @Override
                public void run() {
                    simulatedLLConnectionManager.put(LLSlavePort, new SimulatedConnectionManager(LLSlavePort));
                }
            }.start();
            System.out.println(String.format("Listener LL-Slave lancé sur le port %d", this.LLSlavePort));
        }

        // Instanciation du listener HL du Master
        if (this.HLMasterPort != 0){
            new Thread() {
                @Override
                public void run() {
                    simulatedHLConnectionManager.put(HLMasterPort, new SimulatedConnectionManager(HLMasterPort));
                }
            }.start();
            System.out.println(String.format("Listener HL-Master lancé sur le port %d", this.HLMasterPort));
        }

        // Instanciation du listener HL du Slave
        if (this.HLSlavePort != 0){
            new Thread() {
                @Override
                public void run() {
                    simulatedHLConnectionManager.put(HLSlavePort, new SimulatedConnectionManager(HLSlavePort));
                }
            }.start();
            System.out.println(String.format("Listener HL-Slave lancé sur le port %d", this.HLSlavePort));
        }

        if (this.lidarPort != 0){
            new Thread() {
                @Override
                public void run() {
                    lidarConnection = new SimulatedConnectionManager(lidarPort);
                }
            }.start();
            System.out.println(String.format("Présence de Lidar simulée sur le port %d", this.lidarPort));
        }

        //On attend que tous les listeners permettant la communication entre le HL et le LL d'un meme robot soient connectés
        if (this.LLMasterPort != 0) {
            while (this.simulatedLLConnectionManager.get(this.LLMasterPort) == null || !this.simulatedLLConnectionManager.get(this.LLMasterPort).isReady()) {
                try {
                    Thread.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        if (this.LLSlavePort != 0) {
            while (this.simulatedLLConnectionManager.get(this.LLSlavePort) == null || !this.simulatedLLConnectionManager.get(this.LLSlavePort).isReady()) {
                try {
                    Thread.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }


        //On attend que tous les listeners permettant la communication entre les HL soient connectés
        if (this.HLMasterPort != 0) {
            while (this.simulatedHLConnectionManager.get(this.HLMasterPort) == null || !this.simulatedHLConnectionManager.get(this.HLMasterPort).isReady()) {
                try {
                    Thread.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        if (this.HLSlavePort != 0) {
            while (this.simulatedHLConnectionManager.get(this.HLSlavePort) == null || !this.simulatedHLConnectionManager.get(this.HLSlavePort).isReady()) {
                try {
                    Thread.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        //Récupération des obstacles
        this.hl = HLInstance.get(Robots.MAIN);

        if(visualisationMode) {
            System.out.println("Initialisation du robot visualisé");
            try {
                VisualisedRobot robot = new VisualisedRobot(hl, robotClass);
                this.simulatedRobots.put(SimulatedConnectionManager.VISUALISATION_PORT, robot);
            } catch (ContainerException e) {
                e.printStackTrace();
            }
        } else {
            // On simule une présence de lidar si nécessaire
            if (this.lidarPort != 0){
                while (this.lidarConnection == null || !this.lidarConnection.isReady()){
                    try {
                        Thread.sleep(5);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }


            // On créer un robot par port
            for (int port : this.simulatedLLConnectionManager.keySet()) {
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
        }

        System.out.println("Instanciation de l'interface graphique");
        try {
            this.table = this.hl.module(Table.class);
        } catch (ContainerException e) {
            e.printStackTrace();
        }

        SimulatedConnectionManager debugServerConnection = new SimulatedConnectionManager((Integer) ConfigData.DEBUG_SIMULATEUR_PORT.getDefaultValue());
        // On instancie l'interface graphique
        this.graphicalInterface = new GraphicalInterface();
        this.graphicalInterface.setPathfollowerToShow(pathfollowerToShow, pathfollowerToShowPort);
        this.graphicalInterface.setTable(this.table);
        this.graphicalInterface.setSimulatedRobots(this.simulatedRobots);
        this.graphicalInterface.setColorblindMode(this.colorblindMode);
        this.graphicalInterface.setIsDrawingPoints(true);
        this.graphicalInterface.setDrawingPaths(true);
        this.graphicalInterface.setDrawingGraph(true);
        this.graphicalInterface.setTimeScale(speedFactor);
        this.graphicalInterface.setIsCreatingObstacleWithMouse(this.isSimulatingObstacleWithMouse);
        this.graphicalInterface.launch();
        System.out.println("Interface graphique instanciée");

        // On instancie le manager de la simulation (qui va s'occuper de faire les appels à toutes les fonctions)
        this.simulatorManager = new SimulatorManager();
        this.simulatorManager.setLLMasterPort(this.LLMasterPort);
        this.simulatorManager.setLLSlavePort(this.LLSlavePort);
        this.simulatorManager.setHLMasterPort(this.HLSlavePort);
        this.simulatorManager.setHLSlavePort(this.HLSlavePort);
        this.simulatorManager.setSimulatedLLConnectionManagers(this.simulatedLLConnectionManager);
        this.simulatorManager.setSimulatedHLConnectionManagers(this.simulatedHLConnectionManager);
        this.simulatorManager.setGraphicalInterface(this.graphicalInterface);
        this.simulatorManager.setSimulatedRobots(this.simulatedRobots);
        this.simulatorManager.setDebugServerConnection(debugServerConnection);
        this.simulatorManager.launch();
        System.out.println("Manager de simulation instancié");

        //On indique que tout a bien été instancié
        this.finished = true;
    }

    public void waitForLaunchCompletion() {
        while(!isFinished()) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /* ================================================ Getters ================================================= */

    /** Getter du manager de la simulation */
    public SimulatorManager getSimulatorManager(){
        if (!this.finished){
            System.out.println("SIMULATEUR : Le lanceur de simulateur n'a pas fini lancer le simulateur");
            return null;
        }
        else{
            return this.simulatorManager;
        }
    }

    public boolean isFinished(){
        return this.finished;
    }
}
