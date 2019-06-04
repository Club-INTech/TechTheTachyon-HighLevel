import com.studiohartman.jamepad.*;
import orders.OrderWrapper;
import orders.order.ActuatorsOrder;
import orders.order.MontlheryOrder;
import orders.order.Order;
import pfg.config.Config;
import robot.Master;
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
    private boolean translating;
    private boolean rotating;
    private final OrderWrapper orders;
    private Robot robot;
    private ArmState armPosition = ArmState.NO_IDEA;
    private Order lastOrder;

    public MontlheryController(Master robot, OrderWrapper orders) {
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

        while (!isInterrupted()) {
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
                        robot.useActuator(ActuatorsOrder.DESACTIVE_ELECTROVANNE_DROITE);
                    }
                    if(rightTriggerPressed && !wasRightTriggerPressed) {
                        robot.useActuator(ActuatorsOrder.DESACTIVE_LA_POMPE_DROITE);
                        robot.useActuator(ActuatorsOrder.ACTIVE_ELECTROVANNE_DROITE);
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
                            order(MontlheryOrder.AVANCE);
                        } else {
                            order(MontlheryOrder.RECULE);
                        }
                        moving = true;
                        translating = true;
                    } else if(translating) {
                        order(MontlheryOrder.STOP_TRANSLATION);
                        translating = false;
                    }

                    if(epsilonCheck(rightAxisX)) {
                        moving = true;
                        rotating = true;
                        if(rightAxisX < 0) {
                            if(leftAxisY > 0) {
                                order(MontlheryOrder.LEFT);
                            } else {
                                order(MontlheryOrder.RIGHT);
                            }
                        } else {
                            if(leftAxisY > 0) {
                                order(MontlheryOrder.RIGHT);
                            } else {
                                order(MontlheryOrder.LEFT);
                            }
                        }
                    } else if(rotating) {
                        order(MontlheryOrder.STOP_ROTATION);
                        rotating = false;
                    }

                    if(!moving) {
                        order(MontlheryOrder.STOP);
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
                Thread.sleep(16);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        controllers.quitSDLGamepad();
    }

    private void order(MontlheryOrder order) {
        if(lastOrder != order) {
            orders.sendString(order.getOrderStr());
            lastOrder = order;
        }
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
