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
    PALETSX6_Y_VIOLET(4),

    ACCELERATEUR_Y_VIOLET(6),
    ACCELERATEUR_Y_JAUNE(3),

    /*      SLAVE       */

    ;

    private int offset;


    Offsets(int offset){
        this.offset=offset;
    }

    public int get() {
        return offset;
    }

    public static void loadFromConfig(Config config) {
        for(Offsets offset : values()) {
            offset.offset = config.getInt(offset);
        }
    }

    @Override
    public Integer getDefaultValue() {
        return offset;
    }
}
