package data;

public enum Palet {

    ROUGE_1_ZONE_DEPART_US(0,CouleurPalet.ROUGE, false,false),
    ROUGE_2_ZONE_DEPART_US(1,CouleurPalet.ROUGE, false,false),
    VERT_ZONE_DEPART_US(2,CouleurPalet.VERT, false,false),


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

    /**
     * Constructeur
     * @param id id du palet
     * @param couleur couleur du palet
     * @param palet_pris si le palet a été pris (ie enlevé de son emplacement de base)
     * @param palet_marque si la palet a été marqué (ie des points ont été marqué grâce à ce palet)
     */
    Palet(int id, CouleurPalet couleur, boolean palet_pris , boolean palet_marque) {
        this.couleur = couleur;
        this.palet_pris = palet_pris;
        this.palet_marque = palet_marque;
        this.id = id;
    }

    /**
     * Renvoie l'id du palet
     * @return renvoie l'id du palet
     */
    public int getId(){
        return this.id;
    }

    /**
     * Renvoie la couleur du palet
     * @return renvoie un objet CouleurPalet correspondant à la couleur du palet
     */
    public CouleurPalet getCouleur(){
        return this.couleur;
    }

    /**
     * Renvoie le palet correspondant à l'id donné
     * @param id id du palet à rechercher
     * @return renvoie le palet correspondant à l'id, ou null si aucun palet n'est trouvé
     */
    public static Palet getPaletById(int id){
        for (Palet palet : values()){
            if (palet.id == id){
                return palet;
            }
        }
        return null;
    }

    /**
     * Renvoie si le palet a été pris
     * @return true si le palet a été pris, false sinon
     */
    public boolean getPaletPris(){ return this.palet_pris; }

    /**
     * Set si le palet a été pris
     * @param valeur true si le palet a été pris, false sinon
     */
    public void setPaletPris(boolean valeur){ this.palet_pris = valeur; }

    /**
     * Renvoie si le palet a été marqué
     * @return true si le palet a été marqué, false sinon
     */
    public boolean getPaletMarque(){ return this.palet_marque; }

    /**
     * Set si le palet a été marqué
     * @param valeur true si le palet a été marqué, false sinon
     */
    public void setPaletMarque(boolean valeur){ this.palet_marque =valeur; }

}
