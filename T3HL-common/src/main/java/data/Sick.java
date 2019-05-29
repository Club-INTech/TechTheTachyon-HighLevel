package data;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * Enum des sicks, les numérotations respectent la convention avec le LL
 */
public enum Sick {

    SICK_AVANT(0, -1), //SICK DROIT POUR LE SECONDAIRE
    SICK_AVANT_GAUCHE(1, -1), // SICK ARRIERE DROIT POUR LE SECONDAIRE
    SICK_ARRIERE_GAUCHE(2, -1), //SICK ARRIERE GAUCHE POUR LE SECONDAIRE
    SICK_ARRIERE(3, -1),
    SICK_ARRIERE_DROIT(4, -1),
    SICK_AVANT_DROIT(5, -1),

    ;

    // =====================================================================
    // ==== Capteurs SICK à utiliser selon l'orientation et la position ====
    // =====================================================================
    //RIEN DU TOUT
    public static final Sick[] NOTHING = {};

    public static final Sick[] LOWER_LEFT_CORNER_TOWARDS_PI = {SICK_AVANT, SICK_AVANT_GAUCHE, SICK_ARRIERE_GAUCHE};
    public static final Sick[] UPPER_LEFT_CORNER_TOWARDS_PI = {SICK_AVANT, SICK_AVANT_DROIT, SICK_ARRIERE_DROIT};


    public static final Sick[] LOWER_RIGHT_CORNER_TOWARDS_0 = {SICK_AVANT, SICK_AVANT_DROIT, SICK_ARRIERE_DROIT};
    public static final Sick[] UPPER_RIGHT_CORNER_TOWARDS_0 = {SICK_AVANT, SICK_AVANT_GAUCHE, SICK_ARRIERE_GAUCHE};

    // Symétries côté gauche
    public static final Sick[] LOWER_LEFT_CORNER_TOWARDS_0 = {SICK_ARRIERE, SICK_AVANT_DROIT, SICK_ARRIERE_DROIT};
    public static final Sick[] UPPER_LEFT_CORNER_TOWARDS_0 = {SICK_ARRIERE, SICK_AVANT_GAUCHE, SICK_ARRIERE_GAUCHE};

    // Symétries côté droit
    public static final Sick[] LOWER_RIGHT_CORNER_TOWARDS_PI = {SICK_ARRIERE, SICK_AVANT_GAUCHE, SICK_ARRIERE_GAUCHE};
    public static final Sick[] UPPER_RIGHT_CORNER_TOWARDS_PI = {SICK_ARRIERE, SICK_AVANT_DROIT, SICK_ARRIERE_DROIT};


    //Secondaire

    public static final Sick[] SECONDAIRE= {SICK_AVANT, SICK_AVANT_GAUCHE, SICK_ARRIERE_GAUCHE};

    /**
     * Indice du capteur sick
     */
    private final int indiceSick;

    /**
     * Dernière mesure
     */
    private int lastMeasure;

    private static Sick[] significantSicks = LOWER_LEFT_CORNER_TOWARDS_PI;

    /**
     * La nouvelle position et orientation du robot après calcul
     */
    private static CompletableFuture<XYO> newXYO;
    /**
     * Bloc statique pour instancier les variables statiques
     */
    static {
        newXYO = new CompletableFuture<>();
    }

    /**
     * Constructeur de l'enum
     * @param indiceSick
     */
    Sick(int indiceSick, int lastMeasure){
        this.indiceSick=indiceSick;
        this.lastMeasure = lastMeasure;
    }

    /**
     * Set la valeur du completable
     * @param newXYO
     */
    public static void setNewXYO(XYO newXYO) {
        Sick.newXYO.complete(newXYO);
    }

    /**
     * Get la valeur du XYO
     * @return
     */
    public static XYO getNewXYO() {
        XYO xyo = null;
        try {
            xyo = newXYO.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return  xyo;
    }

    /**
     * Renvoie les indices des sicks qui sont activés
     * @return
     */
    public static Sick[] getSignificantSicks() {
        return significantSicks;
    }

    /**
     * Renvoie la dernière mesure réalisée par le sick
     * @return int
     */
    public int getLastMeasure(){
        return this.lastMeasure;
    }

    /**
     * Set la dernière mesure réalisée par le sick
     */
    public void setLastMeasure(int lastMeasure){
        this.lastMeasure = lastMeasure;
    }

    /**
     * Renvoie toutes les dernières mesures des sicks
     * @return int[]
     */
    public static int[] getLastMeasures(){
        int[] lastMeasures = new int[6];
        Sick[] sicks = Sick.values();
        for (int i = 0; i < sicks.length; i++){
            lastMeasures[i] = sicks[i].getLastMeasure();
        }
        return lastMeasures;
    }


    public static void setSignificantSicks(Sick[] sicks) {
        significantSicks = sicks;
    }

    public static void resetNewXYO() {
        // on ne peut pas complete un CompletableFuture deux fois de suite
        newXYO = new CompletableFuture<>();
    }

    public int getIndex() {
        return indiceSick;
    }
}
