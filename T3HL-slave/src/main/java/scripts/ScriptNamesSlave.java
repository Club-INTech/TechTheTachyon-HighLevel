package scripts;

import utils.HLInstance;
import utils.container.ContainerException;
import utils.container.Module;

public enum ScriptNamesSlave implements ScriptNames {
    HOMOLOGATION(ScriptHomologationSlave.class,"homologation"),
    ;

    private Class<? extends Script> scriptClass;
    private String scriptName;

    ScriptNamesSlave(Class<? extends Script> scriptClass, String scriptName){
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
