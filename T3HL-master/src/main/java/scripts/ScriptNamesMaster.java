package scripts;

import utils.Container;
import utils.container.ContainerException;
import utils.container.Service;

public enum ScriptNamesMaster implements ScriptNames {
    ACCELERATEUR(Accelerateur.class),
    PALETS3(Paletsx3.class),
    PALETS6(PaletsX6.class),
    PALETS_ZONE_DEPART(PaletsZoneDepart.class),
    PALETS_ZONE_CHAOS(ScriptPaletsZoneChaos.class),
    GOLDENIUM(Goldenium.class);


    private Class<? extends Service> aClass;

    private Script script;

    static {
       // reInit();
    }

    ScriptNamesMaster(Class<? extends Service> aClass){
        this.aClass=aClass;
    }

    @Override
    public Script getScript() {
        if(script == null) {
            Container container = Container.getInstance("Master");
            try {
                script = (Script)container.getService(aClass);
            } catch (ContainerException e) {
                e.printStackTrace();
            }
        }
        return script;
    }

    /**
     * JUSTE POUR LE TEST
     */
    public static void cleanup() {
        for(ScriptNamesMaster name : values()) {
            name.script = null;
        }
    }

    /**
     * JUSTE POUR LE TEST
     */
    public static void reInit() {
        try {
            Container container = Container.getInstance("Master");
            for (ScriptNamesMaster scriptNames : ScriptNamesMaster.values()) {
                scriptNames.script = (Script)container.getService(scriptNames.aClass);
            }
        } catch (ContainerException e){
            e.printStackTrace();
        }
    }
}
