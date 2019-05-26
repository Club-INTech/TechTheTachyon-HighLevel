package utils.communication;

import com.fazecast.jSerialComm.SerialPort;
import utils.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static com.fazecast.jSerialComm.SerialPort.TIMEOUT_SCANNER;

/**
 * Permet de communiquer en série avec le LL
 */
public class SerialInterface implements CommunicationInterface {

    private static int BAUD_RATE = 2_000_000;
    private final boolean mandatory;
    private SerialPort port;
    private PrintStream printer;
    private boolean open;
    private BufferedReader reader;

    public SerialInterface(String ip, int port, boolean mandatory) {
        // on n'utilise pas l'IP ni le port
        this.mandatory = mandatory;
    }

    @Override
    public boolean send(String message) throws CommunicationException {
        if (port != null) {
            printer.println(message + "\n");
            printer.flush();
            return true;
        }
        return false;
    }

    @Override
    public Optional<String> read() throws CommunicationException {
        if (port != null) {
            try {
                if (reader.ready()) {
                    String line = reader.readLine();
                    if(line.startsWith("[Dynamixel-Com]")) {
                        Log.DYNAMIXEL_COM.debug(line);
                    }
                    //System.out.println("[Serial] Received: " + line);
                    return Optional.of(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
                throw new CommunicationException(e);
            }
        }
        return Optional.empty();
    }

    @Override
    public void init() throws CommunicationException {
        this.port = SerialPort.getCommPort("/dev/ttyACM0");
        Log.COMMUNICATION.debug("Ouverture de " + port.getSystemPortName());
        port.setBaudRate(BAUD_RATE);
        port.setNumStopBits(1);
        port.setParity(0);
        port.setNumDataBits(8);
        port.setComPortTimeouts(TIMEOUT_SCANNER, 0, 0);
        new Thread(() -> {
            while(!port.openPort()) {
                Log.COMMUNICATION.critical("Echec de l'ouverture du port! Réessai dans 0.5s");
                try {
                    TimeUnit.MILLISECONDS.sleep(500);
                } catch (InterruptedException e) {
                    break;
                }
            }
            this.printer = new PrintStream(port.getOutputStream());
            this.reader = new BufferedReader(new InputStreamReader(port.getInputStream()));
            open = true;
        }).start();
    }

    @Override
    public void close() throws CommunicationException {
        port.closePort();
        open = false;
    }

    @Override
    public boolean isMandatory() {
        return mandatory;
    }

    @Override
    public boolean isInterfaceOpen() {
        return open;
    }
}
