package lowlevel.actuators;

/**
 * Représentation d'un groupe de servomoteurs
 *
 * @author jglrxavpok
 */
public class ServoGroup implements Actuator {

    /**
     * Identifiant du groupe
     */
    private final int id;

    /**
     * Liste des servomoteurs du groupe
     */
    private final Servo[] servos;

    /**
     * Version symétrisée de ce groupe. (Par défaut, 'this')
     */
    protected ServoGroup symetrized = this;

    /**
     * Crées un nouveau groupe de servomoteurs
     * @param id l'identifiant du groupe
     * @param servos liste des servomoteurs du groupe
     */
    public ServoGroup(int id, Servo... servos) {
        this.id = id;
        this.servos = servos;
    }

    /**
     * Version symétrisée du groupe
     * @return
     */
    public ServoGroup getSymetrized() {
        return symetrized;
    }

    /**
     * Nombre de servomoteurs dans le groupe
     * @return
     */
    public int count() {
        return servos.length;
    }

    /**
     * L'identifiant du groupe
     * @return
     */
    public int id() {
        return id;
    }
}
