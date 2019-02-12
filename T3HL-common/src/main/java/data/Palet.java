package data;

import data.table.StillCircularObstacle;

public enum Palet {
    ROUGE_1_ZONE_DEPART_US(0,CouleurPalet.ROUGE, false,false),
    ROUGE_2_ZONE_DEPART_US(1,CouleurPalet.ROUGE, false,false),
    VERT_ZONE_DEPART_US(2,CouleurPalet.VERT, false,false),
    ROUGE_1_ZONE_CHAOS_US(3,CouleurPalet.ROUGE,false,false),
    ROUGE_2_ZONE_CHAOS_US(4,CouleurPalet.ROUGE,false,false),
    BLEU_ZONE_CHAOS_US(5,CouleurPalet.BLEU,false,false),
    VERT_ZONE_CHAOS_US(6,CouleurPalet.VERT,false,false),
    ROUGE_1_ZONE_CHAOS_ENNEMI(7,CouleurPalet.ROUGE,false,false),
    ROUGE_2_ZONE_CHAOS_ENNEMI(8,CouleurPalet.ROUGE,false,false),
    BLEU_ZONE_CHAOS_ENNEMI(9,CouleurPalet.BLEU,false,false),
    VERT_ZONE_CHAOS_ENNEMI(10,CouleurPalet.VERT,false,false),
    ROUGE_1_ZONE_X6_US(11,CouleurPalet.ROUGE,false,false),
    VERT_1_ZONE_X6_US(12,CouleurPalet.VERT,false,false),
    ROUGE_2_ZONE_X6_US(13,CouleurPalet.ROUGE,false,false),
    VERT_2_ZONE_X6_US(14,CouleurPalet.VERT,false,false),
    ROUGE_3_ZONE_X6_US(15,CouleurPalet.ROUGE,false,false),
    BLEU_ZONE_X6_US(16,CouleurPalet.BLEU,false,false),
    ROUGE_1_ZONE_X6_ENNEMI(17,CouleurPalet.ROUGE,false,false),
    VERT_1_ZONE_X6_ENNEMI(18,CouleurPalet.VERT,false,false),
    ROUGE_2_ZONE_X6_ENNEMI(19,CouleurPalet.ROUGE,false,false),
    VERT_2_ZONE_X6_ENNEMI(20,CouleurPalet.VERT,false,false),
    ROUGE_3_ZONE_X6_ENNEMI(21,CouleurPalet.ROUGE,false,false),
    BLEU_ZONE_X6_ENNEMI(22,CouleurPalet.BLEU,false,false),
    ROUGE_ZONE_X3(23,CouleurPalet.ROUGE,false,false),
    BLEU_ZONE_X3(24,CouleurPalet.BLEU,false,false),
    VERT_ZONE_X3(25,CouleurPalet.VERT,false,false),
    GOLDENIUM(26,CouleurPalet.GOLDENIUM,false,false),

    ;
    private int id;
    private CouleurPalet couleur;
    private boolean palet_pris;
    private boolean palet_marque;

    Palet(int id, CouleurPalet couleur, boolean palet_pris , boolean palet_marque) {
        this.couleur = couleur;
        this.palet_pris = palet_pris;
        this.palet_marque = palet_marque;
        this.id = id;
    }

    public int getId(){
        return this.id;
    }

    public CouleurPalet getCouleur(){
        return this.couleur;
    }

    public void setPaletPris(boolean valeur){
            this.palet_pris= valeur;
    }

    public boolean getPaletPris(){ return this.palet_pris;}

    public boolean getPaletMarque(){return this.palet_marque;}

    public void setPaletMarque(boolean valeur){ this.palet_marque =valeur;}





}
