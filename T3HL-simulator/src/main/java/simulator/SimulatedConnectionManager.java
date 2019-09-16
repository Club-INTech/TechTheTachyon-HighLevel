package simulator;

import utils.Log;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentLinkedQueue;

public class SimulatedConnectionManager extends Thread {

    /**
     * Port pour signifier qu'on veut voir ce que le HL fait (sur le robot)
     */
    public static final int VISUALISATION_PORT = -1;
    private ServerSocket receptionSocket;
    private Socket socket;
    private ConcurrentLinkedQueue<String> receivedMessage;
    private BufferedReader incoming;
    private BufferedWriter outgoing;

    private int port;
    private boolean ready = false;

    /** Constructeur
     * @param port port sur lequel écoute le simulateur
     */
    SimulatedConnectionManager(int port){
        super("SimulatedConnectionManager (port "+port+")");
        this.port=port;
        //On initialise le dernier message reçu et le serveur Socket
        this.receivedMessage = new ConcurrentLinkedQueue<>();
        if(port != VISUALISATION_PORT) {
            try {
                System.out.println("Ouverture d'une socket sur le port "+port);
                this.receptionSocket = new ServerSocket(port);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                this.socket=this.receptionSocket.accept();
                System.out.println("Socket connectée sur le port "+port);
                this.incoming = new BufferedReader(new InputStreamReader(this.socket.getInputStream(), StandardCharsets.UTF_8.name()));
                this.outgoing = new BufferedWriter(new OutputStreamWriter(this.socket.getOutputStream(), StandardCharsets.UTF_8.name()));
            } catch (IOException e) {
                e.printStackTrace();
            }
            this.start();
        } else {
            ready = true;
        }
    }

    @Override
    /** Ecoute les messages et les ajoute à la queue des messages reçus*/
    public void run() {
        this.ready = true;
        //On boucle infiniement pour
        while (true){
            try {
                if (this.incoming.ready()) {
                    this.receivedMessage.add(this.incoming.readLine());
                }
                Thread.sleep(1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /** Renvoie False tant que tout n'est pas instancié */
    boolean isReady(){
        return this.ready;
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
        if(port != VISUALISATION_PORT) {
            try {
                Log.LL_DEBUG.debug("[SimulatedLL] Sending: "+message);
                this.outgoing.write(message);
                this.outgoing.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /** Simule la réception d'un message */
    void SIMULATE_receiveMessage(String message){
        this.receivedMessage.add(message);
    }

    /** Renvoie le port sur lequel la connection écoute */
    int getPort(){
        return this.port;
    }
}
