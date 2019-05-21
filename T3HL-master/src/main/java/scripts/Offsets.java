package scripts;

public enum Offsets {

    PALETSX6_X_JAUNE(-2),
    PALETSX6_Y_JAUNE(-4),
    PALETSX6_X_VIOLET(20),
    PALETSX6_Y_VIOLET(4),
    ;

    int offset;



    Offsets(int offset){
        this.offset=offset;
    }

    public int getOffset() {
        return offset;
    }
}
