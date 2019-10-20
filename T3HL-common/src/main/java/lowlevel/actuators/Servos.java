package lowlevel.actuators;

/**
 * Servomoteurs disponibles pour le HL
 *
 * @author jglrxavpok
 */
public interface Servos {
    Servo oust = new Servo(7);

    Servo rightArmBase = new Servo(1, 4);
    Servo rightArmElbow = new Servo(2, 5);
    Servo rightArmWrist = new Servo(3, 6);

    Servo leftArmBase = new Servo(4, 1);
    Servo leftArmElbow = new Servo(5, 2);
    Servo leftArmWrist = new Servo(6, 3);
}
