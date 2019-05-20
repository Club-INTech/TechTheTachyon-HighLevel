package scripts;

import utils.Container;
import utils.container.ContainerException;
import utils.container.Service;

public enum ScriptNamesMaster implements ScriptNames {
    ACCELERATEUR(Accelerateur.class, "accelerateur"),
    PALETS6(PaletsX6.class, "paletsx6"),
    PALETS6ALTER(X6alter.class, "paletsx6alter"),
    PALETS_ZONE_DEPART(PaletsZoneDepart.class, "palets_zone_depart"),
    PALETS_ZONE_CHAOS(ScriptPaletsZoneChaos.class, "palet_zone_chaos"),
    HOMOLOGATION(ScriptHomologation.class, "homologation"),
    PRECOUPE_ACC(precoupeAccelerateurx6.class, "precoupeAccelerateurx6"),
    ELECTRON(Electron.class, "electron"),
    ;

    private Class<? extends Service> scriptClass;
    private String scriptName;

    ScriptNamesMaster(Class<? extends Service> scriptClass, String scriptName){
        this.scriptClass = scriptClass;
        this.scriptName = scriptName;
    }

    @Override
    public Script createScript(Container container) throws ContainerException {
        return (Script)container.getService(scriptClass);
    }

    /**
     * Renvoie le nom du script
     * @return String du nom du script
     */
    public String getName(){
        return this.scriptName;
    }

}
