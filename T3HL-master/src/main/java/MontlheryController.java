import com.studiohartman.jamepad.*;
import lowlevel.order.Order;
import orders.OrderWrapper;
import orders.order.ActuatorsOrders;
import orders.order.ElevatorOrders;
import orders.order.MontlheryOrders;
import robot.Master;
import robot.Robot;
import utils.container.Module;

public class MontlheryController extends Thread implements Module {

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
                    // LEFT BUMPER: active pompe droite
                    // RIGHT BUMPER: désactive pompe droite
                    if(aPressed && !wasAPressed && armPosition != ArmState.GROUND) {
                        armPosition = ArmState.GROUND;
                        // TODO: remplacer robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_SOL);
                    }
                    if(bPressed && !wasBPressed && armPosition != ArmState.ELEVATOR) {
                        armPosition = ArmState.ELEVATOR;
                        // TODO: remplacer robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_ASCENSEUR);
                    }
                    if(xPressed && !wasXPressed) {
                        robot.perform(ElevatorOrders.RaiseRightElevator);
                    }
                    if(yPressed && !wasYPressed) {
                        robot.perform(ElevatorOrders.LowerRightElevator);
                    }
                    if(leftTriggerPressed && !wasLeftTriggerPressed) {
                        orders.perform(ActuatorsOrders.ActivateRightPump);
                        orders.perform(ActuatorsOrders.DeactivateRightValve);
                    }
                    if(rightTriggerPressed && !wasRightTriggerPressed) {
                        orders.perform(ActuatorsOrders.DeactivateLeftPump);
                        orders.perform(ActuatorsOrders.ActivateRightValve);
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
                            order(MontlheryOrders.GoForward);
                        } else {
                            order(MontlheryOrders.GoBackwards);
                        }
                        moving = true;
                        translating = true;
                    } else if(translating) {
                        order(MontlheryOrders.StopTranslation);
                        translating = false;
                    }

                    if(epsilonCheck(rightAxisX)) {
                        moving = true;
                        rotating = true;
                        if(rightAxisX < 0) {
                            if(leftAxisY > 0) {
                                order(MontlheryOrders.Left);
                            } else {
                                order(MontlheryOrders.Right);
                            }
                        } else {
                            if(leftAxisY > 0) {
                                order(MontlheryOrders.Right);
                            } else {
                                order(MontlheryOrders.Left);
                            }
                        }
                    } else if(rotating) {
                        order(MontlheryOrders.StopRotation);
                        rotating = false;
                    }

                    if(!moving) {
                        order(MontlheryOrders.Stop);
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

    private void order(Order order) {
        if(lastOrder != order) {
            orders.sendString(order.toLL());
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

}
