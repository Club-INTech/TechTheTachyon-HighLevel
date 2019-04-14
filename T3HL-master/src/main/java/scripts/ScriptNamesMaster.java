package scripts;

import utils.Container;
import utils.container.ContainerException;
import utils.container.Service;

public enum ScriptNamesMaster implements ScriptNames {
    ACCELERATEUR(Accelerateur.class, "accelerateur"),
    PALETS3(Paletsx3.class, "paletsx3"),
    PALETS6(PaletsX6.class, "paletsx6"),
    PALETS_ZONE_DEPART(PaletsZoneDepart.class, "palets_zone_depart"),
    PALETS_ZONE_CHAOS(ScriptPaletsZoneChaos.class, "palet_zone_chaos"),
    GOLDENIUM(Goldenium.class,"goldenium");


    private Class<? extends Service> aClass;

    private Script script;
    private String scriptName;

    static {
       // reInit();
    }

    ScriptNamesMaster(Class<? extends Service> aClass, String scriptName){
        this.aClass=aClass;
        this.scriptName = scriptName;
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
     * Renvoie le nom du script
     * @return String du nom du script
     */
    public String getName(){
        return this.scriptName;
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
