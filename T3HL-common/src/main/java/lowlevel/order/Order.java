package lowlevel.order;

/**
 * Ordre abstrait
 *
 * @author jglrxavpok
 */
public interface Order {

    /**
     * Renvoie un String contenant l'ordre à envoyer au LL. (e.g. "pump left on" pour activer la pompe)
     * @return
     */
     String toLL();

}
