package lowlevel;

public class Servo {
    // TODO
    private int id;
    private int symetrizedID;

    public Servo(int id) {
        this(id, id);
    }

    public Servo(int id, int symetrizedID) {
        this.id = id;
        this.symetrizedID = symetrizedID;
    }
}
