package scripts;

import data.CouleurPalet;
import data.Table;
import data.XYO;
import data.synchronization.SynchronizationWithBuddy;
import locomotion.UnableToMoveException;
import pfg.config.Config;
import robot.Slave;
import utils.math.Vec2;

public class MatchSlave extends Script {
    private final ScriptManagerSlave scriptManagerSlave;
    private SynchronizationWithBuddy syncBuddy;

    public MatchSlave(Slave robot, Table table, ScriptManagerSlave scriptManagerSlave, SynchronizationWithBuddy syncBuddy) {
        super(robot,table);
        this.scriptManagerSlave = scriptManagerSlave;
        this.syncBuddy = syncBuddy;
    }

    @Override
    public void execute(Integer version) {
        // 1. Rush Bleu Accélérateur
        // 2. Pousse le palet bleu
        scriptManagerSlave.getScript(ScriptNamesSlave.GETBLUEACC).goToThenExecute(0);

        if(true)
            return;

        // 3. Gold à prendre
        // 4. Rush vers balance
        // (5 Attente du principal?) <- à tester si on passe
        // 6 Pose le Gold dans la balance
        scriptManagerSlave.getScript(ScriptNamesSlave.GOLDENIUM).goToThenExecute(0);

        // 7. Attente que palets x6 est libre
        syncBuddy.waitForFreePaletX6();

        // 8. Palets x6 restants
        // (8,5. On retourne à la balance direct) ?
        scriptManagerSlave.getScript(ScriptNamesSlave.PALETSX6).goToThenExecute(0);
        scriptManagerSlave.getScript(ScriptNamesSlave.CRACHEUR).goToThenExecute(0);

        // 9. Palets x3
        // 10. Balance
        scriptManagerSlave.getScript(ScriptNamesSlave.PALETSX3).goToThenExecute(0);
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
