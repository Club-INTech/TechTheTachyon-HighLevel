import data.Table;
import data.table.Obstacle;
import utils.math.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.Color;
import java.awt.Toolkit;
import java.awt.Graphics;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

class GraphicalInterface extends JFrame {

    private HashMap<Integer, SimulatedRobot> simulatedRobots;
    private Table table;
    private ArrayList<Obstacle> fixedObstacles;

    private BufferedImage backgroundImage;

    private boolean isDrawingPoints;

    private ArrayList<Vec2> pointsToDraw;

    private JPanel panel;
    private long lastTimeUpdate;
    private final int WIDTH_FRAME = 1200;      //in pixels
    private final int HEIGHT_FRAME = 800;      //in pixels
    private final int WIDTH_TABLE = 3000;      //in millimeters
    private final int HEIGHT_TABLE = 2000;     //in millimeters
    private final int MILLIS_BETWEEN_UPDATES=10;
    private Color DEFAULT_COLOR = new Color(0,0,0,255);
    private Color ROBOT_COLOR = new Color(0,255,0,128);
    private Color ORIENTATION_COLOR = new Color(0,0,255,255);
    private Color OBSTACLE_COLOR = new Color(255,0,0,64);
    private Color POINTS_TO_DRAW_COLOR = new Color(255,0,255,255);

    private boolean isLaunched = false;

    /** Set les robots qui sont instancié pour qu'ils soient affichés dans le simulateur
     * @param simulatedRobots HashMap<Integer, SimulatedRobot> des robots instanciés
     */
    void setSimulatedRobots(HashMap<Integer, SimulatedRobot> simulatedRobots){
        if (!this.isLaunched){
            this.simulatedRobots=simulatedRobots;
        }
    }

    /** Set la table
     * @param table table mdr
     */
    void setTable(Table table){
        if (!this.isLaunched){
            this.table=table;
        }
    }

    /** Set le mode daltonien
     * @param value mode daltonien activé sur TRUE
     */
    void setColorblindMode(boolean value){
        if (!this.isLaunched){
            this.setColorSchema(value);
        }
    }

    /** Set si les points sont affichés ou non
     * @param value les points sont affichés si TRUE
     */
    void setIsDrawingPoints(boolean value){
        if (!this.isLaunched){
            this.isDrawingPoints=value;
        }
    }

    /** Lance l'interface graphique */
    void launch(){
        this.isLaunched=true;
    }

    /** Méthode instanciant tous les attributs nécessaires au bon fonctionnement de l'interface graphique
     *  Les attributs définits à NULL sont des attributs qu'il faut SET obligatoirement
     */
    void defaultInit(){
        this.lastTimeUpdate=System.currentTimeMillis();
        this.simulatedRobots=new HashMap<Integer, SimulatedRobot>();
        this.isDrawingPoints=true;
        this.pointsToDraw=new ArrayList<Vec2>();

        this.table=null;
    }


    /** Constructeur */
    GraphicalInterface() {
        this.defaultInit();
        try {
            this.backgroundImage = ImageIO.read(new File("resources/Table2019.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    /** Définit si on est en mode daltonien */
    private void setColorSchema(boolean colorblindMode){
        if (colorblindMode) {
            DEFAULT_COLOR = new Color(0, 0, 0, 255);
            ROBOT_COLOR = new Color(0, 0, 255, 128);
            ORIENTATION_COLOR = new Color(0, 255, 255, 255);
            OBSTACLE_COLOR = new Color(255, 255, 0, 64);
            POINTS_TO_DRAW_COLOR = new Color(255,255,255, 255);
        }
        else{
            DEFAULT_COLOR = new Color(0, 0, 0, 255);
            ROBOT_COLOR = new Color(0, 255, 0, 128);
            ORIENTATION_COLOR = new Color(0, 0, 255, 255);
            OBSTACLE_COLOR = new Color(255, 0, 0, 64);
            POINTS_TO_DRAW_COLOR = new Color(255,255,255,255);
        }
    }

    /** Set la liste des points à afficher */
    void setListOfPointsToDraw(ArrayList<Vec2> pointsToDraw){
        this.pointsToDraw = pointsToDraw;
    }

    /** Fonction appelée par le simulateur */
    void tryUpdate(){
        if (this.isLaunched) {
            if (System.currentTimeMillis() - this.lastTimeUpdate > this.MILLIS_BETWEEN_UPDATES) {
                this.lastTimeUpdate = System.currentTimeMillis();
                this.fixedObstacles = table.getFixedObstacles();
                this.panel.repaint();
            }
        }
    }

    /** Affiche un robot */
    private void drawRobot(Graphics g, int x, int y, double orientation, int diameter){
        g.setColor(ROBOT_COLOR);
        g.fillOval(x-diameter/2,y-diameter/2, diameter,diameter);
        g.setColor(ORIENTATION_COLOR);
        g.drawLine(x, y, Math.round(x+(float)Math.cos(orientation)*diameter), Math.round(y-(float)Math.sin(orientation)*diameter));
        g.setColor(DEFAULT_COLOR);
    }

    /** Affiche le background */
    private void drawBackground(Graphics g){
        g.drawImage(this.backgroundImage,0,0, this.WIDTH_FRAME, this.HEIGHT_FRAME, null);
    }

    /** Affiche les obstacles */
    private void drawObstacles(Graphics g){
        synchronized (this.fixedObstacles) {
            for (Obstacle obstacle : this.fixedObstacles) {
                g.setColor(OBSTACLE_COLOR);
                Shape shape = obstacle.getShape();
                if (shape instanceof CircularRectangle) {
                    for (Rectangle rectangle : ((CircularRectangle) shape).getSideRectangles()) {
                        drawPrimitiveShape(g, rectangle);
                    }
                    for (Circle circle : ((CircularRectangle) shape).getCircleArcs()) {
                        drawPrimitiveShape(g, circle);
                    }
                    drawPrimitiveShape(g, ((CircularRectangle) shape).getMainRectangle());
                } else {
                    drawPrimitiveShape(g, shape);
                }
                g.setColor(DEFAULT_COLOR);
            }
        }
    }

    /** Fonction utlisée pour dessiner un carré ou un rectangle */
    private void drawPrimitiveShape(Graphics g, Shape shape){
        Vec2 centerOnTable = shape.getCenter();
        Vec2 center = transformTableCoordsToInterfaceCoords(centerOnTable);
        if (shape instanceof Circle){
            float diameter = transformTableDistanceToInterfaceDistance(((Circle) shape).getRadius()*2);
            g.fillOval(center.getX()-Math.round(diameter/2), center.getY()-Math.round(diameter/2), Math.round(diameter), Math.round(diameter));
        }
        else if (shape instanceof Rectangle){
            float width = transformTableDistanceToInterfaceDistance(((Rectangle) shape).getWidth());
            float length = transformTableDistanceToInterfaceDistance(((Rectangle) shape).getLength());
            g.fillRect(Math.round(center.getX()-length/2), Math.round(center.getY()-width/2), Math.round(length), Math.round(width));
        }
        else{
            System.out.println("Shape type not found");
        }
    }

    /**
     * Fonction pour dessiner une liste de points
     */
    public void drawPoints(Graphics g){
        g.setColor(POINTS_TO_DRAW_COLOR);
        for(Vec2 vecteur : this.pointsToDraw) {
            vecteur = transformTableCoordsToInterfaceCoords(vecteur);
            g.fillOval(vecteur.getX(), vecteur.getY(), 10, 10);
        }
        g.setColor(DEFAULT_COLOR);
    }

    /** Efface l'affichage */
    private void clearScreen(Graphics g){
        g.clearRect(0,0,this.WIDTH_FRAME, this.HEIGHT_FRAME);
    }

    /** Met à jour l'affichage */
    private void updateGraphics(Graphics g){
        clearScreen(g);
        drawBackground(g);
        drawObstacles(g);
        for (SimulatedRobot simulatedRobot : simulatedRobots.values()) {
            Vec2 coordsOnInterface = transformTableCoordsToInterfaceCoords(simulatedRobot.getX(), simulatedRobot.getY());
            int diameterOnInterface = transformTableDistanceToInterfaceDistance(250);
            drawRobot(g, coordsOnInterface.getX(), coordsOnInterface.getY(), simulatedRobot.getOrientation(), diameterOnInterface);
        }
        if(this.isDrawingPoints) {
            drawPoints(g);
        }
    }


    /** Transforme une distance de la table pour qu'elle soit affichée correction sur l'interface */
    private int transformTableDistanceToInterfaceDistance(int distance){
        return Math.round(distance * (this.WIDTH_FRAME / (float)this.WIDTH_TABLE));
    }

    /** Transforme une distance de la table pour qu'elle soit affichée correction sur l'interface */
    private float transformTableDistanceToInterfaceDistance(float distance){
        return distance * (this.WIDTH_FRAME / (float)this.WIDTH_TABLE);
    }

    /** Transforme les coordonnées de la table pour qu'ils soient affichés correction sur l'interface */
    private Vec2 transformTableCoordsToInterfaceCoords(int x, int y) {
        return new VectCartesian(
                (x + (this.WIDTH_TABLE / 2.0f)) * (this.WIDTH_FRAME / (float) this.WIDTH_TABLE),
                (this.HEIGHT_TABLE - y) * (this.HEIGHT_FRAME / (float) this.HEIGHT_TABLE)
        );
    }

    /** Transforme les coordonnées de la table pour qu'ils soient affichés correctement sur l'interface */
    private Vec2 transformTableCoordsToInterfaceCoords(Vec2 position){
        return transformTableCoordsToInterfaceCoords(position.getX(), position.getY());
    }



}