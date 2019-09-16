package data.controlers;

import data.GameState;
import data.PaletsZoneChaos;
import data.Table;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import pfg.config.Configurable;
import utils.Log;
import utils.container.Module;
import utils.math.Vec2;
import utils.math.InternalVectCartesian;

import java.util.concurrent.ConcurrentLinkedQueue;

import static java.lang.Math.toIntExact;

public class PaletsChaosControler extends Thread implements Module {

    private Table table;

    private ConcurrentLinkedQueue<String> paletsPosAndColorQueue;

    private Listener listener;

    @Configurable
    private boolean symetry;

    private static final String ARGUMENTS_SEPARATOR = " ";

    private static final int TIME_LOOP = 5;

    public PaletsChaosControler(Listener listener, Table table) {
        this.listener = listener;
        this.table = table;
        this.paletsPosAndColorQueue = new ConcurrentLinkedQueue<>();
        listener.addCollection(Channel.PALETS_POSITION, paletsPosAndColorQueue);
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
            while (this.paletsPosAndColorQueue.isEmpty()) {
                try {
                    Thread.sleep(TIME_LOOP);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            Log.DATA_HANDLER.debug("oh oh oh oh oh oh oh oh oh oh oh oh oh ");

            JSONParser parser = new JSONParser();
            String paletsPosAndColorJson = paletsPosAndColorQueue.poll();
            Log.DATA_HANDLER.debug(paletsPosAndColorJson);
            try {
                Object obj = parser.parse(paletsPosAndColorJson);

                JSONObject jsonObject = (JSONObject) obj;

                JSONArray purple_chaos_array = (JSONArray) jsonObject.get("purple_chaos");
                JSONArray yellow_chaos_array = (JSONArray) jsonObject.get("yellow_chaos");
                JSONArray purple_dispenser_array = (JSONArray) jsonObject.get("purple_dispenser");
                JSONArray yellow_dispenser_array = (JSONArray) jsonObject.get("yellow_dispenser");

                int x;
                int y;
                Vec2 position;
                String color;
                boolean first_red_purple = true;
                boolean first_red_yellow = true;
                if(symetry) {
                    for (int i = 0; i < purple_chaos_array.size(); i++) {
                        JSONObject jsonObj = (JSONObject) purple_chaos_array.get(i);
                        color = (String) jsonObj.get("color");
                        x = toIntExact((long) jsonObj.get("x"));
                        y = toIntExact((long) jsonObj.get("y"));
                        position = new InternalVectCartesian(x, y);
                        switch (color) {
                            case "red":
                                if (first_red_purple) {
                                    table.getPaletRedUnZoneChaosPurple().setPosition(position);
                                    PaletsZoneChaos.RED_1_ZONE_CHAOS_PURPLE.setPosition(position);
                                    first_red_purple = false;
                                } else {
                                    table.getPaletRedDeuxZoneChaosPurple().setPosition(position);
                                    PaletsZoneChaos.RED_2_ZONE_CHAOS_PURPLE.setPosition(position);
                                }
                                break;
                            case "blue":
                                PaletsZoneChaos.BLUE_ZONE_CHAOS_PURPLE.setPosition(position);
                                table.getPaletBlueZoneChaosPurple().setPosition(position);
                                break;
                            case "green":
                                PaletsZoneChaos.GREEN_ZONE_CHAOS_PURPLE.setPosition(position);
                                table.getPaletGreenZoneChaosPurple().setPosition(position);
                                break;
                        }
                    }
                }
                else {

                    for (int i = 0; i < yellow_chaos_array.size(); i++) {
                        System.out.println(yellow_chaos_array.get(i));
                        JSONObject jsonObj = (JSONObject) yellow_chaos_array.get(i);
                        x = toIntExact((long) jsonObj.get("x"));
                        y = toIntExact((long) jsonObj.get("y"));
                        color = (String) jsonObj.get("color");
                        position = new InternalVectCartesian(x, y);

                        switch (color) {
                            case "red":
                                if (first_red_yellow) {
                                    table.getPaletRedUnZoneChaosYellow().setPosition(position);
                                    PaletsZoneChaos.RED_1_ZONE_CHAOS_YELLOW.setPosition(position);
                                    first_red_yellow = false;
                                } else {
                                    table.getPaletRedDeuxZoneChaosYellow().setPosition(position);
                                    PaletsZoneChaos.RED_2_ZONE_CHAOS_YELLOW.setPosition(position);
                                }
                                break;
                            case "blue":
                                PaletsZoneChaos.BLUE_ZONE_CHAOS_YELLOW.setPosition(position);
                                table.getPaletBlueZoneChaosYellow().setPosition(position);
                                break;
                            case "green":
                                PaletsZoneChaos.GREEN_ZONE_CHAOS_YELLOW.setPosition(position);
                                table.getPaletGreenZoneChaosPurple().setPosition(position);
                                break;
                        }
                    }
                }
                GameState.POSITIONS_CHAOS_RECUES.setData(true);

                /*for (int i = 0; i < purple_dispenser_array.size(); i++) {
                    System.out.println(purple_dispenser_array.get(i));
                    JSONObject jsonObj = (JSONObject) purple_dispenser_array.get(i);
                    color = (String) jsonObj.get("color");

                }
                for (int i = 0; i < yellow_dispenser_array.size(); i++) {
                    System.out.println(yellow_dispenser_array.get(i));
                    JSONObject jsonObj = (JSONObject) yellow_dispenser_array.get(i);
                    color = (String) jsonObj.get("color");

                }*/

            } catch (ParseException e) {
                e.printStackTrace();
            }

        }
    }
}
