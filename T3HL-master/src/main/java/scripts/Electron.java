package scripts;

import connection.Connection;
import data.SensorState;
import data.Table;
import data.XYO;
import pfg.config.Configurable;
import robot.Master;
import utils.Log;
import utils.communication.CommunicationException;
import utils.math.Vec2;

import java.util.concurrent.TimeUnit;

public class Electron extends Script{

    @Configurable
    private boolean usingElectron;

    public Electron(Master robot, Table table) {
        super(robot, table);
    }

    @Override
    public void execute(Integer version) {
        if(!usingElectron) {
            Log.STRATEGY.critical("L'électron est désactivé! On skippe le script de lancement de l'électron!");
            return;
        }
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

    @Override
    public Vec2 entryPosition(Integer version) {
        return XYO.getRobotInstance().getPosition();
    }

    @Override
    public void finalize(Exception e) { }
}
