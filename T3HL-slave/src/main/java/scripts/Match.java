package scripts;

import data.Table;
import data.XYO;
import pfg.config.Config;
import robot.Slave;
import utils.math.Circle;
import utils.math.Shape;

public class Match extends Script {
    private final ScriptManagerSlave scriptManagerSlave;

    public Match(Slave robot, Table table, ScriptManagerSlave scriptManagerSlave) {
        super(robot,table);

        this.scriptManagerSlave = scriptManagerSlave;
    }


    @Override
    public void execute(Integer version) {
        scriptManagerSlave.getScript(ScriptNamesSlave.CRACHEUR).goToThenExecute(0);
    }

    @Override
    public Shape entryPosition(Integer version) {
        return new Circle(XYO.getRobotInstance().getPosition(), 100);
    }

    @Override
    public void finalize(Exception e) {

    }

    @Override
    public void updateConfig(Config config) {
        super.updateConfig(config);
    }
}
