package validation;

import connection.Connection;
import connection.ConnectionManager;
import data.GameState;
import data.Graphe;
import data.PaletsZoneChaos;
import data.Table;
import data.controlers.Listener;
import data.controlers.PaletsChaosControler;
import locomotion.UnableToMoveException;
import orders.hooks.HookFactory;
import orders.order.ActuatorsOrder;
import org.junit.Before;
import org.junit.Test;
import robot.Robot;
import scripts.ScriptManager;
import utils.Container;
import utils.Log;
import utils.math.Vec2;
import utils.math.VectCartesian;

public class JUnit_ScriptPaletsZoneChaos {
    private ScriptManager scriptManager;
    private GameState state;
    private HookFactory hookFactory;
    private ConnectionManager connectionManager;
    private Container container;
    private Listener listener;
    private PaletsChaosControler paletsChaosControler;
    private Table table;
    private Graphe graphe;
    private Robot robot;

    @Before
    public void setUp() {
        try { container = Container.getInstance("Master");
            connectionManager = container.getService(ConnectionManager.class);
            connectionManager.initConnections(Connection.BALISE);
            listener = container.getService(Listener.class);
            table = container.getService(Table.class);
            graphe = container.getService(Graphe.class);
            graphe.reInit();
            table.initObstacles();
            table.setGraphe(graphe);
            listener.start();
            paletsChaosControler = container.getService(PaletsChaosControler.class);
            robot = container.getService(Robot.class);
            scriptManager=container.getService(ScriptManager.class);
            container.startInstanciedThreads();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @Test
    public void testScript() {
        int xEntry = -1020;
        int yEntry = 1050;
        Vec2[] positions = new VectCartesian[3];

        positions[0]=new VectCartesian(xEntry,yEntry);
        positions[1]= PaletsZoneChaos.RED_1_ZONE_CHAOS_YELLOW.getPosition();
        positions[2]=PaletsZoneChaos.RED_2_ZONE_CHAOS_YELLOW.getPosition();
        positions[3]=PaletsZoneChaos.GREEN_ZONE_CHAOS_YELLOW.getPosition();

        for(int i=1; i<positions.length;i++){
            positions[i].setX(positions[i].getX()-220);
        }



        boolean premierPaletPris = false;
        try{
            robot.turn(Math.PI/2);
            robot.useActuator(ActuatorsOrder.ACTIVE_LA_POMPE_DROITE);
            robot.useActuator(ActuatorsOrder.DESACTIVE_ELECTROVANNE_DROITE);
            for (Vec2 position : positions) {
                if(premierPaletPris == false){
                    robot.followPathTo(position);
                } else{ premierPaletPris=true;}
                robot.useActuator(ActuatorsOrder.DESCEND_ASCENSEUR_DROIT_DE_UN_PALET);
                robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_SOL);
                robot.useActuator(ActuatorsOrder.DESACTIVE_ELECTROVANNE_DROITE);
                robot.useActuator(ActuatorsOrder.ENVOIE_LE_BRAS_DROIT_A_LA_POSITION_ASCENSEUR);
                robot.useActuator(ActuatorsOrder.ACTIVE_ELECTROVANNE_DROITE);

                premierPaletPris = true;
            }
            robot.useActuator(ActuatorsOrder.DESACTIVE_LA_POMPE_DROITE);
        }catch (UnableToMoveException e) {
            e.printStackTrace();
        }

        Log.TABLE.debug("execution zoneChaos");

    }

}
