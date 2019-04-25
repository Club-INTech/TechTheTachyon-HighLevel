package utils;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/**
 * PrintStream qui permet de rediger sur un logger particulier. La redirection n'est pas immédiate et est faite lors d'un flush.
 * @author jglrxavpok
 */
public class LogPrintStream extends PrintStream {

    @FunctionalInterface
    public interface LogWritingFunction {
        /**
         *
         * @param logger le logger à utiliser
         * @param message le message à logger
         * @param stackOffset un offset dans la stack de la JVM, utile pour prendre en compte la redirection par ce LogPrintStream
         */
        void write(Log logger, String message, int stackOffset);
    }

    /**
     * Le logger sur lequel rediriger
     */
    private final Log logger;

    /**
     * La fonction utilisée pour l'écriture sur le logger
     */
    private LogWritingFunction writingFunction;

    public LogPrintStream(Log logger, LogWritingFunction writingFunction) {
        // on utilise un BAOS pour pouvoir stocker temporairement le message écrit
        super(new ByteArrayOutputStream(), true);
        this.logger = logger;
        this.writingFunction = writingFunction;
    }

    @Override
    public void flush() {
        // on récupère ce qui a été écrit et on le transmet
        ByteArrayOutputStream baos = (ByteArrayOutputStream) this.out;
        String message = new String(baos.toByteArray());
        if(message.length() == 0) {
            writingFunction.write(logger, "", 2);
        } else {
            writingFunction.write(logger, message.substring(0, message.length()-1 /* Retrait du \n de fin */), 2);
        }
        baos.reset(); // reset pour libérer la place
    }

    // Partie moins fun: réimplementation des println

    @Override
    public void println() {
        super.println();
        flush();
    }

    @Override
    public void println(boolean x) {
        super.println(x);
        flush();
    }

    @Override
    public void println(char x) {
        super.println(x);
        flush();
    }

    @Override
    public void println(int x) {
        super.println(x);
        flush();
    }

    @Override
    public void println(long x) {
        super.println(x);
        flush();
    }

    @Override
    public void println(float x) {
        super.println(x);
        flush();
    }

    @Override
    public void println(double x) {
        super.println(x);
        flush();
    }

    @Override
    public void println(char[] x) {
        super.println(x);
        flush();
    }

    @Override
    public void println(String x) {
        super.println(x);
        flush();
    }

    @Override
    public void println(Object x) {
        super.println(x);
        flush();
    }

}
