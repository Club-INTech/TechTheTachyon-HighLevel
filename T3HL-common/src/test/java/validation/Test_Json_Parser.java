package validation;

import connection.Connection;
import connection.ConnectionManager;
import data.Graphe;
import data.PaletsZoneChaos;
import data.PaletsZoneDepart;
import data.Table;
import data.controlers.Listener;
import data.controlers.PaletsChaosControler;
import data.controlers.PaletsDepartControler;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Before;
import org.junit.Test;
import utils.Container;
import utils.math.Vec2;
import utils.math.VectCartesian;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import static java.lang.Math.toIntExact;

public class Test_Json_Parser {

    private ConnectionManager connectionManager;
    private Container container;
    private Listener listener;
    private PaletsDepartControler paletsDepartControler;
    private Table table;
    private Graphe graphe;

    /*@Before
    public void setUp() throws Exception {
        container = Container.getInstance("Master");
        connectionManager = container.getService(ConnectionManager.class);
        connectionManager.initConnections(Connection.BALISE_IA);
        listener = container.getService(Listener.class);
        table = container.getService(Table.class);
        graphe = container.getService(Graphe.class);
        graphe.reInit();
        table.initObstacles();
        table.setGraphe(graphe);
        listener.start();
        paletsDepartControler = container.getService(PaletsDepartControler.class);
    }*/

    @Test
    public void testParse() {


        JSONParser parser = new JSONParser();
        try {
            File jsonFile = new File("src/test/java/validation/file1.txt");
            FileReader json= new FileReader(jsonFile);
            Object obj = parser.parse(json);

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

            System.out.println(PaletsZoneDepart.PALET_R.getCouleur());
            System.out.println(PaletsZoneDepart.PALET_G.getCouleur());
            System.out.println(PaletsZoneDepart.PALET_B.getCouleur());


        }
        catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
    @Test
    public void testAvecZoneChaosControle(){
        paletsDepartControler.run();
        System.out.println(PaletsZoneDepart.PALET_R.getCouleur());
        System.out.println(PaletsZoneDepart.PALET_G.getCouleur());
        System.out.println(PaletsZoneDepart.PALET_B.getCouleur());

    }


}
