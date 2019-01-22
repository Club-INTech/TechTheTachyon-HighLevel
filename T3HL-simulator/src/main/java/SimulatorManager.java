import exceptions.OrderException;
import orders.order.MotionOrder;
import orders.order.Order;
import utils.math.VectCartesian;

import java.util.HashMap;

public class SimulatorManager extends Thread {

    private int[] ports;
    private HashMap<Integer, ConnectionManagerSimulator> commSimulatorThreads;
    private HashMap<Integer, SimulatedRobot> simulatedRobots;
    private GraphicalInterface graphicalInterface;

    /** Constructeur */
    SimulatorManager(int[] ports, GraphicalInterface graphicalInterace, HashMap<Integer, ConnectionManagerSimulator> commSimulatorThread, HashMap<Integer, SimulatedRobot> simulatedRobots){
        this.ports=ports;
        this.graphicalInterface = graphicalInterace;
        this.commSimulatorThreads = commSimulatorThread;
        this.simulatedRobots = simulatedRobots;


        this.start();
    }

    @Override
    /** Manage les messages reçus, le robot et l'interface graphique */
    public void run() {
        String lastMessage;

        //On tourne en boucle
        while (true) {

            //On tryUpdate la position du robot si besoin est
            for (int port: ports) {

                //On gère les messages d'entrée
                lastMessage=this.commSimulatorThreads.get(port).getLastReceivedMessage();
                if (lastMessage!=null) {
                    handleMessage(lastMessage, simulatedRobots.get(port));
                }

                simulatedRobots.get(port).tryUpdate();
                if (simulatedRobots.get(port).mustSendStoppedMovingMessage()){
                    this.commSimulatorThreads.get(port).sendMessage("StoppedMoving\n");
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

    /** Gère les messages qui sont reçus */
    private void handleMessage(String m, SimulatedRobot robot){
        System.out.println(String.format("SIMULATEUR : message reçu : %s",m));
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
                else {
                    System.out.println(String.format("SIMULATEUR : l'ordre \"%s\" est inconnu", order));
                }
            }
            catch (OrderException e){
                System.out.println(String.format("SIMULATEUR : %s", e));
            }
        }
        else{
            System.out.println("SIMULATEUR : l'ordre est vide");
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

}