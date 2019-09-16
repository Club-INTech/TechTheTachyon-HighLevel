package scripts;

import data.synchronization.SynchronizationWithBuddy;
import locomotion.UnableToMoveException;
import pfg.config.Configurable;
import utils.ConfigData;
import utils.HLInstance;
import utils.container.ContainerException;
import utils.math.Vec2;

public class Match extends Script {
    public static final int ACC_VERSION = 1;
    private final ScriptManagerMaster scriptManagerMaster;
    private SynchronizationWithBuddy syncBuddy;
    @Configurable
    private boolean secours;

    public Match(HLInstance hl, ScriptManagerMaster scriptManagerMaster, SynchronizationWithBuddy syncBuddy) {
        super(hl);
        this.scriptManagerMaster = scriptManagerMaster;
        this.syncBuddy = syncBuddy;
    }


    @Override
    public void execute(int version) {
        if(hl.getConfig().getBoolean(ConfigData.HOMOLOGATION)) {
            scriptManagerMaster.getScript(ScriptNamesMaster.ELECTRON).timedExecute(0);
            scriptManagerMaster.getScript(ScriptNamesMaster.HOMOLOGATION).timedExecute(0);
        } else if(hl.getConfig().getBoolean(ConfigData.OPEN_THE_GATE)) {
            scriptManagerMaster.getScript(ScriptNamesMaster.OPEN_THE_GATE).timedExecute(0);
        } else if(hl.getConfig().getBoolean(ConfigData.ZONE_CHAOS_TEST)) {
            System.out.println("ok ok ");
            scriptManagerMaster.getScript(ScriptNamesMaster.PALETS_ZONE_CHAOS).timedExecute(0);
        } else {
            // 0. Lancer l'électron
            scriptManagerMaster.getScript(ScriptNamesMaster.ELECTRON).timedExecute(0);

            // 1. Zone de départ, juste la case bleue
            scriptManagerMaster.getScript(ScriptNamesMaster.PALETS_ZONE_DEPART).timedExecute(0/*PaletsZoneDepart.JUST_BLUE*/);


            // 3. Palets x6
            scriptManagerMaster.getScript(ScriptNamesMaster.PALETSX6).goToThenExecute(4);

            // (3,5. Prendre les palets restants de la zone de départ?)

            // 4. Aller vers l'accélérateur
            // (4,5. Désactiver la détection du secondaire ?)
            Script accelerateurScript = scriptManagerMaster.getScript(ScriptNamesMaster.ACCELERATEUR);

            // 5. Prévenir le secondaire que le distributeur de palets x6 est libre => TODO: c'est la balance en fait qui coince
            async("Execution des actions pendant le déplacement", () -> accelerateurScript.executeWhileMovingToEntry(ACC_VERSION));


            //On tente d'aller à l'accélérateur
            boolean exceptionRaised = false;
            if(!secours) {
                exceptionRaised = attemptMultipleTimes(3, () -> {
                    try {
                        followPathTo(accelerateurScript.entryPosition(ACC_VERSION), 0);
                        return false;
                    } catch (UnableToMoveException e) {
                        e.printStackTrace();
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                        return true;
                    }
                });
            }
            if (secours || exceptionRaised) {
                //Si on n'a jamais réussi à aller à l'accélérateur, on fait le script de sécurité
                try {
                    hl.module(VideDansZoneDepartSiProbleme.class).goToThenExecute(0);
                } catch (ContainerException e) {
                    e.printStackTrace();
                }
            } else {
                //Si on a réussi à aller à l'accélérateur, on exécute le script
                try {
                    hl.module(Accelerateur.class).timedExecute(ACC_VERSION);
                } catch (ContainerException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public Vec2 entryPosition(int version) {
        return scriptManagerMaster.getScript(ScriptNamesMaster.PALETS_ZONE_DEPART).entryPosition(0);
    }

    @Override
    public void finalize(Exception e) {

    }
}
