package validation;

import data.PaletsZoneChaos;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Test;
import utils.math.Vec2;
import utils.math.VectCartesian;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import static java.lang.Math.toIntExact;

public class Test_Json_Parser {

    @Test
    public void testParse() {

        JSONParser parser = new JSONParser();
        try {
            File jsonFile = new File("src/test/java/validation/file1.txt");
            FileReader json= new FileReader(jsonFile);
            Object obj = parser.parse(json);
            JSONObject jsonObject =(JSONObject) obj;

            JSONArray purple_chaos_array = (JSONArray) jsonObject.get("purple_chaos");
            JSONArray yellow_chaos_array = (JSONArray) jsonObject.get("yellow_chaos");
            JSONArray purple_dispenser_array = (JSONArray) jsonObject.get("purple_dispenser");
            JSONArray yellow_dispenser_array = (JSONArray) jsonObject.get("yellow_dispenser");

            int x;
            int y;
            Vec2 position;
            String color;
            boolean first_red = true;
            for (int i = 0; i < purple_chaos_array.size(); i++) {
                JSONObject jsonObj = (JSONObject) purple_chaos_array.get(i);
                color = (String) jsonObj.get("color");
                x = toIntExact((long) jsonObj.get("x"));
                y = toIntExact((long) jsonObj.get("y"));
                position = new VectCartesian(x, y);
                switch (color) {
                    case "red":
                        if (first_red) {
                            PaletsZoneChaos.RED_1_ZONE_CHAOS_PURPLE.setPosition(position);
                            first_red = false;
                        } else {
                            PaletsZoneChaos.RED_2_ZONE_CHAOS_PURPLE.setPosition(position);
                        }
                        break;
                    case "blue":
                        PaletsZoneChaos.BLUE_ZONE_CHAOS_PURPLE.setPosition(position);
                        break;
                    case "green":
                        PaletsZoneChaos.GREEN_ZONE_CHAOS_PURPLE.setPosition(position);
                        break;
                }
            }

            System.out.println(PaletsZoneChaos.RED_1_ZONE_CHAOS_PURPLE.getPosition());
            System.out.println(PaletsZoneChaos.RED_2_ZONE_CHAOS_PURPLE.getPosition());
            System.out.println(PaletsZoneChaos.BLUE_ZONE_CHAOS_PURPLE.getPosition());
            System.out.println(PaletsZoneChaos.GREEN_ZONE_CHAOS_PURPLE.getPosition());


        }
        catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
