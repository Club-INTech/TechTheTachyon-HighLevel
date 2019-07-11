package unitaires;

import org.junit.Assert;
import org.junit.Test;
import utils.Container;
import utils.container.ContainerException;
import utils.container.Module;

public class TestTypeHierarchyModule {

    private static abstract class ModuleBase implements Module {}
    private static class MyModule extends ModuleBase {}

    @Test
    public void findModuleWithBaseClass() throws ContainerException {
        Container container = Container.getInstance("Master");
        MyModule myModule = container.module(MyModule.class);
        Assert.assertEquals(myModule, container.module(ModuleBase.class));
    }
}
