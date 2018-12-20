public class LaunchSimulator extends Thread{

    private ConnectionManagerSimulator simulatorThread;
    private GraphicalInterface frame;

    /** Constructeur
     * @param port port sur lequel on devra se connecter pour parler au simulateur
     */
    public LaunchSimulator(int port){
        //this.simulatorThread=new ConnectionManagerSimulator(port);
        this.frame = new GraphicalInterface();
        while (!this.frame.isReady()){
            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        this.run();
    }

    @Override
    public void run() {
        while (true) {
            this.frame.update();
        }
    }
}
