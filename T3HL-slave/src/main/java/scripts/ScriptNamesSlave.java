package scripts;

import utils.Container;
import utils.container.ContainerException;
import utils.container.Service;

public enum ScriptNamesSlave implements ScriptNames {
    GOLDENIUM(Goldenium.class,"goldenium"),
    PALETS3(Paletsx3.class, "paletsx3"),
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
