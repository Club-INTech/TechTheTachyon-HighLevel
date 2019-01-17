import orders.order.MotionOrder;
import orders.order.Order;
import utils.Log;
import utils.math.VectCartesian;

public class Manager extends Thread {

    private ConnectionManagerSimulator commSimulatorThread;
    private GraphicalInterface graphicalInterface;
    private SimulatedRobot simulatedRobot;
    private Thread threadHandlingMessages;

    Manager(GraphicalInterface graphicalInterace, ConnectionManagerSimulator commSimulatorThread, SimulatedRobot simulatedRobot){
        this.graphicalInterface = graphicalInterace;
        this.commSimulatorThread = commSimulatorThread;
        this.simulatedRobot = simulatedRobot;
        this.start();
    }

    @Override
    public void run() {
        String lastMessage;

        //On tourne en boucle
        while (true) {
            //On gère les messages d'entrée
            lastMessage=this.commSimulatorThread.getLastReceivedMessage();
            if (lastMessage!=null) {
                handleMessage(lastMessage);
            }

            //On update la position du robot si besoin est
            this.simulatedRobot.update();

            //On update l'interface graphique
            this.graphicalInterface.update();

            //On attend un peu, faut pas deconner
            try {
                Thread.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


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
            Log.COMMUNICATION.critical(String.format("SIMULATEUR : l'ordre \"%s\" est inconnu", order));
        }
    }

    private boolean compare(String message, Order order){
        return message.equals(order.getOrderStr());
    }

}