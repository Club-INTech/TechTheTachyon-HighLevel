package scripts;

import data.Table;
import data.XYO;
import data.synchronization.SynchronizationWithBuddy;
import locomotion.UnableToMoveException;
import pfg.config.Config;
import robot.Master;
import utils.math.Vec2;

public class Match extends Script {
    private final ScriptManagerMaster scriptManagerMaster;
    private SynchronizationWithBuddy syncBuddy;

    public Match(Master robot, Table table, ScriptManagerMaster scriptManagerMaster, SynchronizationWithBuddy syncBuddy) {
        super(robot,table);
        this.scriptManagerMaster = scriptManagerMaster;
        this.syncBuddy = syncBuddy;
    }


    @Override
    public void execute(Integer version) {
        // 0. Lancer l'électron
        scriptManagerMaster.getScript(ScriptNamesMaster.ELECTRON).goToThenExecute(0);

        // 1. Zone de départ, juste la case bleue
        scriptManagerMaster.getScript(ScriptNamesMaster.PALETS_ZONE_DEPART).goToThenExecute(0/*PaletsZoneDepart.JUST_BLUE*/);

        /*
        // 2. Zone de chaos (tout)
        scriptManagerMaster.getScript(ScriptNamesMaster.PALETS_ZONE_CHAOS).goToThenExecute(0);*/

        // 3. Palets x6
        scriptManagerMaster.getScript(ScriptNamesMaster.PALETS6).goToThenExecute(3);

        // (3,5. Prendre les palets restants de la zone de départ?)

        // 4. Aller vers l'accélérateur
        // (4,5. Désactiver la détection du secondaire ?)
        Script accelerateurScript = scriptManagerMaster.getScript(ScriptNamesMaster.ACCELERATEUR);

        try {
            robot.followPathTo(accelerateurScript.entryPosition(0), () -> {
                // 5. Prévenir le secondaire que le distributeur de palets x6 est libre
                syncBuddy.sendPaletX6Free();
            });

            // 6. Faire l'accélérateur
            scriptManagerMaster.getScript(ScriptNamesMaster.ACCELERATEUR).goToThenExecute(0);
        } catch (UnableToMoveException e) {
            e.printStackTrace();
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
