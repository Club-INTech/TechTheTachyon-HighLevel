import exceptions.OrderException;
import orders.order.*;
import utils.math.VectCartesian;

import java.util.HashMap;

public class SimulatorManager extends Thread {

    //Attributs pouvant être modifiés avant le lancement
    private int[] LLports;
    private int[] HLports;
    private HashMap<Integer, SimulatedConnectionManager> simulatedLLConnectionManagers;
    private HashMap<Integer, SimulatedConnectionManager> simulatedHLConnectionManagers;
    private HashMap<Integer, SimulatedRobot> simulatedRobots;
    private GraphicalInterface graphicalInterface;

    //Permet de savoir si cette instance est démarrée
    private boolean isLaunched = false;

    /* ============================================= Constructeur ============================================= */
    /** Constructeur */
    SimulatorManager(){
        this.initDefaultPassedParameters();
    }

    /* ================================== Passage et initialisation de paramètres ============================= */
    /** Méthode instanciant tous les attributs nécessaires au bon fonctionnement d'un robot simulé
     *  Les attributs définits à NULL sont des attributs qu'il faut SET obligatoirement
     */
    private void initDefaultPassedParameters(){
        this.LLports=new int[]{};
        this.HLports=new int[]{};
        this.graphicalInterface=null;
        this.simulatedRobots=null;
        this.simulatedLLConnectionManagers=new HashMap<>();
        this.simulatedHLConnectionManagers=null;
    }

    /** Setter des ports utilisés pour parler au LL */
    void setLLports(int[] LLports){
        if (canParametersBePassed()) {
            this.LLports = LLports;
        }
    }

    /** Setter des ports utilisés pour parler entre les HL */
    void setHLports(int[] HLports){
        if (canParametersBePassed()) {
            this.HLports = HLports;
        }
    }

    /** Setter de l'interface graphique */
    void setGraphicalInterface(GraphicalInterface graphicalInterface){
        if (canParametersBePassed()) {
            this.graphicalInterface = graphicalInterface;
        }
    }

    /** Set les connexions qui sont utilisées pour recevoir les messages venant d'un HL vers son LL
     * @param simulatedLLConnectionManagers connexion en question
     */
    void setSimulatedLLConnectionManagers(HashMap<Integer,SimulatedConnectionManager> simulatedLLConnectionManagers){
        if (canParametersBePassed()) {
            this.simulatedLLConnectionManagers = simulatedLLConnectionManagers;
        }
    }

    /** Set les connexions qui sont utilisées pour recevoir les messages venant d'un HL vers l'autre HL
     * @param simulatedHLConnectionManagers connexion en question
     */
    void setSimulatedHLConnectionManagers(HashMap<Integer,SimulatedConnectionManager> simulatedHLConnectionManagers){
        if (canParametersBePassed()) {
            this.simulatedHLConnectionManagers = simulatedHLConnectionManagers;
        }
    }

    /** Set les robots qui sont instancié pour qu'ils executent les ordres
     * @param simulatedRobots HashMap<Integer, SimulatedRobot> des robots instanciés
     */
    void setSimulatedRobots(HashMap<Integer,SimulatedRobot> simulatedRobots){
        if (canParametersBePassed()) {
            this.simulatedRobots = simulatedRobots;
        }
    }

    /** Permet de savoir si on a lancé le robot simulé */
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
    /** Lance le manager du simulateur */
    void launch(){
        this.isLaunched=true;
        this.start();
        System.out.println("Manager de simulation démarré");
    }

    /** Manage les messages reçus, le robot et l'interface graphique */
    @Override
    public void run() {
        String lastMessage;

        //On tourne en boucle
        while (true) {

            //On tryUpdate la position du robot
            for (int port : LLports) {
                //On gère les messages d'entrée
                lastMessage = this.simulatedLLConnectionManagers.get(port).getLastReceivedMessage();
                if (lastMessage != null) {
                    handleMessageLL(lastMessage, simulatedRobots.get(port));
                }

                simulatedRobots.get(port).tryUpdate();
            }

            //On écoute les ports du HL pour transmettre un éventuel message
            for (int port : HLports) {
                //On gère les messages d'entrée
                lastMessage = this.simulatedHLConnectionManagers.get(port).getLastReceivedMessage();
                if (lastMessage != null) {
                    handleMessageHL(lastMessage, port);
                }
            }

            //On tryUpdate l'interface graphique
            this.graphicalInterface.tryUpdate();

            //On attend un peu, faut pas deconner
            try {
                Thread.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    /* ================================= Gère les messages qui sont envoyés vers le LL ============================= */
    /** Gère les messages qui sont reçus pour le LL */
    private void handleMessageLL(String m, SimulatedRobot robot){
        System.out.println(String.format("SIMULATEUR-LL : message reçu : %s",m));
        if(m.startsWith("!")) { // ordre qui demande une synchronisation
            m = m.substring(1);
            robot.confirmOrder(m);
        }
        String[] arguments = m.split(" ");
        if (arguments.length>0) {
            String order = arguments[0];
            try {
                if (testOrder(arguments, MotionOrder.MOVE_LENTGHWISE,2)) {
                    robot.moveLengthwise(parseInt(arguments[1]));
                }
                else if (testOrder(arguments, MotionOrder.TURN,2)) {
                    robot.turn(parseFloat(arguments[1]));
                }
                else if (testOrder(arguments, MotionOrder.MOVE_TO_POINT,3)) {
                    robot.goTo(new VectCartesian(parseInt(arguments[1]), parseInt(arguments[2])));
                }
                else if (testOrder(arguments, MotionOrder.STOP,1)) {
                    robot.stop();
                }
                else if (testOrder(arguments, PositionAndOrientationOrder.SET_POSITION_AND_ORIENTATION, 4)){
                    robot.setPosition(new VectCartesian(parseInt(arguments[1]), parseInt(arguments[2])));
                    robot.setOrientation(parseFloat(arguments[3]));
                }
                else if (testOrder(arguments, PositionAndOrientationOrder.SET_ORIENTATION,2)){
                    robot.setOrientation(parseFloat(arguments[1]));
                }
                else if(testOrder(arguments, MontlheryOrder.MONTLHERY, 1)) {
                    robot.setMontlheryMode();
                }
                else if(testOrder(arguments, MontlheryOrder.AVANCE, 1)) {
                    robot.goForward();
                }
                else if(testOrder(arguments, MontlheryOrder.RECULE, 1)) {
                    robot.goBackwards();
                }
                else if(testOrder(arguments, MontlheryOrder.LEFT, 1)) {
                    robot.turnLeft();
                }
                else if(testOrder(arguments, MontlheryOrder.RIGHT, 1)) {
                    robot.turnRight();
                }
                else if(testOrder(arguments, MontlheryOrder.STOP, 1)) {
                    robot.sstop();
                }
                else if(testOrder(arguments, ActuatorsOrder.DESCEND_ASCENSEUR_GAUCHE_DE_UN_PALET, 2)
                        || testOrder(arguments, ActuatorsOrder.MONTE_ASCENCEUR_GAUCHE_DE_UN_PALET, 2)) {
                    robot.sendConfirmationForElevator("left");
                }
                else if(testOrder(arguments, ActuatorsOrder.DESCEND_ASCENSEUR_DROIT_DE_UN_PALET, 2)
                        || testOrder(arguments, ActuatorsOrder.MONTE_ASCENCEUR_DROIT_DE_UN_PALET, 2)) {
                    robot.sendConfirmationForElevator("right");
                }
                else {
                    System.out.println(String.format("SIMULATEUR-LL : l'ordre \"%s\" est inconnu", order));
                }
            }
            catch (OrderException e){
                System.out.println(String.format("SIMULATEUR -LL: %s", e));
            }
        }
        else{
            System.out.println("SIMULATEUR-LL : l'ordre est vide");
        }
    }

    /* ================================= Gère les messages qui sont envoyés vers le HL ============================= */
    /** Transmet les messages qui sont reçus pour le HL de l'autre robot à l'autre robot*/
    private void handleMessageHL(String m, int port){
        if (this.HLports.length==2){
            StringBuilder mWithCarryReturn = new StringBuilder(m);
            mWithCarryReturn.append("\n");
            if (port==this.HLports[0]){
                // possibilités de traitements et d'exploitation des infos du message avant sa transmission
                this.simulatedHLConnectionManagers.get(this.HLports[1]).sendMessage(mWithCarryReturn.toString());
            }
            else{
                // possibilités de traitements et d'exploitation des infos du message avant sa transmission
                System.out.print(mWithCarryReturn);
                this.simulatedHLConnectionManagers.get(this.HLports[0]).sendMessage(mWithCarryReturn.toString());
            }
        }
    }

    /* ========================= Méthodes permettant tester la validité des ordres reçus ========================== */
    /** Compare une string et un ordre
     * @param arguments arguments envoyés au simulateur
     * @param order ordre auquel on compare le message reçu
     * @param nb_args nombre d'arguments attendus (comprenant l'ordre)
     * @return True, si le bon nombre d'arguments est reçu par le simulateur, et que l'ordre correspond au message envoyé, False sinon
     * @throws OrderException si un mauvais nombre d'arguments est reçu par le simulateur
     */
    private boolean testOrder(String[] arguments, Order order, int nb_args) throws OrderException {
        if (arguments[0].equals(order.getOrderStr())){
            if (arguments.length==nb_args){
                return true;
            }
            else{
                StringBuilder message = new StringBuilder();
                for (int i=0; i<arguments.length; i++){
                    message.append(arguments[i]);
                    message.append(" ");
                }
                throw new OrderException(String.format("Mauvais nombre d'arguments pour l'ordre %s(attendu: %d)", message.toString(), nb_args));
            }
        }
        else{
            return false;
        }
    }

    /** Parser de float
     * @param str chaîne de caractère à parser
     * @return le nombre parsé
     */
    private float parseFloat(String str) throws OrderException {
        try{
            return Float.parseFloat(str);
        }
        catch (NumberFormatException e){
            throw new OrderException(String.format("Le parser de float n'a pas réussi à parser \"%s\"",str));
        }
    }

    /** Parser d'integer
     * @param str chaîne de caractère à parser
     * @return le nombre parsé
     */
    private int parseInt(String str) throws OrderException {
        try{
            return Integer.parseInt(str);
        }
        catch (NumberFormatException e){
            throw new OrderException(String.format("Le parser d'integer n'a pas réussi à parser \"%s\"",str));
        }
    }

    /* ================================================= Getters ============================================== */
    /** Getter de l'interface graphique */
    GraphicalInterface getGraphicalInterface(){
        return this.graphicalInterface;
    }
}