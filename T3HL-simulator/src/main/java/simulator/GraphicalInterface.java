package simulator;

import data.CouleurPalet;
import data.Table;
import data.graphe.Node;
import data.table.MobileCircularObstacle;
import data.table.Obstacle;
import locomotion.PathFollower;
import utils.ConfigData;
import utils.RobotSide;
import utils.math.*;
import utils.math.Rectangle;
import utils.math.Shape;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.util.List;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class GraphicalInterface extends JFrame {

    private static final float ASPECT_RATIO = 16f/9f;

    /**
     * Police utilisée pour écrire à l'écran
     */
    private final Font font;

    //Attributs qui peuvent être modifiés avant le lancement
    private HashMap<Integer, IRobot> simulatedRobots;
    private Table table;
    private boolean colorblindMode;
    private boolean isDrawingPoints;

    /**
     * Dessin des chemins que les robots doivent suivre?
     */
    private boolean isDrawingPaths;
    /**
     * Dessin du graphe?
     */
    private boolean isDrawingGraph;
    private boolean isCreatingObstacleWithMouse;

    //Attributs graphiques
    private BufferedImage backgroundImage;
    private JPanel panel;
    private int TABLE_PIXEL_WIDTH = 980;      //in pixels
    private int STACKS_PIXEL_WIDTH = 300;
    private int TOTAL_PIXEL_WIDTH = this.TABLE_PIXEL_WIDTH+this.STACKS_PIXEL_WIDTH;
    private int TABLE_PIXEL_HEIGHT = (int) ((TABLE_PIXEL_WIDTH+STACKS_PIXEL_WIDTH)/ASPECT_RATIO);      //in pixels
    private Color DEFAULT_COLOR = new Color(0,0,0,255);
    private Color ROBOT_COLOR = new Color(0,255,0,128);
    private Color ORIENTATION_COLOR = new Color(0,0,255,255);
    private Color FIXED_OBSTACLE_COLOR = new Color(255,0,0,64);
    private Color TEMPORARY_OBSTACLE_COLOR = new Color(0,0,255,128);
    private Color MOBILE_OBSTACLE_COLOR = new Color(255,255,0,64);
    private Color POINTS_TO_DRAW_COLOR = new Color(255,0,255,255);
    private Color RIDGE_COLOR = new Color(0,0,0,255);
    private Color PATH_COLOR = new Color(255,0,0,255);

    //Attributs pas graphiques
    private ArrayList<Obstacle> fixedObstacles;
    private ArrayList<Obstacle> temporaryObstacles;
    private ConcurrentLinkedQueue<MobileCircularObstacle> mobileObstacles;
    private long lastTimeUpdate;
    private ArrayList<Vec2> pointsToDraw;
    private Point mousePosition;
    private final int MILLIS_BETWEEN_UPDATES=10;
    private final int WIDTH_TABLE = 3000;      //in millimeters
    private final int HEIGHT_TABLE = 2000;     //in millimeters

    //Permet de savoir si cette instance est démarrée
    private boolean isLaunched = false;
    private PathFollower pathfollowerToShow;

    private int pathfollowerToShowPort;
    private long startTime;
    private float timeScale;

    /* ============================================= Constructeur ============================================= */
    /** Constructeur */
    GraphicalInterface() {
        this.initDefaultPassedParameters();

        this.fixedObstacles = new ArrayList<>();
        this.temporaryObstacles = new ArrayList<>();
        this.mobileObstacles = new ConcurrentLinkedQueue<>();

        this.pointsToDraw=new ArrayList<>();
        this.lastTimeUpdate=System.currentTimeMillis();
        try {
            this.backgroundImage = ImageIO.read(getClass().getResourceAsStream("/Table2020.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.setTitle("Simulateur");
        this.font = new Font("Consolas", Font.PLAIN, 15);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.panel = new JPanel(){
            @Override
            protected void paintComponent(Graphics graphics) {
                graphics.setFont(font);
                updateGraphics(graphics);
                Toolkit.getDefaultToolkit().sync();
            }
        };
        this.panel.setDoubleBuffered(true);
        this.panel.setVisible(true);
        this.panel.setPreferredSize(new Dimension(TOTAL_PIXEL_WIDTH, this.TABLE_PIXEL_HEIGHT));
        this.getContentPane().add(this.panel);
        this.setVisible(true);
        this.pack();
    }

    /* ================================== Passage et initialisation de paramètres ============================= */
    /** Méthode instanciant tous les attributs nécessaires au bon fonctionnement de l'interface graphique
     *  Les attributs définits à NULL sont des attributs qu'il faut SET obligatoirement
     */
    private void initDefaultPassedParameters(){
        this.simulatedRobots= new HashMap<>();
        this.isDrawingPoints=true;
        this.isDrawingGraph=true;
        this.isDrawingPaths=true;
        this.colorblindMode=false;
        this.isCreatingObstacleWithMouse=false;

        this.table=null;
    }

    /** Set les robots qui sont instancié pour qu'ils soient affichés dans le simulateur
     * @param simulatedRobots HashMap<Integer, SimulatedRobot> des robots instanciés
     */
    void setSimulatedRobots(HashMap<Integer, IRobot> simulatedRobots){
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
        startTime = System.currentTimeMillis();
        System.out.println("Interface graphique démarrée");
    }

    /* ======================================== Méthode d'update général ======================================= */
    /** Fonction appelée par le simulateur */
    void tryUpdate(){
        if (this.isLaunched) {
            if (System.currentTimeMillis() - this.lastTimeUpdate > this.MILLIS_BETWEEN_UPDATES) {
                this.lastTimeUpdate = System.currentTimeMillis();
                this.fixedObstacles = table.getFixedObstacles();
                this.temporaryObstacles = table.getTemporaryObstacles();
                this.mobileObstacles = table.getMobileObstacles();
                this.mousePosition = this.panel.getMousePosition();
                tryMoveSimulatedObstacleWithMouse();
                this.panel.repaint();
            }
        }
    }

    /* ========================================== Méthodes de dessin =========================================== */
    /** Affiche un robot */
    private void drawRobot(IRobot simulatedRobot, Graphics g, int x, int y, double orientation, int diameter, int index){
        g.setColor(ROBOT_COLOR);
        g.fillOval(x-diameter/2,y-diameter/2, diameter,diameter);
        g.setColor(ORIENTATION_COLOR);
        g.drawLine(x, y, Math.round(x+(float)Math.cos(orientation)*diameter), Math.round(y-(float)Math.sin(orientation)*diameter));

        if(this.isDrawingPaths) {
            if(pathfollowerToShow != null && simulatedRobot.getPort() == pathfollowerToShowPort) {
                g.setColor(PATH_COLOR);
                Vec2 prev = transformTableCoordsToInterfaceCoords(simulatedRobot.getPosition());
                Vec2 target = transformTableCoordsToInterfaceCoords(simulatedRobot.getTargetPosition());
                g.drawLine(prev.getX(), prev.getY(), target.getX(), target.getY());
                prev = target;
                for(Vec2 p : pathfollowerToShow.getQueue()) {
                    Vec2 screenPos = transformTableCoordsToInterfaceCoords(p);
                    g.drawLine(prev.getX(), prev.getY(), screenPos.getX(), screenPos.getY());
                    prev = screenPos;
                }
            }
        }
        drawDebug(g, simulatedRobot, index);

        g.setColor(DEFAULT_COLOR);
    }

    private void drawDebug(Graphics g, IRobot simulatedRobot, int index) {
        drawElevators(g, simulatedRobot, index);
        drawArmPositions(g, simulatedRobot, index);
    }

    private void drawArmPositions(Graphics g, IRobot simulatedRobot, int index) {
        g.setColor(Color.BLACK);

        int totalElevatorPanelHeight = 100;
        int margin = 10;
        int baseY = index*totalElevatorPanelHeight+margin;
        int baseX = TABLE_PIXEL_WIDTH + margin*2 + 120;
        int textHeight = g.getFontMetrics().getHeight();

        g.drawString("Lpos: "+simulatedRobot.getLeftArmPosition(), baseX, baseY+textHeight);
        g.drawString("Rpos: "+simulatedRobot.getRightArmPosition(), baseX, baseY+textHeight*2+margin);
    }

    private void drawElevators(Graphics g, IRobot simulatedRobot, int index) {
        g.setColor(Color.BLACK);

        int totalElevatorPanelHeight = 100;
        int baseY = index*totalElevatorPanelHeight;
        int margin = 10;
        int baseX = TABLE_PIXEL_WIDTH + margin;
        int textHeight = g.getFontMetrics().getHeight();
        g.drawString("Robot(port="+simulatedRobot.getPort()+")", baseX, baseY+textHeight);

        for(RobotSide side : RobotSide.values()) {
            g.setColor(Color.BLACK); // on reset la couleur car le dessin des palets peut changer la couleur

            List<CouleurPalet> elevator = simulatedRobot.getElevatorOrNull(side);
            if(elevator == null)
                continue;
            int paletHeight = 20;
            int paletSpacing = 2;
            int paletWidth = 50;
            int innerSpacing = 2;

            int elevatorBottomY = baseY + textHeight + (paletHeight + paletSpacing) * 5 + innerSpacing;
            g.drawLine(baseX, baseY+textHeight, baseX, elevatorBottomY);
            g.drawLine(baseX, elevatorBottomY, baseX+paletWidth+innerSpacing*2, elevatorBottomY);
            g.drawLine(baseX+paletWidth+innerSpacing*2, baseY+textHeight, baseX+paletWidth+innerSpacing*2, elevatorBottomY);

            g.drawString(side.toString(), baseX, elevatorBottomY+textHeight);

            // render palets
            int paletYOffset = -paletHeight-innerSpacing*2;
            for(CouleurPalet colour : elevator) {
                switch (colour) {
                    case ROUGE:
                        g.setColor(Color.RED);
                        break;

                    case BLEU:
                        g.setColor(Color.BLUE);
                        break;

                    case VERT:
                        g.setColor(Color.GREEN);
                        break;

                    case GOLDENIUM:
                        g.setColor(Color.ORANGE);
                        break;

                    case PAS_DE_PALET:
                        g.setColor(Color.DARK_GRAY);
                        break;
                }
                g.fillRect(baseX+innerSpacing, elevatorBottomY+paletYOffset+innerSpacing, paletWidth, paletHeight);
                paletYOffset -= paletHeight+paletSpacing;
            }

            baseX += paletWidth + innerSpacing*2 + margin;
        }
    }

    /** Affiche le background */
    private void drawBackground(Graphics g){
        g.drawImage(this.backgroundImage,0,0, this.TABLE_PIXEL_WIDTH, this.TABLE_PIXEL_HEIGHT, null);
    }

    /** Affiche les obstacles */
    private void drawObstacles(Graphics g){
        g.setColor(FIXED_OBSTACLE_COLOR);
        synchronized (this.fixedObstacles) {
            for (Obstacle obstacle : this.fixedObstacles) {
                drawSingleObstacle(g, obstacle);
            }
        }
        g.setColor(TEMPORARY_OBSTACLE_COLOR);
        synchronized (this.temporaryObstacles) {
            for (Obstacle obstacle : this.temporaryObstacles) {
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
            if(obstacle instanceof MobileCircularObstacle) {
                Circle pathShape = ((MobileCircularObstacle) obstacle).getPathfindingShape();
                Vec2 center = transformTableCoordsToInterfaceCoords(pathShape.getCenter());
                float diameter = transformTableDistanceToInterfaceDistance(pathShape.getRadius()*2);
                g.drawOval(center.getX()-Math.round(diameter/2), center.getY()-Math.round(diameter/2), Math.round(diameter), Math.round(diameter));

                drawPrimitiveShape(g, shape);
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
        g.clearRect(0,0,this.TABLE_PIXEL_WIDTH+this.STACKS_PIXEL_WIDTH, this.TABLE_PIXEL_HEIGHT);
    }

    /** Met à jour l'affichage */
    private void updateGraphics(Graphics g){
        clearScreen(g);
        drawBackground(g);
        drawObstacles(g);
        int index = 0;

        if(this.isDrawingGraph) {
            drawGraphe(g);
        }

        for (IRobot simulatedRobot : simulatedRobots.values()) {
            Vec2 coordsOnInterface = transformTableCoordsToInterfaceCoords(simulatedRobot.getX(), simulatedRobot.getY());
            int diameterOnInterface = transformTableDistanceToInterfaceDistance((Integer) ConfigData.ROBOT_RAY.getDefaultValue()*2);
            drawRobot(simulatedRobot, g, coordsOnInterface.getX(), coordsOnInterface.getY(), simulatedRobot.getOrientation(), diameterOnInterface, index);
            index++;
        }
        if (this.isDrawingPoints) {
            drawPoints(g);
        }

        drawTime(g);
    }

    private void drawTime(Graphics g) {
        g.setColor(Color.RED);
        long elapsedTime = (long) ((System.currentTimeMillis() - startTime) * timeScale);
        long millis = elapsedTime % 1000;
        long seconds = elapsedTime/1000;
        String message = String.format("%03d:%03d", seconds, millis);
        int messageWidth = g.getFontMetrics().stringWidth(message);
        g.drawString(message, TOTAL_PIXEL_WIDTH-messageWidth, TABLE_PIXEL_HEIGHT);
    }

    private void drawGraphe(Graphics g) {
        int pointDiameter=10;
        try {
            table.getGraphe().readLock().lock();

            /*
            g.setColor(RIDGE_COLOR);
            for(Ridge ridge : table.getGraphe().getRidges()) {
                Vec2 pointA = transformTableCoordsToInterfaceCoords(ridge.getSeg().getPointA());
                Vec2 pointB = transformTableCoordsToInterfaceCoords(ridge.getSeg().getPointB());
                //  if(simulatedRobots.values().stream().anyMatch(it -> ridge.getSeg().getPointA().distanceTo(it.getPosition()) <= 100 || ridge.getSeg().getPointB().distanceTo(it.getPosition()) <= 100)) {
                if(Math.random() < 0.025 && ridge.isReachable(table.getGraphe())) {
                    g.drawLine(pointA.getX(), pointA.getY(), pointB.getX(), pointB.getY());
                }
                // }
            }*/

            g.setColor(POINTS_TO_DRAW_COLOR);
            for(Node node : table.getGraphe().getNodes()) {
                Vec2 vecteur = node.getPosition();
                if(table.isPositionInFixedObstacle(node.getPosition())) {
                    continue;
                }
                vecteur = transformTableCoordsToInterfaceCoords(vecteur);
                g.fillOval(vecteur.getX()-pointDiameter/2, vecteur.getY()-pointDiameter/2, pointDiameter, pointDiameter);
            }
        } finally {
            table.getGraphe().readLock().unlock();
        }
        g.setColor(DEFAULT_COLOR);
    }

    /* ============ Méthodes de transformation des coordonnées entre la table et la fenêtre graphique ============= */
    /** Transforme une distance de la table pour qu'elle soit affichée correction sur l'interface */
    private int transformTableDistanceToInterfaceDistance(int distanceOnTable){
        return Math.round(distanceOnTable * (this.TABLE_PIXEL_WIDTH / (float)this.WIDTH_TABLE));
    }

    /** Transforme une distance de la table pour qu'elle soit affichée correction sur l'interface */
    private float transformTableDistanceToInterfaceDistance(float distanceOnTable){
        return distanceOnTable * (this.TABLE_PIXEL_WIDTH / (float)this.WIDTH_TABLE);
    }

    /** Transforme les coordonnées de la table pour qu'ils soient affichés correction sur l'interface */
    private Vec2 transformTableCoordsToInterfaceCoords(int xOnTable, int yOnTable) {
        return new InternalVectCartesian(
                (xOnTable + (this.WIDTH_TABLE / 2.0f)) * (this.TABLE_PIXEL_WIDTH / (float) this.WIDTH_TABLE),
                (this.HEIGHT_TABLE - yOnTable) * (this.TABLE_PIXEL_HEIGHT / (float) this.HEIGHT_TABLE)
        );
    }

    /** Transforme les coordonnées de la table pour qu'ils soient affichés correctement sur l'interface */
    private Vec2 transformTableCoordsToInterfaceCoords(Vec2 positionOnTable){
        return transformTableCoordsToInterfaceCoords(positionOnTable.getX(), positionOnTable.getY());
    }



    /** Transforme les coordonnées de l'interface pour qu'ils correspondent à ceux de la table */
    private Vec2 transformInterfaceCoordsToTableCoords(int xOnInterface, int yOnInterface){
        return new InternalVectCartesian(
                (xOnInterface - (this.TABLE_PIXEL_WIDTH / 2.0f)) * (this.WIDTH_TABLE / (float) this.TABLE_PIXEL_WIDTH),
                (this.TABLE_PIXEL_HEIGHT - yOnInterface) * (this.HEIGHT_TABLE/ (float) this.TABLE_PIXEL_HEIGHT)
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
                Vec2 tableCoords = transformInterfaceCoordsToTableCoords(this.mousePosition.x, this.mousePosition.y);
                if(!tableCoords.equals(table.getSimulatedObstacle().getPosition())) {
                    this.table.SIMULATEDmoveMobileObstacle(tableCoords);
                }
            }
            else{
                this.table.SIMULATEDmoveMobileObstacle(new InternalVectCartesian(0, -1000));
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

    public boolean isDrawingPaths() {
        return isDrawingPaths;
    }

    public void setDrawingPaths(boolean drawingPaths) {
        isDrawingPaths = drawingPaths;
    }

    public boolean isDrawingGraph() {
        return isDrawingGraph;
    }

    public void setDrawingGraph(boolean drawingGraph) {
        isDrawingGraph = drawingGraph;
    }

    public void setPathfollowerToShow(PathFollower follower, int port) {
        this.pathfollowerToShow = follower;
        this.pathfollowerToShowPort = port;
    }

    public PathFollower getPathfollowerToShow() {
        return pathfollowerToShow;
    }

    public void setTimeScale(float timeScale) {
        this.timeScale = timeScale;
    }

    public float getTimeScale() {
        return timeScale;
    }

    public void resetTimer() {
        this.startTime = System.currentTimeMillis();
    }
}