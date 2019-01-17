import javax.swing.*;
import java.awt.*;

class GraphicalInterface extends JFrame {

    private SimulatedRobot simulatedRobot;

    private JPanel panel;
    private long lastTimeUpdate;
    private final int WIDTH = 800;
    private final int HEIGHT = 600;
    private final int MILLIS_BETWEEN_UPDATES=10;
    private final Color DEFAULT_COLOR = new Color(0,0,0,255);
    private final Color ROBOT_COLOR = new Color(0,255,0,128);
    private final Color OBSTACLE_COLOR = new Color(255,0,0,128);


    /** Constructeur */
    GraphicalInterface(SimulatedRobot simulatedRobot) {
        this.lastTimeUpdate=System.currentTimeMillis();

        this.setTitle("Simulateur");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(this.WIDTH, this.HEIGHT);

        this.panel = new JPanel(){
            @Override
            protected void paintComponent(Graphics graphics) {
                updateGraphics(graphics);
                Toolkit.getDefaultToolkit().sync();
            }
        };
        this.panel.setDoubleBuffered(true);
        this.panel.setVisible(true);

        this.getContentPane().add(this.panel);
        this.setVisible(true);
    }

    /** Fonction appelée par le simulateur */
    void update(){
        if (System.currentTimeMillis() - this.lastTimeUpdate > this.MILLIS_BETWEEN_UPDATES) {
            this.lastTimeUpdate=System.currentTimeMillis();
            this.panel.repaint();
        }
    }

    /** Affiche le robot*/
    private void drawRobot(Graphics g, int x, int y, int radius){
        g.setColor(ROBOT_COLOR);
        g.fillOval(x-radius/2,y-radius/2,radius,radius);
        g.setColor(DEFAULT_COLOR);
    }

    /** Efface l'affichage */
    private void clearScreen(Graphics g){
        g.clearRect(0,0,this.WIDTH, this.HEIGHT);
    }

    /** Met à jour l'affichage */
    private void updateGraphics(Graphics g){
        clearScreen(g);
        drawRobot(g, this.simulatedRobot.getX(), this.simulatedRobot.getY(), 50);
    }
}