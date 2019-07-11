package utils.communication;

import connection.Connection;
import data.SensorState;
import orders.OrderWrapper;
import pfg.config.Config;
import pfg.config.Configurable;
import utils.ConfigData;
import utils.container.Module;

public class KeepAlive extends Thread implements Module {

    private final OrderWrapper orderWrapper;
    @Configurable
    private long pingInterval;
    @Configurable
    private long pingTimeout;
    private Connection llConnection;

    public KeepAlive(OrderWrapper orderWrapper) {
        this.orderWrapper = orderWrapper;
    }

    @Override
    public void run() {
        SensorState.LAST_PONG.setData(System.currentTimeMillis());
        while (!llConnection.isInitiated()){
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        /*while(!isInterrupted()) {
            orderWrapper.ping();

            long time = System.currentTimeMillis();
            if (!llConnection.isInitiated()){
                SensorState.LAST_PONG.setData(time);
            }
            else {
                if (time - SensorState.LAST_PONG.getData() >= pingTimeout) {
                    Log.COMMUNICATION.critical("TIMEOUT! Attempting reconnection...");
                    SensorState.LAST_PONG.setData(System.currentTimeMillis());
                    // TODO: que faire quand il y a un timeout?
                }
            }
            try {
                Thread.sleep(pingInterval); // toutes les 1/2 secondes
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }*/
    }

    @Override
    public void updateConfig(Config config) {
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
