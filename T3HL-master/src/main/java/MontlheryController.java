import com.studiohartman.jamepad.*;
import orders.OrderWrapper;
import orders.order.MontlheryOrder;
import pfg.config.Config;
import robot.Robot;
import utils.container.Service;

public class MontlheryController extends Thread implements Service {

    private static final float EPSILON = 0.35f;
    private final OrderWrapper orders;
    private Robot robot;

    public MontlheryController(Robot robot, OrderWrapper orders) {
        this.robot = robot;
        this.orders = orders;
    }

    @Override
    public void run() {
        robot.switchToMontlheryMode();
        // initialisation du manager pour les manettes
        ControllerManager controllers = new ControllerManager();
        controllers.initSDLGamepad();

        ControllerIndex controller = controllers.getControllerIndex(0);

        while (!Thread.currentThread().isInterrupted()) {
            controllers.update();
            if (controller.isConnected()) {
                try {
                    float leftAxisY = controller.getAxisState(ControllerAxis.LEFTY);
                    float rightAxisX = controller.getAxisState(ControllerAxis.RIGHTX);

                    boolean moving = false;
                    if(epsilonCheck(leftAxisY)) {
                        float forward = leftAxisY;
                        if(forward > 0) {
                            orders.sendString(MontlheryOrder.AVANCE.getOrderStr());
                        } else {
                            orders.sendString(MontlheryOrder.RECULE.getOrderStr());
                        }
                        moving = true;
                    }

                    if(epsilonCheck(rightAxisX)) {
                        moving = true;
                        if(rightAxisX < 0) {
                            orders.sendString(MontlheryOrder.LEFT.getOrderStr());
                        } else {
                            orders.sendString(MontlheryOrder.RIGHT.getOrderStr());
                        }
                    }

                    if(!moving) {
                        orders.sendString(MontlheryOrder.STOP.getOrderStr());
                    }
                } catch (ControllerUnpluggedException e) {
                    e.printStackTrace();
                }
            } else {
                System.err.println("Aucune manette de branchée!");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        controllers.quitSDLGamepad();
    }

    /**
     * Vérifies qu'une valeur est en dehors de [-epsilon;epsilon]
     * @param value
     * @return
     */
    private boolean epsilonCheck(float value) {
        return Math.abs(value) > EPSILON;
    }

    @Override
    public void updateConfig(Config config) {

    }
}
