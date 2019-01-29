package data;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public enum CouleurPalet {
    VERT,
    ROUGE,
    BLEU,
    GOLDENIUM,
    /**
     * Le bas niveau renvoit un message d'erreur: pas de couleur
     */
    INVISIBLE,
    ;

    static CompletableFuture<CouleurPalet> couleurPalRecue;
    //static CouleurPalet couleurPalRecue;

    static {
        couleurPalRecue = new CompletableFuture<>();
    }


    public static void setCouleurPalRecu(String couleurRecue) {
        switch (couleurRecue) {
            case "vert":
                couleurPalRecue.complete(CouleurPalet.VERT);
                break;
            case "rouge":
                couleurPalRecue.complete(CouleurPalet.ROUGE);
                break;
            case "bleu":
                couleurPalRecue.complete(CouleurPalet.BLEU);
                break;
            case "goldenium":
                couleurPalRecue.complete(CouleurPalet.GOLDENIUM);
                break;
            default:
                couleurPalRecue.complete(CouleurPalet.INVISIBLE);
                break;
        }
    }

    public static CouleurPalet getCouleurPalRecu(){
        CouleurPalet couleurPalet = CouleurPalet.INVISIBLE ;
        try {
            couleurPalet = couleurPalRecue.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return couleurPalet ;
    }
}
