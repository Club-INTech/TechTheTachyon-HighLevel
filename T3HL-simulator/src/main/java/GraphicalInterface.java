import data.Table;
import data.graphe.Node;
import data.table.MobileCircularObstacle;
import data.table.Obstacle;
import utils.ConfigData;
import utils.math.*;
import utils.math.Rectangle;
import utils.math.Shape;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

class GraphicalInterface extends JFrame {

    //Attributs qui peuvent être modifiés avant le lancement
    private HashMap<Integer, SimulatedRobot> simulatedRobots;
    private Table table;
    private boolean colorblindMode;
    private boolean isDrawingPoints;
    private boolean isCreatingObstacleWithMouse;

    //Attributs graphiques
    private BufferedImage backgroundImage;
    private JPanel panel;
    private int WIDTH_FRAME = 1200;      //in pixels
    private int HEIGHT_FRAME = 800;      //in pixels
    private Color DEFAULT_COLOR = new Color(0,0,0,255);
    private Color ROBOT_COLOR = new Color(0,255,0,128);
    private Color ORIENTATION_COLOR = new Color(0,0,255,255);
    private Color FIXED_OBSTACLE_COLOR = new Color(255,0,0,64);
    private Color MOBILE_OBSTACLE_COLOR = new Color(255,255,0,64);
    private Color POINTS_TO_DRAW_COLOR = new Color(255,0,255,255);

    //Attributs pas graphiques
    private ArrayList<Obstacle> fixedObstacles;
    private ArrayList<MobileCircularObstacle> mobileObstacles;
    private long lastTimeUpdate;
    private ArrayList<Vec2> pointsToDraw;
    private Point mousePosition;
    private final int MILLIS_BETWEEN_UPDATES=10;
    private final int WIDTH_TABLE = 3000;      //in millimeters
    private final int HEIGHT_TABLE = 2000;     //in millimeters

    //Permet de savoir si cette instance est démarrée
    private boolean isLaunched = false;

    /* ============================================= Constructeur ============================================= */
    /** Constructeur */
    GraphicalInterface() {
        this.initDefaultPassedParameters();
        this.pointsToDraw=new ArrayList<Vec2>();
        this.lastTimeUpdate=System.currentTimeMillis();
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

    /* ================================== Passage et initialisation de paramètres ============================= */
    /** Méthode instanciant tous les attributs nécessaires au bon fonctionnement de l'interface graphique
     *  Les attributs définits à NULL sont des attributs qu'il faut SET obligatoirement
     */
    private void initDefaultPassedParameters(){
        this.simulatedRobots=new HashMap<Integer, SimulatedRobot>();
        this.isDrawingPoints=true;
        this.colorblindMode=false;
        this.isCreatingObstacleWithMouse=false;

        this.table=null;
    }

    /** Set les robots qui sont instancié pour qu'ils soient affichés dans le simulateur
     * @param simulatedRobots HashMap<Integer, SimulatedRobot> des robots instanciés
     */
    void setSimulatedRobots(HashMap<Integer, SimulatedRobot> simulatedRobots){
        if (canParametersBePassed()){
            this.simulatedRobots=simulatedRobots;
        }
    }

    /** Set si on crée un obstacle en bougeant la souris */
    void setIsCreatingObstacleWithMouse(boolean value){
        if (canParametersBePassed()) {
            this.isCreatingObstacleWithMouse = value;
        }
    }

    /** Set la table
     * @param table table mdr
     */
    void setTable(Table table){
        if (canParametersBePassed()){
            this.table=table;
        }
    }

    /** Set le mode daltonien
     * @param value mode daltonien activé sur TRUE
     */
    void setColorblindMode(boolean value){
        if (canParametersBePassed()){
            this.colorblindMode=value;
        }
    }

    /** Set si les points sont affichés ou non
     * @param value les points sont affichés si TRUE
     */
    void setIsDrawingPoints(boolean value){
        if (canParametersBePassed()){
            this.isDrawingPoints=value;
        }
    }

    /** Permet de savoir si on a lancé le robot simulé */
    private boolean canParametersBePassed(){
        if (this.isLaunched){
            System.out.println("SIMULATEUR : On ne peut pas passer de paramètres à l'interface graphique lorsqu'elle est déjà lancée");
            return false;
        }
        else {
            return true;
        }
    }

    /* ======================================== Lancement de l'instance ======================================== */
    /** Lance l'interface graphique */
    void launch(){
        this.updateColorSchema(this.colorblindMode);
        if (this.isCreatingObstacleWithMouse) {
            this.addSimulatedObstacleWithMouse();
        }
        this.isLaunched=true;
        System.out.println("Interface graphique démarrée");
    }

    /* ======================================== Méthode d'update général ======================================= */
    /** Fonction appelée par le simulateur */
    void tryUpdate(){
        if (this.isLaunched) {
            if (System.currentTimeMillis() - this.lastTimeUpdate > this.MILLIS_BETWEEN_UPDATES) {
                this.lastTimeUpdate = System.currentTimeMillis();
                this.fixedObstacles = table.getFixedObstacles();
                this.mobileObstacles = table.getMobileObstacles();
                this.mousePosition = this.panel.getMousePosition();
                tryMoveSimulatedObstacleWithMouse();
                this.panel.repaint();
            }
        }
    }

    /* ========================================== Méthodes de dessin =========================================== */
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
        g.setColor(FIXED_OBSTACLE_COLOR);
        synchronized (this.fixedObstacles) {
            for (Obstacle obstacle : this.fixedObstacles) {
                drawSingleObstacle(g, obstacle);
            }
        }
        g.setColor(MOBILE_OBSTACLE_COLOR);
        synchronized (this.mobileObstacles) {
            for (Obstacle obstacle : this.mobileObstacles) {
                drawSingleObstacle(g, obstacle);
            }
        }
        g.setColor(DEFAULT_COLOR);
    }

    /** Affiche un obstacle */
    private void drawSingleObstacle(Graphics g, Obstacle obstacle){
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
        int pointDiameter=10;
        g.setColor(POINTS_TO_DRAW_COLOR);
        for(Vec2 vecteur : this.pointsToDraw) {
            vecteur = transformTableCoordsToInterfaceCoords(vecteur);
            g.fillOval(vecteur.getX()-pointDiameter/2, vecteur.getY()-pointDiameter/2, pointDiameter, pointDiameter);
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
        if (this.isDrawingPoints) {
            drawPoints(g);
        }

        drawGraphe(g);
    }

    private void drawGraphe(Graphics g) {
        int pointDiameter=10;
        g.setColor(POINTS_TO_DRAW_COLOR);
        for(Node node : table.getGraphe().getNodes()) {
            Vec2 vecteur = node.getPosition();
            vecteur = transformTableCoordsToInterfaceCoords(vecteur);
            g.fillOval(vecteur.getX()-pointDiameter/2, vecteur.getY()-pointDiameter/2, pointDiameter, pointDiameter);
        }
        g.setColor(DEFAULT_COLOR);
    }

    /* ============ Méthodes de transformation des coordonnées entre la table et la fenêtre graphique ============= */
    /** Transforme une distance de la table pour qu'elle soit affichée correction sur l'interface */
    private int transformTableDistanceToInterfaceDistance(int distanceOnTable){
        return Math.round(distanceOnTable * (this.WIDTH_FRAME / (float)this.WIDTH_TABLE));
    }

    /** Transforme une distance de la table pour qu'elle soit affichée correction sur l'interface */
    private float transformTableDistanceToInterfaceDistance(float distanceOnTable){
        return distanceOnTable * (this.WIDTH_FRAME / (float)this.WIDTH_TABLE);
    }

    /** Transforme les coordonnées de la table pour qu'ils soient affichés correction sur l'interface */
    private Vec2 transformTableCoordsToInterfaceCoords(int xOnTable, int yOnTable) {
        return new VectCartesian(
                (xOnTable + (this.WIDTH_TABLE / 2.0f)) * (this.WIDTH_FRAME / (float) this.WIDTH_TABLE),
                (this.HEIGHT_TABLE - yOnTable) * (this.HEIGHT_FRAME / (float) this.HEIGHT_TABLE)
        );
    }

    /** Transforme les coordonnées de la table pour qu'ils soient affichés correctement sur l'interface */
    private Vec2 transformTableCoordsToInterfaceCoords(Vec2 positionOnTable){
        return transformTableCoordsToInterfaceCoords(positionOnTable.getX(), positionOnTable.getY());
    }



    /** Transforme les coordonnées de l'interface pour qu'ils correspondent à ceux de la table */
    private Vec2 transformInterfaceCoordsToTableCoords(int xOnInterface, int yOnInterface){
        return new VectCartesian(
                (xOnInterface - (this.WIDTH_FRAME / 2.0f)) * (this.WIDTH_TABLE / (float) this.WIDTH_FRAME),
                (this.HEIGHT_FRAME - yOnInterface) * (this.HEIGHT_TABLE/ (float) this.HEIGHT_FRAME)
        );
    }

    /** Transforme les coordonnées de l'interface pour qu'ils correspondent à ceux de la table */
    private Vec2 transformInterfaceCoordsToTableCoords(Vec2 positionOnInterface){
        return transformInterfaceCoordsToTableCoords(positionOnInterface.getX(), positionOnInterface.getY());
    }

    /* ===================================== Méthodes sur l'obstacle simulé ===================================== */
    /** Crée un obstacle simulé avec la souris */
    private void addSimulatedObstacleWithMouse(){
        this.table.SIMULATEDaddMobileObstacle();
    }

    /** Déplace l'obstacle simulé avec la souris */
    private void tryMoveSimulatedObstacleWithMouse(){
        if (this.isCreatingObstacleWithMouse){
            if (this.mousePosition!=null) {
                this.table.SIMULATEDmoveMobileObstacle(
                        transformInterfaceCoordsToTableCoords(this.mousePosition.x, this.mousePosition.y)
                );
            }
            else{
                this.table.SIMULATEDmoveMobileObstacle(new VectCartesian(0, -1000));
            }
        }
    }

    /* ===================================== Méthodes sur le points à dessiner ===================================== */
    /** Définit les points à dessiner */
    void setPointsToDraw(Vec2[] positions) {
        this.clearPointsToDraw();
        for (Vec2 position : positions) {
            this.addPointToDraw(position);
        }
    }

    /** Ajoute un point à dessiner */
    void addPointToDraw(Vec2 position){
        this.pointsToDraw.add(position);
    }

    /** Ajout des points à dessiner */
    void addPointsToDraw(Vec2[] positions){
        for (Vec2 position : positions){
            this.addPointToDraw(position);
        }
    }

    /** Supprime tous les points à dessiner */
    void clearPointsToDraw() {
        this.pointsToDraw.clear();
    }

    /* ============================================ Autres méthodes =========================================== */
    /** Définit si on est en mode daltonien */
    private void updateColorSchema(boolean colorblindMode){
        if (colorblindMode) {
            DEFAULT_COLOR = new Color(0, 0, 0, 255);
            ROBOT_COLOR = new Color(0, 0, 255, 128);
            ORIENTATION_COLOR = new Color(0, 255, 255, 255);
            FIXED_OBSTACLE_COLOR = new Color(255, 255, 0, 64);
            MOBILE_OBSTACLE_COLOR = new Color(255,0,0,64);
            POINTS_TO_DRAW_COLOR = new Color(255,255,255, 255);
        }
        else{
            DEFAULT_COLOR = new Color(0, 0, 0, 255);
            ROBOT_COLOR = new Color(0, 255, 0, 128);
            ORIENTATION_COLOR = new Color(0, 0, 255, 255);
            FIXED_OBSTACLE_COLOR = new Color(255, 0, 0, 64);
            MOBILE_OBSTACLE_COLOR = new Color(255,255,0,64);
            POINTS_TO_DRAW_COLOR = new Color(255,255,255,255);
        }
    }
}