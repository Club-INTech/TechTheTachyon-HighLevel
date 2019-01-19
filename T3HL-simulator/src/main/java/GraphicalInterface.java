import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

class GraphicalInterface extends JFrame {

    private SimulatedRobot simulatedRobot;

    private BufferedImage backgroundImage;

    private JPanel panel;
    private long lastTimeUpdate;
    private final int WIDTH_FRAME = 1200;      //in pixels
    private final int HEIGHT_FRAME = 800;      //in pixels
    private final int WIDTH_TABLE = 3000;      //in millimeters
    private final int HEIGHT_TABLE = 2000;     //in millimeters
    private final int MILLIS_BETWEEN_UPDATES=10;
    private final Color DEFAULT_COLOR = new Color(0,0,0,255);
    private final Color ROBOT_COLOR = new Color(0,255,0,128);
    private final Color ORIENTATION_COLOR = new Color(0,0,255,192);
    private final Color OBSTACLE_COLOR = new Color(255,0,0,128);


    /** Constructeur */
    GraphicalInterface(SimulatedRobot simulatedRobot) {
        this.simulatedRobot=simulatedRobot;

        try {
            this.backgroundImage = ImageIO.read(new File("resources/Table2019.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }


        this.lastTimeUpdate=System.currentTimeMillis();

        this.setTitle("Simulateur");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.panel = new JPanel(){
            @Override
            protected void paintComponent(Graphics graphics) {
                updateGraphics(graphics);
                Toolkit.getDefaultToolkit().sync();
            }
        };
        this.panel.setDoubleBuffered(true);
        this.panel.setVisible(true);
        this.panel.setPreferredSize(new Dimension(this.WIDTH_FRAME, this.HEIGHT_FRAME));

        this.getContentPane().add(this.panel);
        this.setVisible(true);
        this.pack();
    }

    /** Fonction appelée par le simulateur */
    void tryUpdate(){
        if (System.currentTimeMillis() - this.lastTimeUpdate > this.MILLIS_BETWEEN_UPDATES) {
            this.lastTimeUpdate=System.currentTimeMillis();
            this.panel.repaint();
        }
    }

    /** Affiche le robot*/
    private void drawRobot(Graphics g, int x, int y, float orientation, int radius){
        g.setColor(ROBOT_COLOR);
        g.fillOval(x-radius/2,y-radius/2,radius,radius);
        g.setColor(ORIENTATION_COLOR);
        g.drawLine(x, y, Math.round(x+(float)Math.cos(orientation)*radius/2), Math.round(y-(float)Math.sin(orientation)*radius/2));
        g.setColor(DEFAULT_COLOR);
    }

    private void drawBackground(Graphics g){
        g.drawImage(this.backgroundImage,0,0, this.WIDTH_FRAME, this.HEIGHT_FRAME, null);
    }

    /** Efface l'affichage */
    private void clearScreen(Graphics g){
        g.clearRect(0,0,this.WIDTH_FRAME, this.HEIGHT_FRAME);
    }

    /** Met à jour l'affichage */
    private void updateGraphics(Graphics g){
        clearScreen(g);
        drawBackground(g);
        drawRobot(g,
                Math.round((this.simulatedRobot.getX()+(this.WIDTH_TABLE/2.0f))*(this.WIDTH_FRAME/(float)this.WIDTH_TABLE)),
                Math.round((this.HEIGHT_TABLE-this.simulatedRobot.getY())*(this.HEIGHT_FRAME/(float)this.HEIGHT_TABLE)),
                this.simulatedRobot.getOrientation(),
                50);
    }
}