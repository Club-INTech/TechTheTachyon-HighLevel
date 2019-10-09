package utils;

import pfg.config.BaseConfigInfo;
import pfg.config.Config;
import pfg.config.ConfigInfo;

import java.util.HashMap;
import java.util.Map;

/**
 * Liste des offsets disponibles pour les scripts
 * @author mikail, jglrxavpok
 */
public interface Offsets {

    /*
        ATTENTION! S'il y a des valeurs dans config.txt, les valeurs en dessous seront écrasées! (Ce sont des valeurs par défaut)
     */


    /*      MASTER      */
    ConfigInfo<Integer> PALETSX6_X_JAUNE = new BaseConfigInfo<>(-2, Integer.TYPE);
    ConfigInfo<Integer> PALETSX6_Y_JAUNE = new BaseConfigInfo<>(-4, Integer.TYPE);
    ConfigInfo<Integer> PALETSX6_X_VIOLET = new BaseConfigInfo<>(20, Integer.TYPE);
    ConfigInfo<Integer> PALETSX6_Y_VIOLET = new BaseConfigInfo<>(10, Integer.TYPE);
    ConfigInfo<Double> PALETSX6_THETA_VIOLET = new BaseConfigInfo<>(0.025/* atan(10.0/100.0) */, Double.TYPE);

    ConfigInfo<Integer> ACCELERATEUR_Y_VIOLET = new BaseConfigInfo<>(6, Integer.TYPE);
    ConfigInfo<Integer> ACCELERATEUR_Y_JAUNE = new BaseConfigInfo<>(3, Integer.TYPE);
    ConfigInfo<Integer> PALETS_X6_BALANCE_Y_JAUNE = new BaseConfigInfo<>(0, Integer.TYPE);
    ConfigInfo<Integer> PALETS_X6_BALANCE_Y_VIOLET = new BaseConfigInfo<>(10, Integer.TYPE);

    ConfigInfo<Integer> ZDD_X_VIOLET = new BaseConfigInfo<>(5, Integer.TYPE);
    ConfigInfo<Integer> ZDD_X_JAUNE = new BaseConfigInfo<>(5, Integer.TYPE);
    ConfigInfo<Integer> ZDD_Y_JAUNE = new BaseConfigInfo<>(-5, Integer.TYPE);
    ConfigInfo<Integer> ZDD_Y_VIOLET = new BaseConfigInfo<>(-5, Integer.TYPE);
    ConfigInfo<Integer> ZDD_POST_BALANCE_X_JAUNE = new BaseConfigInfo<>(0, Integer.TYPE);
    ConfigInfo<Integer> ZDD_POST_BALANCE_X_VIOLET = new BaseConfigInfo<>(0, Integer.TYPE);
    ConfigInfo<Integer> ZDD_POST_BALANCE_Y_JAUNE = new BaseConfigInfo<>(0, Integer.TYPE);
    ConfigInfo<Integer> ZDD_POST_BALANCE_Y_VIOLET = new BaseConfigInfo<>(0, Integer.TYPE);

    ConfigInfo<Integer> ACCELERATEUR_Y_RECALAGE_JAUNE = new BaseConfigInfo<>(100, Integer.TYPE);
    ConfigInfo<Integer> ACCELERATEUR_Y_RECALAGE_VIOLET = new BaseConfigInfo<>(100, Integer.TYPE);
    ConfigInfo<Double> ACCELERATEUR_THETA_RECALAGE_JAUNE = new BaseConfigInfo<>(0.1, Double.TYPE);
    ConfigInfo<Double> ACCELERATEUR_THETA_RECALAGE_VIOLET = new BaseConfigInfo<>(0.1, Double.TYPE);
    ConfigInfo<Double> ACCELERATEUR_THETA_RECALAGE_JAUNE_COTE_2 = new BaseConfigInfo<>(0.1, Double.TYPE);
    ConfigInfo<Double> ACCELERATEUR_THETA_RECALAGE_VIOLET_COTE_2 = new BaseConfigInfo<>(0.1, Double.TYPE);

    /*      SLAVE       */
    ConfigInfo<Integer> PALETSX3_X_JAUNE = new BaseConfigInfo<>(0, Integer.TYPE);
    ConfigInfo<Integer> PALETSX3_Y_JAUNE = new BaseConfigInfo<>(0, Integer.TYPE);
    ConfigInfo<Integer> PALETSX3_X_VIOLET = new BaseConfigInfo<>(0, Integer.TYPE);
    ConfigInfo<Integer> PALETSX3_Y_VIOLET = new BaseConfigInfo<>(0, Integer.TYPE);

    ConfigInfo<Integer> GOLDENIUM_X_JAUNE = new BaseConfigInfo<>(0, Integer.TYPE);
    ConfigInfo<Integer> GOLDENIUM_Y_JAUNE = new BaseConfigInfo<>(0, Integer.TYPE);
    ConfigInfo<Integer> GOLDENIUM_X_VIOLET = new BaseConfigInfo<>(0, Integer.TYPE);
    ConfigInfo<Integer> GOLDENIUM_Y_VIOLET = new BaseConfigInfo<>(0, Integer.TYPE);

    ConfigInfo<Integer> GETBLUEACC_X_JAUNE = new BaseConfigInfo<>(0, Integer.TYPE);
    ConfigInfo<Integer> GETBLUEACC_Y_JAUNE = new BaseConfigInfo<>(0, Integer.TYPE);
    ConfigInfo<Integer> GETBLUEACC_X_VIOLET = new BaseConfigInfo<>(0, Integer.TYPE);
    ConfigInfo<Integer> GETBLUEACC_Y_VIOLET = new BaseConfigInfo<>(0, Integer.TYPE);

    ConfigInfo<Integer> GETBLUEACC_X_RETRAIT_JAUNE = new BaseConfigInfo<>(0, Integer.TYPE);
    ConfigInfo<Integer> GETBLUEACC_Y_RETRAIT_JAUNE = new BaseConfigInfo<>(0, Integer.TYPE);
    ConfigInfo<Integer> GETBLUEACC_X_RETRAIT_VIOLET = new BaseConfigInfo<>(0, Integer.TYPE);
    ConfigInfo<Integer> GETBLUEACC_Y_RETRAIT_VIOLET = new BaseConfigInfo<>(0, Integer.TYPE);

    ConfigInfo<Integer> SECONDAIRE_BALANCE_OFFSET_X_JAUNE = new BaseConfigInfo<>(0, Integer.TYPE);
    ConfigInfo<Integer> SECONDAIRE_BALANCE_OFFSET_X_VIOLET = new BaseConfigInfo<>(20, Integer.TYPE);
    ConfigInfo<Integer> SECONDAIRE_BALANCE_OFFSET_Y_JAUNE = new BaseConfigInfo<>(0, Integer.TYPE);
    ConfigInfo<Integer> SECONDAIRE_BALANCE_OFFSET_Y_VIOLET = new BaseConfigInfo<>(0, Integer.TYPE);

    ConfigInfo<Integer> MOVE_GOLDENIUM_JAUNE = new BaseConfigInfo<>(517, Integer.TYPE);
    ConfigInfo<Integer> MOVE_GOLDENIUM_VIOLET = new BaseConfigInfo<>(517, Integer.TYPE);

    ConfigInfo<Integer> GOLDENIUM_GOTO_X_JAUNE = new BaseConfigInfo<>(0, Integer.TYPE);
    ConfigInfo<Integer> GOLDENIUM_GOTO_Y_JAUNE = new BaseConfigInfo<>(0, Integer.TYPE);
    ConfigInfo<Integer> GOLDENIUM_GOTO_X_VIOLET = new BaseConfigInfo<>(0, Integer.TYPE);
    ConfigInfo<Integer> GOLDENIUM_GOTO_Y_VIOLET = new BaseConfigInfo<>(0, Integer.TYPE);

    ConfigInfo<Integer> DECALAGE_GOLD_JAUNE = new BaseConfigInfo<>(20, Integer.TYPE);
    ConfigInfo<Integer> DECALAGE_GOLD_VIOLET = new BaseConfigInfo<>(20, Integer.TYPE);
    ;

    ConfigInfo<?>[] ALL_VALUES = ConfigInfo.findAllIn(Offsets.class);
    Map<ConfigInfo<?>, Object> OFFSET_CACHE = new HashMap<>();

    static ConfigInfo<?>[] values() {
        return ALL_VALUES;
    }

    static void loadFromConfig(Config config) {
        for(ConfigInfo<?> offset : values()) {
            OFFSET_CACHE.put(offset, config.get(offset));
        }
    }

    @SuppressWarnings("unchecked cast")
    static <Type> Type get(ConfigInfo<Type> offset) {
        return (Type)OFFSET_CACHE.get(offset);
    }

}
