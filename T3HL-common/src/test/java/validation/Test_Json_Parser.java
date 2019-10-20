package validation;

import connection.ConnectionManager;
import data.Graphe;
import data.PaletsZoneDepart;
import data.Table;
import data.controlers.Listener;
import data.controlers.PaletsDepartControler;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Test;
import utils.HLInstance;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Test_Json_Parser {

    private ConnectionManager connectionManager;
    private HLInstance hl;
    private Listener listener;
    private PaletsDepartControler paletsDepartControler;
    private Table table;
    private Graphe graphe;

    /*@Before
    public void setUp() throws Exception {
        container = Container.getInstance("Master");
        connectionManager = container.module(ConnectionManager.class);
        connectionManager.initConnections(Connection.BALISE_IA);
        listener = container.module(Listener.class);
        table = container.module(Table.class);
        graphe = container.module(Graphe.class);
        graphe.reInit();
        table.initObstacles();
        table.setGraphe(graphe);
        listener.start();
        paletsDepartControler = container.module(PaletsDepartControler.class);
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


}
