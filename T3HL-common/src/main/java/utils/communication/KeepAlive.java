package utils.communication;

import data.SensorState;
import orders.OrderWrapper;
import pfg.config.Config;
import utils.ConfigData;
import utils.container.Service;

public class KeepAlive extends Thread implements Service {

    private final OrderWrapper orderWrapper;
    private long pingInterval;
    private long pingTimeout;

    public KeepAlive(OrderWrapper orderWrapper) {
        this.orderWrapper = orderWrapper;
    }

    @Override
    public void run() {
        SensorState.LAST_PONG.setData(System.currentTimeMillis());
        while(!isInterrupted()) {
            orderWrapper.ping();

            long time = System.currentTimeMillis();
            if(time-SensorState.LAST_PONG.getData() >= pingTimeout) {
                throw new RuntimeException("Timeout HL<->LL");
                // TODO: que faire quand il y a un timeout?
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
    }
}
