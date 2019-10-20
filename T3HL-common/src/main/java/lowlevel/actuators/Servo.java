package lowlevel.actuators;

/**
 * Classe représentant un servomoteur
 *
 * @author jglrxavpok
 */
public class Servo implements Actuator {
    /**
     * Identifiant du servomoteur
     */
    private int id;

    /**
     * Identifiant du servomoteur lorsqu'il est symétrisé
     */
    private int symetrizedID;

    /**
     * Instancies le servomoteur avec le même ID symétrisé que l'ID de base. Ce servomoteur ne sera donc pas symétrisé.
     * @param id l'identifiant du servomoteur
     */
    public Servo(int id) {
        this(id, id);
    }

    /**
     * Instancies le servomoteur
     * @param id l'identifiant du servomoteur
     * @param symetrizedID l'identifiant du servomoteur lorsqu'il est symétrisé
     */
    public Servo(int id, int symetrizedID) {
        this.id = id;
        this.symetrizedID = symetrizedID;
    }
}
