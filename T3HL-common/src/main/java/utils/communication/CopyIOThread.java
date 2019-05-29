package utils.communication;

import utils.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Thread qui permet de transférer les entrées/sorties d'un Process vers la sortie standard grâce à Log
 */
public class CopyIOThread extends Thread {

    private final BufferedReader stdoutReader;
    private final BufferedReader errReader;
    private Log output;

    public CopyIOThread(Process process, Log output) {
        super(process.toString());
        setDaemon(true);
        this.output = output;
        this.stdoutReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        this.errReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
    }

    @Override
    public void run() {
        while(!isInterrupted()) {
            try {
                if(stdoutReader.ready()) {
                    String line = stdoutReader.readLine();
                    output.debug(line);
                }
                if(errReader.ready()) {
                    String line = errReader.readLine();
                    output.critical(line);
                }
                Thread.sleep(100);
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
            }
        }
    }
}
