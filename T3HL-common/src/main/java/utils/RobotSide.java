package utils;

public enum RobotSide {

    // TODO: Ajouter FRONT/BACK (ou autres) si besoin
    LEFT,
    RIGHT;

    private RobotSide opposite;

    static {
        LEFT.opposite = RIGHT;
        RIGHT.opposite = LEFT;
    }

    public RobotSide opposite() {
        return opposite;
    }

}
