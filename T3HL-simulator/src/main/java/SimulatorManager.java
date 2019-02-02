import exceptions.OrderException;
import orders.order.MotionOrder;
import orders.order.Order;
import orders.order.PositionAndOrientationOrder;
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

    /** Constructeur */
    SimulatorManager(int[] LLports, int[] HLports, GraphicalInterface graphicalInterace,
                     HashMap<Integer,SimulatedConnectionManager> simulatedLLConectionManagers,
                     HashMap<Integer, SimulatedConnectionManager> simulatedHLConectionManagers,
                     HashMap<Integer, SimulatedRobot> simulatedRobots){
        this.LLports = LLports;
        this.HLports = HLports;
        this.graphicalInterface = graphicalInterace;
        this.simulatedLLConnectionManagers = simulatedLLConectionManagers;
        this.simulatedHLConnectionManagers = simulatedHLConectionManagers;
        this.simulatedRobots = simulatedRobots;

        this.start();
    }


    /** Manage les messages reçus, le robot et l'interface graphique */
    @Override
    public void run() {
        String lastMessage;

        //On tourne en boucle
        while (true) {

            //On tryUpdate la position du robot
            for (int port: LLports) {
                //On gère les messages d'entrée
                lastMessage=this.simulatedLLConnectionManagers.get(port).getLastReceivedMessage();
                if (lastMessage!=null) {
                    handleMessageLL(lastMessage, simulatedRobots.get(port));
                }

                simulatedRobots.get(port).tryUpdate();
            }

            //On écoute les ports du HL pour transmettre un éventuel message
            for (int port : HLports) {
                //On gère les messages d'entrée
                lastMessage=this.simulatedHLConnectionManagers.get(port).getLastReceivedMessage();
                if (lastMessage!=null) {
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

    /** Gère les messages qui sont reçus pour le LL */
    private void handleMessageLL(String m, SimulatedRobot robot){
        System.out.println(String.format("SIMULATEUR-LL : message reçu : %s",m));
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
                this.simulatedHLConnectionManagers.get(this.HLports[0]).sendMessage(mWithCarryReturn.toString());
            }
        }
    }

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

    /** Getter de l'interface graphique */
    GraphicalInterface getGraphicalInterface(){
        return this.graphicalInterface;
    }
}