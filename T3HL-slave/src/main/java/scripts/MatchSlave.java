package scripts;

import data.XYO;
import data.synchronization.SynchronizationWithBuddy;
import pfg.config.Config;
import utils.HLInstance;
import utils.math.Vec2;

public class MatchSlave extends Script {
    private final ScriptManagerSlave scriptManagerSlave;
    private SynchronizationWithBuddy syncBuddy;

    public MatchSlave(HLInstance hl, ScriptManagerSlave scriptManagerSlave, SynchronizationWithBuddy syncBuddy) {
        super(hl);
        this.scriptManagerSlave = scriptManagerSlave;
        this.syncBuddy = syncBuddy;
    }

    @Override
    public void execute(int version) {
        // Code lançant les différents scripts du secondaire
        scriptManagerSlave.getScript(ScriptNamesSlave.HOMOLOGATION).timedExecute(0);
        // TODO: A vous de jouer les 1As!
    }

    @Override
    public Vec2 entryPosition(int version) {
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
