package data;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * Enum des sicks, les numérotations respectent la convention avec le LL
 */
public enum Sick {

    SICK_AVANT(0),
    SICK_AVANT_GAUCHE(1),
    SICK_ARRIERE_GAUCHE(2),
    SICK_ARRIERE(3),
    SICK_ARRIERE_DROIT(4),
    SICK_AVANT_DROIT(5),

    ;

    // =====================================================================
    // ==== Capteurs SICK à utiliser selon l'orientation et la position ====
    // =====================================================================
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

    /**
     * Indice du capteur sick
     */
    private final int indiceSick;

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
    Sick(int indiceSick){
        this.indiceSick=indiceSick;
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


    public static void setSignificantSicks(Sick[] sicks) {
        significantSicks = sicks;
    }

    public int getIndex() {
        return indiceSick;
    }
}
