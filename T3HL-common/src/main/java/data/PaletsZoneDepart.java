package data;


public enum PaletsZoneDepart {

    /**
     * Instances de PaletsZoneDepart suivant les cases de départ
     */

    PALET_R(CouleurPalet.ROUGE),
    PALET_G(CouleurPalet.ROUGE),
    PALET_B(CouleurPalet.VERT)

    ;


    private CouleurPalet couleur;


    /**
     */
    PaletsZoneDepart(CouleurPalet couleur) {
        this.couleur = couleur;
    }

    public void setCouleur(String couleur) {
        switch (couleur) {
            case "red":
                this.couleur = CouleurPalet.ROUGE;
                break;
            case "green":
                this.couleur = CouleurPalet.VERT;
        }
    }

    public CouleurPalet getCouleur() {
        return couleur;
    }
}
