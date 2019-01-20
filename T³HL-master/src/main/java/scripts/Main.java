package scripts;

import data.Graphe;
import data.Table;
import robot.Master;
import utils.Container;
import utils.container.ContainerException;


public class Main {
    public static void main(String[] args){
        Container container = Container.getInstance("Master");
        ScriptManagerMaster scriptManager;
        try {
            Master robot = container.getService(Master.class);
            Table table = container.getService(Table.class);
            scriptManager = container.getService(ScriptManagerMaster.class);

        } catch (ContainerException e) {
            e.printStackTrace();
        }
        //Paletsx3 paletx3 = scriptManager.getScript(Paletsx3);
    }

}
