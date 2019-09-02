package scripts;

import data.XYO;
import data.synchronization.SynchronizationWithBuddy;
import pfg.config.Config;
import utils.ConfigData;
import utils.HLInstance;
import utils.math.Vec2;

import java.util.concurrent.TimeUnit;

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
        // 1. Rush Bleu Accélérateur
        // 2. Pousse le palet bleu

        // on attend que le principal parte

        if (hl.getConfig().getBoolean(ConfigData.HOMOLOGATION)) {
            scriptManagerSlave.getScript(ScriptNamesSlave.HOMOLOGATION).timedExecute(0);
        }
        else {
            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            scriptManagerSlave.getScript(ScriptNamesSlave.GETREDDEP).timedExecute(0);

            scriptManagerSlave.getScript(ScriptNamesSlave.GETBLUEACC).goToThenExecute(0);

            scriptManagerSlave.getScript(ScriptNamesSlave.GOLDENIUM).timedExecute(0);

            if (true)
                return;

            scriptManagerSlave.getScript(ScriptNamesSlave.PALETSX3).goToThenExecute(0);
            table.addTassot();
            scriptManagerSlave.getScript(ScriptNamesSlave.CRACHEUR).timedExecute(0);

            // 3. Gold à prendre
            // 4. Rush vers balance
            // (5 Attente du principal?) <- à tester si on passe
            // 6 Pose le Gold dans la balance
            scriptManagerSlave.getScript(ScriptNamesSlave.GOLDENIUM).execute(0);

            // 7. Attente que palets x6 soit libre
            syncBuddy.waitForFreeBalance();

            // 8. Palets x6 restants
            // (8,5. On retourne à la balance direct) ?
            scriptManagerSlave.getScript(ScriptNamesSlave.PALETSX6).goToThenExecute(0);
            scriptManagerSlave.getScript(ScriptNamesSlave.CRACHEUR).goToThenExecute(0);

            // 9. Palets x3
            // 10. Balance
            scriptManagerSlave.getScript(ScriptNamesSlave.PALETSX3).goToThenExecute(0);
            scriptManagerSlave.getScript(ScriptNamesSlave.CRACHEUR).goToThenExecute(0);

        }
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
