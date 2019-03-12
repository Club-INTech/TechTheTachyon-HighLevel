import com.studiohartman.jamepad.*;
import orders.OrderWrapper;
import orders.order.ActuatorsOrder;
import orders.order.MontlheryOrder;
import pfg.config.Config;
import robot.Robot;
import utils.container.Service;

public class MontlheryController extends Thread implements Service {

    enum ArmState {
        GROUND, ELEVATOR, NO_IDEA
    }

    private static final float EPSILON = 0.35f;
    private boolean wasAPressed;
    private boolean wasBPressed;
    private boolean wasXPressed;
    private boolean wasYPressed;
    private boolean wasLeftTriggerPressed;
    private boolean wasRightTriggerPressed;
    private boolean wasMoving;
    private final OrderWrapper orders;
    private Robot robot;
    private ArmState armPosition = ArmState.NO_IDEA;

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

                    boolean aPressed = controller.isButtonPressed(ControllerButton.A);
                    boolean bPressed = controller.isButtonPressed(ControllerButton.B);
                    boolean xPressed = controller.isButtonPressed(ControllerButton.X);
                    boolean yPressed = controller.isButtonPressed(ControllerButton.Y);
                    boolean leftTriggerPressed = controller.isButtonPressed(ControllerButton.LEFTBUMPER);
                    boolean rightTriggerPressed = controller.isButtonPressed(ControllerButton.RIGHTBUMPER);
                    // A: Goto GROUND
                    // B: Goto ELEVATOR
                    // X: monter asc droit
                    // Y: descendre asc droit
                    // LEFT TRIGGER: active pompe droite
                    // RIGHT TRIGGER: désactive pompe droite
                    if(aPressed && !wasAPressed && armPosition != ArmState.GROUND) {
                        armPosition = ArmState.GROUND;
                        robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_SOL);
                    }
                    if(bPressed && !wasBPressed && armPosition != ArmState.ELEVATOR) {
                        armPosition = ArmState.ELEVATOR;
                        robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_ASCENSEUR);
                    }
                    if(xPressed && !wasXPressed) {
                        robot.useActuator(ActuatorsOrder.MONTE_ASCENCEUR_DROIT_DE_UN_PALET);
                    }
                    if(yPressed && !wasYPressed) {
                        robot.useActuator(ActuatorsOrder.DESCEND_ASCENSEUR_DROIT_DE_UN_PALET);
                    }
                    if(leftTriggerPressed && !wasLeftTriggerPressed) {
                        robot.useActuator(ActuatorsOrder.ACTIVE_LA_POMPE_DROITE);
                        robot.useActuator(ActuatorsOrder.ACTIVE_ELECTROVANNE_DROITE);
                    }
                    if(rightTriggerPressed && !wasRightTriggerPressed) {
                        robot.useActuator(ActuatorsOrder.DESACTIVE_LA_POMPE_DROITE);
                        robot.useActuator(ActuatorsOrder.DESACTIVE_ELECTROVANNE_DROITE);
                    }

                    wasAPressed = aPressed;
                    wasBPressed = bPressed;
                    wasXPressed = xPressed;
                    wasYPressed = yPressed;
                    wasLeftTriggerPressed = leftTriggerPressed;
                    wasRightTriggerPressed = rightTriggerPressed;

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

                    if(!moving && wasMoving) {
                        orders.sendString(MontlheryOrder.STOP.getOrderStr());
                    }
                    wasMoving = moving;
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
