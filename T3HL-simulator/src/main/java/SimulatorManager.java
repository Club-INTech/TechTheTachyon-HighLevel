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
        String order = arguments[0];
        if (compare(order, MotionOrder.MOVE_LENTGHWISE)){
            robot.moveLengthwise(Integer.parseInt(arguments[1]));
        }
        else if (compare(order, MotionOrder.TURN)){
            robot.turn(Float.parseFloat(arguments[1]));
        }
        else if (compare(order, MotionOrder.MOVE_TO_POINT)){
            robot.goTo(new VectCartesian(Integer.parseInt(arguments[1]), Integer.parseInt(arguments[2])));
        }
        else if (compare(order, MotionOrder.STOP)){
            robot.stop();
        }
        else{
            System.out.println(String.format("SIMULATEUR : l'ordre \"%s\" est inconnu", order));
        }
    }

    /** Compare une string et un ordre */
    private boolean compare(String str, Order order){
        return str.equals(order.getOrderStr());
    }

}