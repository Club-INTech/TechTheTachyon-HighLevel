package data;

import utils.math.Vec2;
import utils.math.InternalVectCartesian;

public enum PaletsZoneChaos {
    /**
     * Instances de paletsZoneChaos avec des positions et des couleurs par défaut
     */



    RED_1_ZONE_CHAOS_PURPLE(3,CouleurPalet.ROUGE,false,false,new InternalVectCartesian(476,1151)),
    RED_2_ZONE_CHAOS_PURPLE(4,CouleurPalet.ROUGE,false,false,new InternalVectCartesian(585,1101)),
    BLUE_ZONE_CHAOS_PURPLE(5,CouleurPalet.BLEU,false,false,new InternalVectCartesian(504,1003)),
    GREEN_ZONE_CHAOS_PURPLE(6,CouleurPalet.VERT,false,false,new InternalVectCartesian(410,1062)),

    RED_1_ZONE_CHAOS_YELLOW(7,CouleurPalet.ROUGE,false,false,new InternalVectCartesian(426,1050)),
    RED_2_ZONE_CHAOS_YELLOW(8,CouleurPalet.ROUGE,false,false,new InternalVectCartesian(574,1050)),
    BLUE_ZONE_CHAOS_YELLOW(9,CouleurPalet.BLEU,false,false,new InternalVectCartesian(500,976)),
    GREEN_ZONE_CHAOS_YELLOW(10,CouleurPalet.VERT,false,false,new InternalVectCartesian(501,1124)),
    ;

    private int id;
    private CouleurPalet couleur;
    private boolean palet_pris;
    private boolean palet_marque;
    private Vec2 position;

    /**
     * Constructeur
     * @param id id du palet
     * @param couleur couleur du palet
     * @param palet_pris est-ce que le palet a été pris ?
     * @param palet_marque est-ce que le palet a été déposé pour marquer des points ?
     * @param position position du palet sur la table
     */
    PaletsZoneChaos(int id, CouleurPalet couleur, boolean palet_pris , boolean palet_marque, Vec2 position) {
        this.id = id;
        this.couleur = couleur;
        this.palet_pris = palet_pris;
        this.palet_marque = palet_marque;
        this.position = position;
    }

    /**
     * Set la position du palet
     * @param position Vec2 de la position
     */
    public void setPosition(Vec2 position) {
        this.position = position;
    }

    /**
     * Retourne la position du palet
     * @return Vec2 de la position du palet
     */
    public Vec2 getPosition() {
        return position;
    }

    /**
     * Set si la palet a été pris
     * @param value True si le palet a été pris, False sinon
     */
    public void setPaletPris(boolean value){
        this.palet_pris = value;
    }

    /**
     * Renvoie si le palet a été pris
     * @return True si le palet a été pris, False sinon
     */
    public boolean getPaletPris(){
        return this.palet_pris;
    }

    /**
     * Set si le palet a été déposé pour marquer des points
     * @param value True si c'est le cas, False sinon
     */
    public void setPaletMarque(boolean value){
        this.palet_marque = value;
    }

    /**
     * Get si le palet a été déposé pour marquer des points
     * @return True si c'est le cas, False sinon
     */
    public boolean getPaletMarque(){
        return this.palet_marque;
    }

}