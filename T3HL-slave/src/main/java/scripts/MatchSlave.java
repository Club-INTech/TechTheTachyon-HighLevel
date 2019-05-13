package scripts;

import data.CouleurPalet;
import data.Table;
import data.XYO;
import pfg.config.Config;
import robot.Slave;
import utils.math.Circle;
import utils.math.Shape;
import utils.math.Vec2;

public class MatchSlave extends Script {
    private final ScriptManagerSlave scriptManagerSlave;

    public MatchSlave(Slave robot, Table table, ScriptManagerSlave scriptManagerSlave) {
        super(robot,table);
        this.scriptManagerSlave = scriptManagerSlave;
    }

    @Override
    public void execute(Integer version) {
        for (int i = 0; i<5; i++){
            robot.pushPaletDroit(CouleurPalet.VERT);
        }
        scriptManagerSlave.getScript(ScriptNamesSlave.CRACHEUR).goToThenExecute(0);
    }

    @Override
    public Vec2 entryPosition(Integer version) {
        return XYO.getRobotInstance().getPosition();
    }

    @Override
    public void finalize(Exception e) {

    }

    @Override
    public void updateConfig(Config config) {
        super.updateConfig(config);
    }
}
