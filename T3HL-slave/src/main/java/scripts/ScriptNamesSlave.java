package scripts;

import utils.Container;
import utils.container.ContainerException;
import utils.container.Service;

public enum ScriptNamesSlave implements ScriptNames {
    GOLDENIUM(Goldenium.class,"goldenium"),
    PALETSX3(PaletsX3Slave.class, "paletsX3"),
    PALETSX6(PaletsX6Slave.class, "paletsX6"),
    CRACHEUR(Cracheur.class, "cracheur"),
    GETBLUEACC(GetBlueAcc.class, "getBlueAcc"),
    GETREDDEP(GetRedDep.class, "getRedDep"),
    ;

    private Class<? extends Service> scriptClass;
    private String scriptName;

    ScriptNamesSlave(Class<? extends Service> scriptClass, String scriptName){
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
