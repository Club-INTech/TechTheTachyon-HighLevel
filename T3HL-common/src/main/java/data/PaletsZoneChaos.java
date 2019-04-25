package data;

import utils.math.Vec2;
import utils.math.VectCartesian;

import java.util.ArrayList;

public enum PaletsZoneChaos {
    /**
     * Instances de paletsZoneChaos avec des positions et des couleurs par défaut
     */

    RED_1_ZONE_CHAOS_PURPLE(3,CouleurPalet.ROUGE,false,false,new VectCartesian(-436,1050)),
    RED_2_ZONE_CHAOS_PURPLE(4,CouleurPalet.ROUGE,false,false,new VectCartesian(-539,1050)),
    BLUE_ZONE_CHAOS_PURPLE(5,CouleurPalet.BLEU,false,false,new VectCartesian(-500,1011)),
    GREEN_ZONE_CHAOS_PURPLE(6,CouleurPalet.VERT,false,false,new VectCartesian(-500,1089)),

    RED_1_ZONE_CHAOS_YELLOW(7,CouleurPalet.ROUGE,false,false,new VectCartesian(426,1050)),
    RED_2_ZONE_CHAOS_YELLOW(8,CouleurPalet.ROUGE,false,false,new VectCartesian(574,1050)),
    BLUE_ZONE_CHAOS_YELLOW(9,CouleurPalet.BLEU,false,false,new VectCartesian(500,976)),
    GREEN_ZONE_CHAOS_YELLOW(10,CouleurPalet.VERT,false,false,new VectCartesian(501,1124)),
    ;

    private int id;
    private CouleurPalet couleur;
    private boolean palet_pris;
    private boolean palet_marque;
    private Vec2 position;

    PaletsZoneChaos(int id, CouleurPalet couleur, boolean palet_pris , boolean palet_marque, Vec2 position) {
        this.couleur = couleur;
        this.palet_pris = palet_pris;
        this.palet_marque = palet_marque;
        this.id = id;
        this.position = position;
    }

    public void setPosition(Vec2 position) {
        this.position = position;
    }

    public Vec2 getPosition() {
        return position;
    }
}