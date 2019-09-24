package robot;

/**
 * Liste des Robots et identifiants utilisés dans le HL
 *
 * @author jglrxavpok
 */
public enum Robots {
    MAIN("Master"),
    SECONDARY("Slave");

    private final String id;

    private Robots(String id) {
        this.id = id;
    }

    public String id() {
        return id;
    }
}
