package scripts;

import data.CouleurPalet;
import data.Table;
import pfg.config.Config;
import robot.Master;
import scripts.*;
import utils.math.Circle;
import utils.math.Shape;
import utils.math.VectCartesian;

public class Match extends Script {
    private final ScriptManagerMaster scriptManagerMaster;

    public Match(Master robot, Table table, ScriptManagerMaster scriptManagerMaster) {
        super(robot,table);

        this.scriptManagerMaster = scriptManagerMaster;
    }


    @Override
    public void execute(Integer version) {
        //scriptManagerMaster.getScript(ScriptNamesMaster.PALETS_ZONE_CHAOS).goToThenExecute(0);
        scriptManagerMaster.getScript(ScriptNamesMaster.ELECTRON).goToThenExecute(0);
        scriptManagerMaster.getScript(ScriptNamesMaster.PALETS_ZONE_DEPART).goToThenExecute(0);
        scriptManagerMaster.getScript(ScriptNamesMaster.PALETS6).goToThenExecute(3);


         for (int i=0; i<5; i++){
            robot.pushPaletDroit(CouleurPalet.ROUGE);
            robot.pushPaletGauche(CouleurPalet.ROUGE);
        }
        scriptManagerMaster.getScript(ScriptNamesMaster.ACCELERATEUR).goToThenExecute(0);
    }

    @Override
    public Shape entryPosition(Integer version) {
        return scriptManagerMaster.getScript(ScriptNamesMaster.PALETS_ZONE_DEPART).entryPosition(version);
    }

    @Override
    public void finalize(Exception e) {

    }

    @Override
    public void updateConfig(Config config) {
        super.updateConfig(config);
    }
}
