package scripts;

import data.Table;
import data.synchronization.SynchronizationWithBuddy;
import locomotion.UnableToMoveException;
import pfg.config.Config;
import robot.Master;
import utils.Container;
import utils.Log;
import utils.TimeoutError;
import utils.container.ContainerException;
import utils.container.Service;
import utils.math.Vec2;

public class Match extends Script {
    private final ScriptManagerMaster scriptManagerMaster;
    private SynchronizationWithBuddy syncBuddy;
    private final Container container;

    public Match(Master robot, Table table, ScriptManagerMaster scriptManagerMaster, SynchronizationWithBuddy syncBuddy, Container container) {
        super(robot,table);
        this.scriptManagerMaster = scriptManagerMaster;
        this.syncBuddy = syncBuddy;
        this.container = container;
    }


    @Override
    public void execute(Integer version) {
        // 0. Lancer l'électron
        scriptManagerMaster.getScript(ScriptNamesMaster.ELECTRON).timedExecute(0);

        // 1. Zone de départ, juste la case bleue
        scriptManagerMaster.getScript(ScriptNamesMaster.PALETS_ZONE_DEPART).timedExecute(0/*PaletsZoneDepart.JUST_BLUE*/);

        /*
        // 2. Zone de chaos (tout)
        scriptManagerMaster.getScript(ScriptNamesMaster.PALETS_ZONE_CHAOS).goToThenExecute(0);*/

        // 3. Palets x6
        scriptManagerMaster.getScript(ScriptNamesMaster.PALETS6ALTER).goToThenExecute(4);

        // (3,5. Prendre les palets restants de la zone de départ?)

        // 4. Aller vers l'accélérateur
        // (4,5. Désactiver la détection du secondaire ?)
        Script accelerateurScript = scriptManagerMaster.getScript(ScriptNamesMaster.ACCELERATEUR);

        // 5. Prévenir le secondaire que le distributeur de palets x6 est libre => TODO: c'est la balance en fait qui coince
        syncBuddy.sendPaletX6Free();

        int accVersion = 1;
        async("Execution des actions pendant le déplacement", () -> accelerateurScript.executeWhileMovingToEntry(accVersion));

        try {
            Service.withTimeout(5000, () -> {
                try {
                    robot.followPathTo(accelerateurScript.entryPosition(accVersion));
                } catch (UnableToMoveException e) {
                    e.printStackTrace();
                }
            });
            // 6. Faire l'accélérateur
            scriptManagerMaster.getScript(ScriptNamesMaster.ACCELERATEUR).timedExecute(accVersion);
        } catch (TimeoutError timeout) {
            Log.STRATEGY.critical("Impossible d'atteindre l'accélérateur après 5s d'attente, on va vider les ascenseurs dans la zone de départ!");
            try {
                container.getService(VideDansZoneDepartSiProbleme.class).goToThenExecute(0);
            } catch (ContainerException e1) {
                e1.printStackTrace();
            }
        }
    }

    @Override
    public Vec2 entryPosition(Integer version) {
        return scriptManagerMaster.getScript(ScriptNamesMaster.PALETS_ZONE_DEPART).entryPosition(0);
    }

    @Override
    public void finalize(Exception e) {

    }

    @Override
    public void updateConfig(Config config) {
        super.updateConfig(config);
    }
}
