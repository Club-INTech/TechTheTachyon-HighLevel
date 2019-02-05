import connection.Connection;
import pfg.config.Config;
import utils.ConfigData;
import utils.communication.CommunicationException;
import utils.container.Service;

public class CommunicationWithBuddy implements Service {

    private boolean symetry;

    private boolean simulation;

    private Connection ConnectionWithBuddy;

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
