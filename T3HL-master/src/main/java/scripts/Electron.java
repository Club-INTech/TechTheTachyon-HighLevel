package scripts;
/*
import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListener;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import com.pi4j.util.CommandArgumentParser;*/
import connection.Connection;
import data.SensorState;
import data.Table;
import pfg.config.Config;
import robot.Master;
import sun.management.Sensor;
import utils.Log;
import utils.communication.CommunicationException;
import utils.math.Circle;
import utils.math.Shape;
import utils.math.VectCartesian;

import java.util.concurrent.TimeUnit;

public class Electron extends Script{

    public Electron(Master robot, Table table) {
        super(robot, table);
    }

    @Override
    public void execute(Integer version) {

        Thread electronThread = new Thread(() -> {
            Log.ELECTRON.debug("Thread sending activating order started");
            while (!SensorState.ELECTRON_ACTIVATED.getData()) {
                try {
                    Connection.ELECTRON.send("Launch");
                } catch (CommunicationException e) {
                    e.printStackTrace();
                }
            }
            Log.ELECTRON.debug("Electron activated");


            while (!SensorState.ELECTRON_ARRIVED.getData()){
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            Log.ELECTRON.debug("Electron arrived");
        });
        electronThread.start();




/* FIXME: A refaire
        final GpioController gpio = GpioFactory.getInstance();
        final GpioPinDigitalOutput pin = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_01, "ESP32_depart", PinState.LOW);

        pin.high();
        robot.increaseScore(20);

        PinPullResistance pull = PinPullResistance.PULL_UP;
        final GpioPinDigitalInput testArrivee = gpio.provisionDigitalInputPin(RaspiPin.GPIO_02, pull);
        testArrivee.addListener(new GpioPinListenerDigital() {
            @Override
            public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {

                if (event.getState().isHigh()){

                    //TODO ajouter au calcul des points
                    robot.increaseScore(20);

                    Log.STRATEGY.debug("Electron arrivee");
                }
            }
        });*/


    }

    @Override
    public Shape entryPosition(Integer version) { return new Circle(new VectCartesian(0, 0), 100000); }

    @Override
    public void finalize(Exception e) { }

    @Override
    public void updateConfig(Config config) { }
}
