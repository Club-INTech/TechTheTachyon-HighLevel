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
        if(couleurRecue.equals("vert")){
            couleurPalRecue.complete(CouleurPalet.VERT);
        }
        else if(couleurRecue.equals("rouge")){
            couleurPalRecue.complete(CouleurPalet.ROUGE);
        }
        else if(couleurRecue.equals("bleu")){
            couleurPalRecue.complete(CouleurPalet.BLEU);
        }
        else if(couleurRecue.equals("goldenium")){
            couleurPalRecue.complete(CouleurPalet.GOLDENIUM);
        }
        else{
            couleurPalRecue.complete(CouleurPalet.INVISIBLE);
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
