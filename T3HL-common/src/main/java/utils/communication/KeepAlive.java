package utils.communication;

import data.SensorState;
import orders.OrderWrapper;
import pfg.config.Config;
import utils.ConfigData;
import utils.container.Service;

public class KeepAlive extends Thread implements Service {

    private final OrderWrapper orderWrapper;
    private long lastPingTime = 0L;
    private long pingInterval;
    private long pingTimeout;
    private long firstPingTime;

    public KeepAlive(OrderWrapper orderWrapper) {
        this.orderWrapper = orderWrapper;
    }

    @Override
    public void run() {
        firstPingTime = System.currentTimeMillis();
        while(!isInterrupted()) {
            orderWrapper.ping();

            long time = System.currentTimeMillis();
            if(time-firstPingTime >= pingTimeout) { // ne pas faire d'erreur dès le premier test (le LL n'a pas encore eu le temps de répondre)
                if(time-SensorState.LAST_PONG.getData() >= pingTimeout) {
                    throw new RuntimeException("Timeout HL<->LL");
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
    }
}
