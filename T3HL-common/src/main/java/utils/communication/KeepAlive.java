package utils.communication;

import connection.Connection;
import data.SensorState;
import orders.OrderWrapper;
import pfg.config.Config;
import utils.ConfigData;
import utils.Log;
import utils.container.Service;

public class KeepAlive extends Thread implements Service {

    private final OrderWrapper orderWrapper;
    private long pingInterval;
    private long pingTimeout;
    private Connection llConnection;

    public KeepAlive(OrderWrapper orderWrapper) {
        this.orderWrapper = orderWrapper;
    }

    @Override
    public void run() {
        SensorState.LAST_PONG.setData(System.currentTimeMillis());
        while(!isInterrupted()) {
            orderWrapper.ping();

            long time = System.currentTimeMillis();
            if (!llConnection.isInitiated()){
                SensorState.LAST_PONG.setData(time);
            }
            else {
                if (time - SensorState.LAST_PONG.getData() >= pingTimeout) {
                    Log.COMMUNICATION.critical("TIMEOUT! Attempting reconnection...");
                    try {
                        llConnection.reInit();
                        SensorState.LAST_PONG.setData(System.currentTimeMillis());
                    } catch (CommunicationException e) {
                        e.printStackTrace();
                    }
                    //throw new RuntimeException("Timeout HL<->LL");
                    // TODO: que faire quand il y a un timeout?
                }
            }
            try {
                Thread.sleep(pingInterval); // toutes les 1/2 secondes
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void updateConfig(Config config) {
        pingInterval = config.getLong(ConfigData.PING_INTERVAL);
        pingTimeout = config.getLong(ConfigData.PING_TIMEOUT);

        boolean isMaster = config.getBoolean(ConfigData.MASTER);
        if(config.getBoolean(ConfigData.SIMULATION)) {
            llConnection = Connection.MASTER_LL_SIMULATEUR;
        } else {
            if(isMaster) {
                llConnection = Connection.TEENSY_MASTER;
            } else {
                llConnection = Connection.TEENSY_SLAVE;
            }
        }
    }
}
