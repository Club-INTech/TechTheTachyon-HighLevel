package utils.container;

/**
 * Classe qui regroupe à la fois un Thread et un Module. Elle permet de forcer la réimplémentation de toString() pour avoir des noms lisibles dans les profileurs
 *
 * @author jglrxavpok
 */
public abstract class ModuleThread extends Thread implements Module {

    public ModuleThread() {
        super();
        setName(toString());
    }

    public abstract void run();

    // On oblige à réimplémenter la méthode pour que les profileurs donnent des noms lisibles
    public String toString() {
        return getClass().getSimpleName();
    }

}
