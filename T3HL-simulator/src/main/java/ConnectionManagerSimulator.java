import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ConnectionManagerSimulator extends Thread {

    private ServerSocket receptionSocket;
    private Socket socket;
    private ConcurrentLinkedQueue<String> receivedMessage;
    private BufferedReader incoming;
    private BufferedWriter outgoing;

    private boolean onlyReceivingSimulatedMessages;

    /** Constructeur
     * @param port port sur lequel écoute le simulateur
     */
    ConnectionManagerSimulator(int port, boolean onlyReceivingSimulatedMessages){
        //On initialise le dernier message reçu et le serveur Socket
        this.onlyReceivingSimulatedMessages=onlyReceivingSimulatedMessages;
        this.receivedMessage = new ConcurrentLinkedQueue<String>();
        if (!this.onlyReceivingSimulatedMessages) {
            try {
                this.receptionSocket = new ServerSocket(port);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                this.socket=this.receptionSocket.accept();
                this.incoming = new BufferedReader(new InputStreamReader(this.socket.getInputStream(), StandardCharsets.UTF_8.name()));
                this.outgoing = new BufferedWriter(new OutputStreamWriter(this.socket.getOutputStream(), StandardCharsets.UTF_8.name()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.start();
    }

    @Override
    /** Ecoute les messages et les ajoute à la queue des messages reçus*/
    public void run() {
        //On boucle infiniement pour
        while (true){
            if (!this.onlyReceivingSimulatedMessages) {
                try {
                    if (this.incoming.ready()) {
                        this.receivedMessage.add(this.incoming.readLine());
                    }
                    Thread.sleep(1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /** Renvoie le dernier message reçu par le simulateur */
    String getLastReceivedMessage(){
        if (this.receivedMessage.peek()!=null){
            return this.receivedMessage.poll();
        }
        else{
            return null;
        }
    }

    /** Envoi un message au robot connecté au simulateur */
    void sendMessage(String message){
        try {
            this.outgoing.write(message);
            this.outgoing.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** Simule la réception d'un message */
    void SIMULATE_receiveMessage(String message){
        this.receivedMessage.add(message);
    }
}
