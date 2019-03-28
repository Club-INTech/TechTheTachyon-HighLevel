import connection.Connection;
import data.Palet;
import data.XYO;
import orders.order.Order;
import pfg.config.Config;
import scripts.Script;
import utils.ConfigData;
import utils.Container;
import utils.communication.CommunicationException;
import utils.container.ContainerException;
import utils.container.Service;

import java.util.Optional;

public class CommunicationWithBuddy extends Thread implements Service {

    private boolean symetry;

    private boolean simulation;

    private Connection ConnectionWithBuddy;

    private Container container;

    public CommunicationWithBuddy(Container container){
        this.container=container;
    }

    @Override
    public void run() {
        while (!isInterrupted()) {
            try {
                Optional<String> message = ConnectionWithBuddy.read();
                if(message.isPresent()) {
                    String msg =message.get();
                }


            } catch (CommunicationException e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    public void sendString(String message) {
        try {
            ConnectionWithBuddy.send(message);
        } catch (CommunicationException e) {
            e.printStackTrace();
            try {
                ConnectionWithBuddy.reInit();
                while (!ConnectionWithBuddy.isInitiated());
                ConnectionWithBuddy.send(message);
            } catch (CommunicationException ef) {
                ef.printStackTrace();
            }
        }
    }

    public void sendPosition(){
        this.sendString("Pos "+ XYO.getRobotInstance());
    }

    public void sendOrder(Script script){
        this.sendString("Order "+ script.getClass().getCanonicalName());
    }

    public void doScript(String scriptName,int version){
        try {
            Class scriptClass = Class.forName(scriptName);
            Script script = (Script)container.getService(scriptClass);
            script.goToThenExecute(version);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (ContainerException e) {
            e.printStackTrace();
        }
    }

    public void paletPris(Palet palet){
        this.sendString("Palet "+palet.getId()+"pris");
    }

    @Override
    public void updateConfig(Config config) {
        // On est du côté violet par défaut , le HL pense en violet
        symetry = config.getString(ConfigData.COULEUR).equals("jaune");
        this.simulation = config.getBoolean(ConfigData.SIMULATION);
        if (this.simulation) {
            this.ConnectionWithBuddy = Connection.SLAVE_SIMULATEUR;
        } else {
            this.ConnectionWithBuddy = Connection.SLAVE;
        }
    }

    public void setConnection(Connection connection) {
        this.ConnectionWithBuddy = connection;
    }
}
