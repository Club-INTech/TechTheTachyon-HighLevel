package scripts;

import utils.HLInstance;
import utils.container.ContainerException;
import utils.container.Module;

public enum ScriptNamesMaster implements ScriptNames {
    ACCELERATEUR(Accelerateur.class, "accelerateur"),
    PALETSX6(PaletsX6.class, "paletsx6alter"),
    PALETS_ZONE_DEPART(PaletsZoneDepart.class, "palets_zone_depart"),
    PALETS_ZONE_CHAOS(ScriptPaletsZoneChaos.class, "palet_zone_chaos"),
    HOMOLOGATION(ScriptHomologation.class, "homologation"),
    ELECTRON(Electron.class, "electron"),
    OPEN_THE_GATE(OpenTheGate.class, "openthegate"),
    ;

    private Class<? extends Script> scriptClass;
    private String scriptName;

    ScriptNamesMaster(Class<? extends Script> scriptClass, String scriptName){
        this.scriptClass = scriptClass;
        this.scriptName = scriptName;
    }

    @Override
    public Script createScript(HLInstance hl) throws ContainerException {
        return hl.module(scriptClass);
    }

    /**
     * Renvoie le nom du script
     * @return String du nom du script
     */
    public String getName(){
        return this.scriptName;
    }

}
