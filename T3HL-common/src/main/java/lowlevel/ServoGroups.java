package lowlevel;

public class ServoGroups implements Servos {

    public static final ServoGroup LeftArm = new ServoGroup(0, leftArmBase, leftArmElbow, leftArmWrist);
    public static final ServoGroup RightArm = new ServoGroup(1, rightArmBase, rightArmElbow, rightArmWrist);

    static {
        LeftArm.symetrized = RightArm;
        RightArm.symetrized = LeftArm;
    }
}
