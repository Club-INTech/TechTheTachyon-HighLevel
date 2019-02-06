package data;

import data.table.StillCircularObstacle;

public enum Palet {
    ROUGE_1_ZONE_DEPART_US(CouleurPalet.ROUGE, false),
    ROUGE_2_ZONE_DEPART_US(CouleurPalet.ROUGE, false),
    VERT_ZONE_DEPART_US(CouleurPalet.VERT, false),
    ROUGE_1_ZONE_CHAOS_US(CouleurPalet.ROUGE,false),
    ROUGE_2_ZONE_CHAOS_US(CouleurPalet.ROUGE,false),
    BLEU_ZONE_CHAOS_US(CouleurPalet.BLEU,false),
    VERT_ZONE_CHAOS_US(CouleurPalet.VERT,false),
    ROUGE_1_ZONE_CHAOS_ENNEMI(CouleurPalet.ROUGE,false),
    ROUGE_2_ZONE_CHAOS_ENNEMI(CouleurPalet.ROUGE,false),
    BLEU_ZONE_CHAOS_ENNEMI(CouleurPalet.BLEU,false),
    VERT_ZONE_CHAOS_ENNEMI(CouleurPalet.VERT,false),
    ROUGE_1_ZONE_X6_US(CouleurPalet.ROUGE,false),
    VERT_1_ZONE_X6_US(CouleurPalet.VERT,false),
    ROUGE_2_ZONE_X6_US(CouleurPalet.ROUGE,false),
    VERT_2_ZONE_X6_US(CouleurPalet.VERT,false),
    ROUGE_3_ZONE_X6_US(CouleurPalet.ROUGE,false),
    BLEU_ZONE_X6_US(CouleurPalet.BLEU,false),
    ROUGE_1_ZONE_X6_ENNEMI(CouleurPalet.ROUGE,false),
    VERT_1_ZONE_X6_ENNEMI(CouleurPalet.VERT,false),
    ROUGE_2_ZONE_X6_ENNEMI(CouleurPalet.ROUGE,false),
    VERT_2_ZONE_X6_ENNEMI(CouleurPalet.VERT,false),
    ROUGE_3_ZONE_X6_ENNEMI(CouleurPalet.ROUGE,false),
    BLEU_ZONE_X6_ENNEMI(CouleurPalet.BLEU,false),
    ROUGE_ZONE_X3(CouleurPalet.ROUGE,false),
    BLEU_ZONE_X3(CouleurPalet.BLEU,false),
    VERT_ZONE_X3(CouleurPalet.VERT,false),





    ;
    private CouleurPalet couleur;
    private boolean palet_pris;

    Palet(CouleurPalet couleur, boolean palet_pris) {
        this.couleur = couleur;
        this.palet_pris = palet_pris;
    }
}
