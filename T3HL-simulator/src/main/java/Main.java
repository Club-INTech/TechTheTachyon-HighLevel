public class Main {
    public static void main(String[] args) {
        SimulatorManagerLauncher launcher = new SimulatorManagerLauncher();
        launcher.setLLports(new int[]{10001,10002});
        launcher.setHLports(new int[]{20001,20002});
        launcher.setcolorblindMode(false);
        launcher.launchSimulator();
    }
}