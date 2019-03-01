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
    /**
     * Indice du capteur sick
     */
    private final int indiceSick;
    /**
     * Indique si on prend en compte les mesures du sick ou pas
     */
    private boolean significant;

    /**
     * La nouvelle position et orientation du robot après calcul
     */
    private static CompletableFuture<XYO> newXYO;
    /**
     * Bloc statique pour instancier les variables statiques
     */
    static {
        newXYO=new CompletableFuture<>();
    }

    /**
     * Constructeur de l'enum
     * @param indiceSick
     */
    Sick(int indiceSick){
        this.indiceSick=indiceSick;
    }

    /**
     * Indique si le sick est signifiant ou pas
     * @return
     */
    public boolean isSignificant() {
        return significant;
    }

    public void setSignificant(boolean significant) {
        this.significant = significant;
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
    public static int[] getSignificantSicks(){
        //TODO Définir les véritables SignificantSicks
        // Il faut impérativement que int[0] soit égal à 0 ou 3
        int[] tab = new int[3];
        tab[1]=1;
        tab[2]=2;
        return tab;
    }


}
