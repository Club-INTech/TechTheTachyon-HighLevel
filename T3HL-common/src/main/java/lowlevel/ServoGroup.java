package lowlevel;

public class ServoGroup {

    private final int id;
    private final Servo[] servos;
    protected ServoGroup symetrized;

    public ServoGroup(int id, Servo... servos) {
        this.id = id;
        this.servos = servos;
        // TODO
    }

    public ServoGroup getSymetrized() {
        return symetrized;
    }

    public int count() {
        return servos.length;
    }

    public int id() {
        return id;
    }
}
