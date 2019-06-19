package utils;

import utils.container.ModuleThread;

/**
 * Exception levée par les {@link ModuleThread}
 * <br/>
 * On pourrait utiliser les {@link java.util.concurrent.TimeoutException} mais il n'y a pas de constructeur prennant une exception en tant que cause.
 * C'est pas super super grave mais c'est plus explicite d'avoir la cause quand il y en a une.
 *
 * Classe fille de {@link RuntimeException} pour ne pas être obligé d'utiliser des try-catch
 * @author jglrxavpok
 */
public class TimeoutError extends RuntimeException {

    public TimeoutError(String message) {
        super(message);
    }

    public TimeoutError(String message, Throwable cause) {
        super(message, cause);
    }

    public TimeoutError(Throwable cause) {
        super(cause);
    }

}
