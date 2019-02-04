package scripts;

import utils.Container;
import utils.container.ContainerException;
import utils.container.Service;

public enum ScriptNamesMaster implements ScriptNames {
    ACCELERATEUR(Accelerateur.class),
    PALETS3(Paletsx3.class),
    PALETS6(PaletsX6.class),
    PALETS_ZONE_DEPART(PaletsZoneDepart.class),
    PALETS_ZONE_CHAOS(PaletsZoneChaos.class),
    GOLDENIUM(Goldenium.class);


    private Class<? extends Service> aClass;

    private Script script;

    static {
        try {
            Container container = Container.getInstance("Master");
            for (ScriptNamesMaster scriptNames : ScriptNamesMaster.values()) {
                scriptNames.script = (Script)container.getService(scriptNames.aClass);
            }
        } catch (ContainerException e){
            e.printStackTrace();
        }
    }

    ScriptNamesMaster(Class<? extends Service> aClass){
        this.aClass=aClass;
    }

    public Script getScript() {
        return script;
    }
}
