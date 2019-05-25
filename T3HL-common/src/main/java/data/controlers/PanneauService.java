package data.controlers;

import com.panneau.LEDs;
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
    private Container container;
    private int programPort;
    private int ledCount;
    private boolean have7seg;

    public PanneauService(Container container) {
        this.container = container;
    }

    public void setPanel(Panneau panel) {
        this.panel = panel;
    }

    public Panneau getPanneau() {
        if(panel == null) {
            try {
                panel = new Panneau(ledCount, programPort, RaspiPin.GPIO_07, have7seg);
                if(have7seg) {
                    Log.STRATEGY.debug("Appel au constructeur du panneau avec " + ledCount + " leds et l'ecran de score");
                }else{
                    Log.STRATEGY.debug("Appel au constructeur du panneau avec " + ledCount + " leds, sans l'ecran de score");
                }
                panel.addListener(teamColor -> {
                    couleur=panel.getTeamColor().toString().toLowerCase();
                    container.getConfig().override(ConfigData.COULEUR, couleur);
                    container.updateConfig(container.getConfig());
                });
                panel.getLeds().fillColor(new LEDs.RGBColor(0,0,0));
            } catch (IOException | I2CFactory.UnsupportedBusNumberException e){
                e.printStackTrace();
            }
        }
        return panel;
    }

    public String getCouleur(){
        return couleur;
    }

    @Override
    public void updateConfig(Config config) {
        updatePeriod = config.getLong(ConfigData.SCORE_UPDATE_PERIOD);
        couleur = config.getString(ConfigData.COULEUR);
        ledCount = config.getInt(ConfigData.LED_COUNT);
        programPort = config.getInt(ConfigData.LED_PROGRAM_PORT);
        have7seg = config.getBoolean(ConfigData.USING_7_SEGMENTS);
    }
}
