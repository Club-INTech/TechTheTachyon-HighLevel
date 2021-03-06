package utils;

import orders.OrderWrapper;
import pfg.config.Config;
import utils.container.ServiceThread;

public class MatchTimer extends ServiceThread {
    private static final long MATCH_LENGTH = 100 * 1000;
    private long startTime;
    private OrderWrapper orderWrapper;

    public MatchTimer(OrderWrapper orderWrapper) {
        this.orderWrapper = orderWrapper;
    }

    public void resetTimer() {
        startTime = System.currentTimeMillis();
        Log.setStartTime();
    }

    @Override
    public void run() {
        while (!isInterrupted()) {
            if(startTime > 0) { // check initialisé
                long elapsed = System.currentTimeMillis() - startTime;
                if(elapsed > MATCH_LENGTH) {
                    orderWrapper.immobilise();
                    orderWrapper.endMatch();
                    break;
                }
            }
        }
    }

    @Override
    public void updateConfig(Config config) {

    }

    public long getTimeElapsed() {
        if(startTime <= 0)
            return 0;
        return System.currentTimeMillis()-startTime;
    }
}
