package utils;

import pfg.config.Config;
import pfg.config.ConfigInfo;

public enum Offsets implements ConfigInfo {

    /*
        ATTENTION! S'il y a des valeurs dans config.txt, les valeurs en dessous seront écrasées! (Ce sont des valeurs par défaut)
     */


    /*      MASTER      */
    PALETSX6_X_JAUNE(-2),
    PALETSX6_Y_JAUNE(-4),
    PALETSX6_X_VIOLET(20),
    PALETSX6_Y_VIOLET(10),
    PALETSX6_THETA_VIOLET(0.025/* atan(10.0/100.0) */),

    ACCELERATEUR_Y_VIOLET(6),
    ACCELERATEUR_Y_JAUNE(3),
    PALETS_X6_BALANCE_Y_JAUNE(0),
    PALETS_X6_BALANCE_Y_VIOLET(10),

    PALETS_DEPART_X_JAUNE(0),
    PALETS_DEPART_X_VIOLET(0),

    ZDD_X_VIOLET(5),

    /*      SLAVE       */
    PALETSX3_X_JAUNE(0),
    PALETSX3_Y_JAUNE(0),
    PALETSX3_X_VIOLET(0),
    PALETSX3_Y_VIOLET(0),

    GOLDENIUM_X_JAUNE(0),
    GOLDENIUM_Y_JAUNE(0),
    GOLDENIUM_X_VIOLET(0),
    GOLDENIUM_Y_VIOLET(0),

    ;

    private double offset;


    Offsets(double offset){
        this.offset=offset;
    }

    public double get() {
        return offset;
    }

    public static void loadFromConfig(Config config) {
        for(Offsets offset : values()) {
            offset.offset = config.getDouble(offset);
        }
    }

    @Override
    public Double getDefaultValue() {
        return offset;
    }
}
