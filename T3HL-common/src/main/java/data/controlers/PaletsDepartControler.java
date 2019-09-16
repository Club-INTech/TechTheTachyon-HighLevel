package data.controlers;

import data.PaletsZoneDepart;
import data.Table;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import pfg.config.Configurable;
import utils.Log;
import utils.container.Module;

import java.util.concurrent.ConcurrentLinkedQueue;

public class PaletsDepartControler extends Thread implements Module {

    private Table table;

    private ConcurrentLinkedQueue<String> paletsColorQueue;

    private Listener listener;

    @Configurable
    private boolean symetry;

    private static final String ARGUMENTS_SEPARATOR = " ";

    private static final int TIME_LOOP = 5;

    public PaletsDepartControler(Listener listener, Table table) {
        this.listener = listener;
        this.table = table;
        this.paletsColorQueue = new ConcurrentLinkedQueue<>();
        listener.addCollection(Channel.PALETS_POSITION, paletsColorQueue);
    }

    @Override
    public void run() {
        Log.DATA_HANDLER.debug("Controler lancé : en attente du listener...");
        while (!listener.hasFinishedLoading()) {
            try {
                Thread.sleep(Listener.TIME_LOOP);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        Log.DATA_HANDLER.debug("Controler Palets Chaos opérationnel");
        while (!Thread.currentThread().isInterrupted()) {
            while (this.paletsColorQueue.isEmpty()) {
                try {
                    Thread.sleep(TIME_LOOP);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            Log.DATA_HANDLER.debug("oh oh oh oh oh oh oh oh oh oh oh oh oh ");

            JSONParser parser = new JSONParser();
            String paletsColorJson = paletsColorQueue.poll();
            Log.DATA_HANDLER.debug(paletsColorJson);
            try {
                Object obj = parser.parse(paletsColorJson);

                JSONObject jsonObject = (JSONObject) obj;

                JSONArray color_depart_array = (JSONArray) jsonObject.get("zone_depart");
                String zone;
                String color;
                for (int i = 0; i < color_depart_array.size(); i++) {
                    JSONObject jsonObj = (JSONObject) color_depart_array.get(i);
                    zone = (String) jsonObj.get("zone");
                    color = (String) jsonObj.get("color");

                    switch (zone) {
                        case "R":
                            PaletsZoneDepart.PALET_R.setCouleur(color);
                            break;
                        case "G":
                            PaletsZoneDepart.PALET_G.setCouleur(color);
                            break;
                        case "B":
                            PaletsZoneDepart.PALET_B.setCouleur(color);
                            break;
                    }
                }




            } catch (ParseException e) {
                e.printStackTrace();
            }

        }
    }
}

