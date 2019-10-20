package unitaires;

import org.junit.Assert;
import org.junit.Test;
import robot.Robots;
import utils.HLInstance;
import utils.container.ContainerException;
import utils.container.Module;

public class TestTypeHierarchyModule {

    private static abstract class ModuleBase implements Module {}
    private static class MyModule extends ModuleBase {}

    @Test
    public void findModuleWithBaseClass() throws ContainerException {
        HLInstance hl = HLInstance.get(Robots.MAIN);
        MyModule myModule = hl.module(MyModule.class);
        Assert.assertEquals(myModule, hl.module(ModuleBase.class));
    }
}
