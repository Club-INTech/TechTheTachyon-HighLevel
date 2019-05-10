package utils.communication;

import com.fazecast.jSerialComm.SerialPort;
import utils.Log;

import java.io.PrintStream;
import java.util.Optional;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import static com.fazecast.jSerialComm.SerialPort.TIMEOUT_SCANNER;

/**
 * Permet de communiquer en série avec le LL
 */
public class SerialInterface implements CommunicationInterface {

    private SerialPort port;
    private PrintStream printer;
    private boolean open;
    private Scanner scanner;

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
            if (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if(line.startsWith("[Dynamixel-Com]")) {
                    System.out.println(line);
                }
             //   System.out.println("=> Received: " + line);
                return Optional.of(line);
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
            this.scanner = new Scanner(port.getInputStream());
            open = true;
        }).start();
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
