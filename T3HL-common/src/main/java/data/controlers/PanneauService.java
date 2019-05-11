package data.controlers;

import com.panneau.Panneau;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.i2c.I2CFactory;
import pfg.config.Config;
import utils.ConfigData;
import utils.Container;
import utils.Log;
import utils.container.Service;

import java.io.IOException;

/**
 * Service & Thread qui gère la """comm""" avec le panneau: score et sélection de couleur au début du match
 */
public class PanneauService implements Service {

    private Panneau panel;
    private long updatePeriod;
    private String couleur;

    public PanneauService(Container container) {
        try {
            panel = new Panneau(RaspiPin.GPIO_00, RaspiPin.GPIO_02, RaspiPin.GPIO_03, RaspiPin.GPIO_07);
            panel.addListener(teamColor -> {
                couleur=panel.getTeamColor().toString().toLowerCase();
                Log.STRATEGY.warning("Couleur: "+couleur);
                container.getConfig().override(ConfigData.COULEUR, couleur);
                container.updateConfig(container.getConfig());
            });
        } catch (IOException | I2CFactory.UnsupportedBusNumberException e){
            e.printStackTrace();
        }
    }

    public void setPanel(Panneau panel) {
        this.panel = panel;
    }

    public Panneau getPanneau(){
        return panel;
    }

    public String getCouleur(){
        return couleur;
    }

    @Override
    public void updateConfig(Config config) {
        updatePeriod = config.getLong(ConfigData.SCORE_UPDATE_PERIOD);
        couleur = config.getString(ConfigData.COULEUR);
    }
}
