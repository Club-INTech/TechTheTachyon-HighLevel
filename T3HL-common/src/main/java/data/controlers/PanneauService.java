package data.controlers;

import com.panneau.Panneau;
import com.pi4j.io.gpio.RaspiPin;
import pfg.config.Config;
import utils.ConfigData;
import utils.container.Service;

import java.io.IOException;

/**
 * Service & Thread qui gère la """comm""" avec le panneau: score et sélection de couleur au début du match
 */
public class PanneauService implements Service {

    private Panneau panel;
    private long updatePeriod;
    private String couleur;

    public PanneauService(){
        try {
            panel = new Panneau(RaspiPin.GPIO_01, RaspiPin.GPIO_02, RaspiPin.GPIO_03, RaspiPin.GPIO_07);
            panel.addListener(teamColor -> {
                couleur=panel.getTeamColor().toString();
                System.out.println(panel.getTeamColor());
            });
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public Panneau getPaneau(){
        return panel;
    }

    public void setPaneau(Panneau p){
        panel=p;
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