package data.controlers;

import com.panneau.Panneau;
import com.panneau.TooManyDigitsException;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.i2c.I2CFactory;
import pfg.config.Config;
import utils.ConfigData;
import utils.container.Service;
import utils.container.ServiceThread;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Service & Thread qui gère la """comm""" avec le panneau: score et sélection de couleur au début du match
 */
public class PanneauService implements Service {

    private Panneau panel;
    private long updatePeriod;
    private String couleur;

    public Panneau getPaneau(){
        return panel;
    }

    public void setPaneau(Panneau p){
        panel=p;
    }

    @Override
    public void updateConfig(Config config) {
        updatePeriod = config.getLong(ConfigData.SCORE_UPDATE_PERIOD);
        couleur = config.getString(ConfigData.COULEUR);
    }
}
