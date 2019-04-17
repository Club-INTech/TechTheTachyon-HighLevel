package data.controlers;

import com.panneau.Panneau;
import com.panneau.TooManyDigitsException;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.i2c.I2CFactory;
import pfg.config.Config;
import utils.ConfigData;
import utils.container.ServiceThread;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Service & Thread qui gère la """comm""" avec le panneau: score et sélection de couleur au début du match
 */
public class PanneauService extends ServiceThread {

    private final boolean onRaspi;
    private Panneau panel;
    private long updatePeriod;
    private int score;
    private String couleur;

    public PanneauService() {
        onRaspi = System.getProperty("user.name").equals("pi");
        if(onRaspi){
            panel=lazyGet();
        }
    }

    public void updateScore(int newScore) {
        this.score = newScore;
        try {
            panel.printScore(score);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public boolean isPurple() {
        if(lazyGet() == null) {
            return couleur.equals("violet");
        }
        return panel.isViolet();
    }

    public boolean isYellow() {
        if(lazyGet() == null) {
            return couleur.equals("jaune");
        }
        return panel.isYellow();
    }

    /**
     * Permet de n'initialiser l'objet Panneau que si on est sur la Raspi ET qu'il n'existe pas déjà
     * @return
     */
    private Panneau lazyGet() {
        if(onRaspi && panel == null) {
            try {
                panel = new Panneau(RaspiPin.GPIO_01, RaspiPin.GPIO_02, RaspiPin.GPIO_03, RaspiPin.GPIO_07);// Le switch est forcément sur le pin 7,
            } catch (I2CFactory.UnsupportedBusNumberException | IOException e) {
                e.printStackTrace();
            }
        }
        return panel;
    }

    @Override
    public void run() {
        if( ! onRaspi) {
            return;
        }
        /*
        while(!isInterrupted()) {
            try {
                lazyGet().printScore(score);
            } catch (TooManyDigitsException | IOException e) {
                e.printStackTrace();
            }

            try {
                TimeUnit.MILLISECONDS.sleep(updatePeriod);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
         */
    }

    @Override
    public void updateConfig(Config config) {
        updatePeriod = config.getLong(ConfigData.SCORE_UPDATE_PERIOD);
        couleur = config.getString(ConfigData.COULEUR);
    }
}
