package simulator;

import utils.ConfigData;

public class Main {
    public static void main(String[] args) {
        SimulatorManagerLauncher launcher = new SimulatorManagerLauncher();
        launcher.setLLports(new int[]{(int)ConfigData.MASTER_LL_SIMULATEUR.getDefaultValue()});
        //launcher.setLLports(new int[]{10001,10002});
        //launcher.setHLports(new int[]{20001,20002});
        launcher.setColorblindMode(true);
        launcher.launch();
        //launcher.setPointsToDraw(new VectCartesian(0,1000));
    }
}