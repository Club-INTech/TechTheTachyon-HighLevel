package utils.container;

import utils.TimeoutError;

import java.time.Duration;
import java.util.concurrent.*;

/**
 * Classe qui regroupe à la fois un Thread et un Service. Elle permet de forcer la réimplémentation de toString() pour avoir des noms lisibles dans les profileurs
 *
 * @author jglrxavpok
 */
public abstract class ServiceThread extends Thread implements Service {

    public ServiceThread() {
        super();
        setName(toString());
    }

    public abstract void run();

    // On oblige à réimplémenter la méthode pour que les profileurs donnent des noms lisibles
    public String toString() {
        return getClass().getSimpleName();
    }

    public void withTimeout(long timeoutMillis, Runnable runnable) throws TimeoutError {
        ExecutorService executor = Executors.newSingleThreadExecutor();

        final Future<Void> handler = executor.submit((Callable) () -> {
            runnable.run();
            return null;
        });

        try {
            handler.get(timeoutMillis, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            handler.cancel(true);
            throw new TimeoutError("Timeout of "+timeoutMillis+" expired!");
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            throw new TimeoutError("Timeout of "+timeoutMillis+" expired due to an error: ", e);
        }

        executor.shutdownNow();
    }

}
