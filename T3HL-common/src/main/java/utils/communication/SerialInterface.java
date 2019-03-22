package utils.communication;

import com.fazecast.jSerialComm.SerialPort;
import utils.ConfigData;
import utils.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.Optional;

import static com.fazecast.jSerialComm.SerialPort.TIMEOUT_READ_BLOCKING;
import static com.fazecast.jSerialComm.SerialPort.TIMEOUT_WRITE_BLOCKING;

/**
 * Permet de communiquer en série avec le LL
 */
public class SerialInterface implements CommunicationInterface {

    private SerialPort port;
    private PrintStream printer;
    private BufferedReader reader;
    private boolean open;

    public SerialInterface(String ip, int port) {
        // on n'utilise pas l'IP ni le port
    }

    @Override
    public void send(String message) throws CommunicationException {
        if (port != null) {
            printer.println(message + "\n");
            printer.flush();
        }
    }

    @Override
    public Optional<String> read() throws CommunicationException {
        if (port != null) {
            try {
                if (reader.ready()) {
                    try {
                        String line = reader.readLine();
                        System.out.println("=> Received: " + line);
                        return Optional.of(line);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return Optional.empty();
    }

    @Override
    public void init() throws CommunicationException {
        this.port = SerialPort.getCommPort("/dev/ttyACM0");
        Log.COMMUNICATION.debug("Ouverture de " + port.getSystemPortName());
        port.setBaudRate(115200);
        port.setNumStopBits(1);
        port.setParity(0);
        port.setNumDataBits(8);
        port.setComPortTimeouts(TIMEOUT_READ_BLOCKING, 1000, 10000);
        boolean result = port.openPort();
        if (!result) {
            Log.COMMUNICATION.critical("Echec de l'ouverture du port!");
        }
        this.printer = new PrintStream(port.getOutputStream());
        this.reader = new BufferedReader(new InputStreamReader(port.getInputStream()));
        open = true;
    }

    @Override
    public void close() throws CommunicationException {
        port.closePort();
        open = false;
    }

    @Override
    public boolean isInterfaceOpen() {
        return open;
    }
}
