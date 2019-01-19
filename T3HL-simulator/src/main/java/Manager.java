import orders.order.MotionOrder;
import orders.order.Order;
import utils.math.VectCartesian;

public class Manager extends Thread {

    private ConnectionManagerSimulator commSimulatorThread;
    private GraphicalInterface graphicalInterface;
    private SimulatedRobot simulatedRobot;
    private Thread threadHandlingMessages;

    /** Constructeur */
    Manager(GraphicalInterface graphicalInterace, ConnectionManagerSimulator commSimulatorThread, SimulatedRobot simulatedRobot){
        this.graphicalInterface = graphicalInterace;
        this.commSimulatorThread = commSimulatorThread;
        this.simulatedRobot = simulatedRobot;
        this.start();
    }

    @Override
    /** Manage les messages reçus, le robot et l'interface graphique */
    public void run() {
        String lastMessage;

        //On tourne en boucle
        while (true) {
            //On gère les messages d'entrée
            lastMessage=this.commSimulatorThread.getLastReceivedMessage();
            if (lastMessage!=null) {
                handleMessage(lastMessage);
            }

            //On tryUpdate la position du robot si besoin est
            this.simulatedRobot.tryUpdate();

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
    private void handleMessage(String m){
        System.out.println(String.format("SIMULATEUR : message reçu : %s",m));
        String[] arguments = m.split(" ");
        String order = arguments[0];
        if (compare(order, MotionOrder.MOVE_LENTGHWISE)){
            this.simulatedRobot.moveLengthwise(Integer.parseInt(arguments[1]));
        }
        else if (compare(order, MotionOrder.TURN)){
            this.simulatedRobot.turn(Float.parseFloat(arguments[1]));
        }
        else if (compare(order, MotionOrder.MOVE_TO_POINT)){
            this.simulatedRobot.goTo(new VectCartesian(Integer.parseInt(arguments[1]), Integer.parseInt(arguments[2])));
        }
        else if (compare(order, MotionOrder.STOP)){
            this.simulatedRobot.stop();
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