package data.controlers;

import com.panneau.LEDs;
import com.panneau.Panneau;
import com.panneau.TooManyDigitsException;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.i2c.I2CFactory;
import pfg.config.Configurable;
import utils.ConfigData;
import utils.HLInstance;
import utils.Log;
import utils.container.Module;

import java.io.IOException;

/**
 * Module & Thread qui gère la """comm""" avec le panneau: score et sélection de couleur au début du match
 */
public class PanneauModule implements Module {

    private Panneau panel;
    @Configurable
    private long scoreUpdatePeriod;
    @Configurable
    private String couleur;
    private HLInstance hl;
    @Configurable
    private int ledProgramPort;
    @Configurable
    private int ledCount;
    @Configurable
    private boolean using7Segments;

    public PanneauModule(HLInstance hl) {
        this.hl = hl;
    }

    public void setPanel(Panneau panel) {
        this.panel = panel;
    }

    public Panneau getPanneau() {
        if(panel == null) {
            try {
                panel = new Panneau(ledCount, ledProgramPort, RaspiPin.GPIO_07, using7Segments);
                if(using7Segments) {
                    Log.STRATEGY.debug("Appel au constructeur du panneau avec " + ledCount + " leds et l'ecran de score");
                }else{
                    Log.STRATEGY.debug("Appel au constructeur du panneau avec " + ledCount + " leds, sans l'ecran de score");
                }
                panel.addListener(teamColor -> {
                    couleur=panel.getTeamColor().toString().toLowerCase();
                    hl.getConfig().override(ConfigData.COULEUR, couleur);
                    hl.updateConfig(hl.getConfig());
                });
                panel.getLeds().fillColor(LEDs.RGBColor.NOIR);
            } catch (IOException | I2CFactory.UnsupportedBusNumberException e){
                e.printStackTrace();
            }
        }
        return panel;
    }

    public String getCouleur(){
        return couleur;
    }

    public void printScore(int score) throws IOException, TooManyDigitsException {
        if(using7Segments){
            panel.printScore(score);
        }
    }

    @Override
    protected void finalize() throws Throwable {
        panel.getLeds().fillColor(LEDs.RGBColor.NOIR);
        panel.printScore(8888);
        super.finalize();
    }
}
