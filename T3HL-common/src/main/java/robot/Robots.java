package robot;

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
