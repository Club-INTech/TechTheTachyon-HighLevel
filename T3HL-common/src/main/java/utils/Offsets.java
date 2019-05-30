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

    ZDD_X_VIOLET(5),
    ZDD_X_JAUNE(5),
    ZDD_Y_JAUNE(-5),
    ZDD_Y_VIOLET(-5),
    ZDD_POST_BALANCE_X_JAUNE(0),
    ZDD_POST_BALANCE_X_VIOLET(0),
    ZDD_POST_BALANCE_Y_JAUNE(0),
    ZDD_POST_BALANCE_Y_VIOLET(0),

    /*      SLAVE       */
    PALETSX3_X_JAUNE(0),
    PALETSX3_Y_JAUNE(0),
    PALETSX3_X_VIOLET(0),
    PALETSX3_Y_VIOLET(0),

    GOLDENIUM_X_JAUNE(0),
    GOLDENIUM_Y_JAUNE(0),
    GOLDENIUM_X_VIOLET(0),
    GOLDENIUM_Y_VIOLET(0),

    GETBLUEACC_X_JAUNE(0),
    GETBLUEACC_Y_JAUNE(0),
    GETBLUEACC_X_VIOLET(0),
    GETBLUEACC_Y_VIOLET(0),

    GETBLUEACC_X_RETRAIT_JAUNE(0),
    GETBLUEACC_Y_RETRAIT_JAUNE(0),
    GETBLUEACC_X_RETRAIT_VIOLET(0),
    GETBLUEACC_Y_RETRAIT_VIOLET(0),

    SECONDAIRE_BALANCE_OFFSET_X_JAUNE(0),
    SECONDAIRE_BALANCE_OFFSET_X_VIOLET(20),
    SECONDAIRE_BALANCE_OFFSET_Y_JAUNE(0),
    SECONDAIRE_BALANCE_OFFSET_Y_VIOLET(0),

    MOVE_GOLDENIUM_JAUNE(0),
    MOVE_GOLDENIUM_VIOLET(0),

    GOLDENIUM_GOTO_X_JAUNE(0),
    GOLDENIUM_GOTO_Y_JAUNE(0),
    GOLDENIUM_GOTO_X_VIOLET(0),
    GOLDENIUM_GOTO_Y_VIOLET(0),
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
