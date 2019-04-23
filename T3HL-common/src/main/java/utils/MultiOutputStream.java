package utils;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Classe permettant d'écrire dans plusieurs {@link OutputStream} à la fois
 * @author jglrxavpok
 */
public class MultiOutputStream extends OutputStream {
    private final OutputStream[] outputs;

    public MultiOutputStream(OutputStream... outputs) {
        this.outputs = outputs;
    }

    // Réimplementation des opérations I/O pour prendre en compte les multiples sorties

    @Override
    public void write(int b) throws IOException {
        for(OutputStream out : outputs) {
            if(out != null) {
                out.write(b);
            }
        }
    }

    @Override
    public void write(byte[] b) throws IOException {
        for(OutputStream out : outputs) {
            if(out != null) {
                out.write(b);
            }
        }
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        for(OutputStream out : outputs) {
            if(out != null) {
                out.write(b, off, len);
            }
        }
    }

    @Override
    public void flush() throws IOException {
        for (OutputStream out : outputs) {
            if(out != null) {
                out.flush();
            }
        }
    }

    @Override
    public void close() throws IOException {
        for (OutputStream out : outputs) {
            if(out != null) {
                out.close();
            }
        }
    }
}
