package scripts;

import data.Table;
import data.XYO;
import data.synchronization.SynchronizationWithBuddy;
import pfg.config.Config;
import robot.Slave;
import utils.ConfigData;
import utils.Container;
import utils.math.Vec2;

import java.util.concurrent.TimeUnit;

public class MatchSlave extends Script {
    private final ScriptManagerSlave scriptManagerSlave;
    private SynchronizationWithBuddy syncBuddy;
    private Container container;

    public MatchSlave(Slave robot, Table table, ScriptManagerSlave scriptManagerSlave, SynchronizationWithBuddy syncBuddy, Container container) {
        super(robot,table);
        this.scriptManagerSlave = scriptManagerSlave;
        this.syncBuddy = syncBuddy;
        this.container=container;
    }

    @Override
    public void execute(Integer version) {
       /* try {
            robot.turn(0);
        } catch (UnableToMoveException e) {
            e.printStackTrace();
        }
       robot.recalageMeca();
        */
        // 1. Rush Bleu Accélérateur
        // 2. Pousse le palet bleu

        // on attend que le principal parte
        if (container.getConfig().getBoolean(ConfigData.HOMOLOGATION)) {
            scriptManagerSlave.getScript(ScriptNamesSlave.HOMOLOGATION).timedExecute(0);
        } else {
            try {
                TimeUnit.SECONDS.sleep(4);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            scriptManagerSlave.getScript(ScriptNamesSlave.GETREDDEP).timedExecute(0);

            scriptManagerSlave.getScript(ScriptNamesSlave.GETBLUEACC).goToThenExecute(0);

            scriptManagerSlave.getScript(ScriptNamesSlave.GOLDENIUM).execute(1);
            scriptManagerSlave.getScript(ScriptNamesSlave.PALETSX3).goToThenExecute(0);
            table.addTassot();
            scriptManagerSlave.getScript(ScriptNamesSlave.CRACHEUR).execute(0);

            if (true)
                return;
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
