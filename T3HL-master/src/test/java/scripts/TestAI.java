package scripts;

import utils.HLInstance;
import utils.container.ContainerException;
import utils.math.Vec2;
import utils.math.VectCartesian;

public class TestAI extends TestBaseHL {
    // TODO: Initialiser l'IA

    @Override
    public void initState(HLInstance hl) throws ContainerException {

    }

    @Override
    public Vec2 startPosition() {
        return new VectCartesian(0,200);
    }

    @Override
    public void action() {

    }
}
