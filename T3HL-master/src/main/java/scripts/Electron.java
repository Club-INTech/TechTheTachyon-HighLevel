package scripts;

import connection.Connection;
import data.SensorState;
import data.XYO;
import data.controlers.Channel;
import data.controlers.Listener;
import pfg.config.Configurable;
import utils.HLInstance;
import utils.Log;
import utils.communication.CommunicationException;
import utils.math.Vec2;

import java.util.concurrent.TimeUnit;

public class Electron extends Script{

    @Configurable
    private boolean usingElectron;

    public Electron(HLInstance hl) {
        super(hl);
    }

    @Override
    public void execute(int version) {
        if(!usingElectron) {
            Log.STRATEGY.critical("L'électron est désactivé! On skippe le script de lancement de l'électron!");
            return;
        }
        Listener listener = hl.module(Listener.class);
        listener.registerMessageHandler(Channel.EVENTS, this::handleElectronEvents);
        Thread electronThread = new Thread(() -> {


            Log.ELECTRON.debug("Thread sending activating order started");
            while ((!SensorState.ELECTRON_ACTIVATED.getData()) && (!SensorState.ELECTRON_ARRIVED.getData()) ) {
                try {
                    Connection.ELECTRON.send("electron_launch");
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                } catch (CommunicationException e) {
                    e.printStackTrace();
                }
            }
            Log.ELECTRON.debug("Electron activated");

            robot.increaseScore(15);

            while (!SensorState.ELECTRON_ARRIVED.getData()){
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return;
                }
            }
            Log.ELECTRON.debug("Electron arrived");

            robot.increaseScore(20);
        });
        electronThread.setName("Electron");
        electronThread.setDaemon(true);
        electronThread.start();
    }

    private void handleElectronEvents(String message) {
        String[] event = message.split(" ");
        switch (event[0]) {
            case "electron_activated":
                // TODO: SensorState encore utile?
                SensorState.ELECTRON_ACTIVATED.setData(true);
                break;

            case "electron_arrived":
                if( ! SensorState.ELECTRON_ARRIVED.getData()) {
                    Log.STRATEGY.debug("Electron arrivé!");
                }
                SensorState.ELECTRON_ARRIVED.setData(true);
                break;
        }
    }

    @Override
    public Vec2 entryPosition(int version) {
        return XYO.getRobotInstance().getPosition();
    }

    @Override
    public void finalize(Exception e) { }
}
