package scripts;

import utils.Container;
import utils.container.Service;

public enum ScriptNamesMaster implements ScriptNames {
    ACCELERATEUR(Accelerateur.class),
    PALETS3(Paletsx3.class),
    PALETS6(PaletsX6.class),
    PALETS_ZONE_DEPART(PaletsZoneDepart.class),
    ACTIVATION_PANNEAU_DOMOTIQUE(ActivationPanneauDomotique.class);


    private Class<? extends Service> aClass;

    private Script script;

    static {
        Container container = Container.getInstance("Master");
        for(ScriptNamesMaster scriptNames: ScriptNamesMaster.values()){
            scriptNames.script = container.getService(scriptNames.aClass);
        }
    }

    ScriptNamesMaster(Class<? extends Service> aClass){
        this.aClass=aClass;
    }
}
