package utils.communication;

import connection.Connection;
import connection.ConnectionManager;
import data.CouleurPalet;
import pfg.config.Config;
import utils.ConfigData;
import utils.RobotSide;
import utils.container.Service;

import java.util.Stack;

public class SimulatorDebug implements Service {

    private final ConnectionManager manager;
    private boolean active;
    private int senderPort;

    public SimulatorDebug(ConnectionManager manager) {
        this.manager = manager;
    }

    public void sendElevatorContents(RobotSide side, Stack<CouleurPalet> elevator) {
        if(active) {
            StringBuilder builder = new StringBuilder(getSenderPort()+" elevatorContents "+side.toString());
            for(CouleurPalet colour : elevator) {
                builder.append(" ");
                builder.append(colour.toString());
            }
            try {
                Connection.DEBUG_SIMULATEUR.send(builder.toString());
            } catch (CommunicationException e) {
                e.printStackTrace();
            }
        }
    }

    public int getSenderPort() {
        return senderPort;
    }

    public void setSenderPort(int senderPort) {
        this.senderPort = senderPort;
    }

    @Override
    public void updateConfig(Config config) {
        active = config.getBoolean(ConfigData.SIMULATION);
    }
}
