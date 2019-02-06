package data;

import data.table.StillCircularObstacle;

public enum Palet {
    ROUGE_1_ZONE_DEPART(CouleurPalet.ROUGE, false),
    ROUGE_2_ZONE_DEPART(CouleurPalet.ROUGE, false),
    VERT_ZONE_DEPART(CouleurPalet.VERT, false),
    ROUGE_1_ZONE_CHAOS(CouleurPalet.ROUGE,false),
    ROUGE_2_ZONE_CHAOS(CouleurPalet.ROUGE,false),
    

    ;
    private CouleurPalet couleur;
    private boolean palet_pris;

    Palet(CouleurPalet couleur, boolean palet_pris) {
        this.couleur = couleur;
        this.palet_pris = palet_pris;
    }
}
