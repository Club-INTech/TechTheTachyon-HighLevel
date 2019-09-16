package simulator;

import data.CouleurPalet;
import data.controlers.Channel;
import utils.RobotSide;
import utils.math.Calculs;
import utils.math.Vec2;
import utils.math.InternalVectCartesian;
import utils.math.VectPolar;

import java.util.LinkedList;
import java.util.List;

public class SimulatedRobot implements IRobot {

    private static final float MONTLHERY_SPEED = 2f; // mm/<tick du simulateur>
    private static final float MONTLHERY_ROT_SPEED = (float) (Math.PI/2f); // rad/s

    private final int port;
    //Attributs qui peuvent être modifiés avant le lancement
    private float speedFactor;
    private SimulatedConnectionManager simulatedLLConnectionManager;

    //Attributs de déplacmeent
    private Vec2 position;          //Position actuelle du robot
    private double orientation;      //Orientation actuelle du robot

    private Vec2 positionTarget;        //Position cible du robot
    private double orientationTarget;    //Orientation cible du robot

    private final float POSITION_TOLERANCE = 0.01f;  //Tolérance sur la position
    private final float ORIENTATION_TOLERANCE = 0.005f;  //Tolérance sur l'orientation

    private final float TRANSLATION_SPEED = 0.5f;     //Vitesse de translation en m/s
    private final float ROTATION_SPEED = 0.002f;      //Vitesse de rotation en rad/s

    private Vec2 START_POSITION = new InternalVectCartesian(0,1000);
    private float START_ORIENTATION = 0;

    private boolean forwardOrBackward;     //Vrai si le robot avance ou recule, faux sinon
    private boolean turning;    //Vrai si le robot tourne, faux sinon

    private boolean previousMovingState = false;
    private boolean stoppedMovingFlag = false;

    private long lastUpdateTime;
    private final int MILLIS_BETWEEN_UPDATES=10;

    //Permet de savoir si cette instance est démarrée
    private boolean isLaunched;

    // variables utiles pour le mode montlhery
    private boolean montlheryMode;
    private boolean forcedMovement;
    private float forcedTranslationSpeed;
    private float forcedRotationSpeed;

    // Ascenseurs
    private List<CouleurPalet> leftStack;
    private List<CouleurPalet> rightStack;

    // Positions des bras (pour le debug)
    private String leftArmPosition = "unknown";
    private String rightArmPosition = "unknown";

    /* ============================================= Constructeur ============================================= */
    /** Constructeur */
    SimulatedRobot(int port){
        this.port = port;
        this.initDefaultPassedParameters();
        this.lastUpdateTime=System.currentTimeMillis();
        this.forwardOrBackward=false;
        this.turning=false;
        this.position = START_POSITION;
        this.positionTarget = START_POSITION;
        this.orientation = START_ORIENTATION;
        this.orientationTarget = START_ORIENTATION;
        this.isLaunched=false;
        this.leftStack=new LinkedList<>();
        this.rightStack=new LinkedList<>();
    }

    /* ================================== Passage et initialisation de paramètres ============================= */
    /** Méthode instanciant tous les attributs nécessaires au bon fonctionnement d'un robot simulé
     *  Les attributs définits à NULL sont des attributs qu'il faut SET obligatoirement
     */
    private void initDefaultPassedParameters(){
        this.speedFactor=1;
        this.simulatedLLConnectionManager = null;
    }

    /** Set le facteur de vitesse de déplacement du robot
     * TODO : facteur de vitesse de déroulement du match
     * @param speedFactor
     */
    void setSpeedFactor(float speedFactor){
        if (canParametersBePassed()) {
            this.speedFactor = speedFactor;
        }
    }

    /** Set la connexion qui utilisée pour envoyer des messages venant du LL vers le HL
     * @param simulatedLLConnectionManager connexion en question
     */
    void setSimulatedLLConnectionManager(SimulatedConnectionManager simulatedLLConnectionManager){
        if (canParametersBePassed()) {
            this.simulatedLLConnectionManager = simulatedLLConnectionManager;
        }
    }

    /** Permet de savoir si on a lancé le robot simulé
     */
    private boolean canParametersBePassed(){
        if (this.isLaunched){
            System.out.println("SIMULATEUR : On ne peut pas passer de paramètres au robot simulé lorsqu'il est déjà lancé");
            return false;
        }
        else{
            return true;
        }
    }
    /* ======================================== Lancement de l'instance ======================================== */
    /** Lance le manager du simulateur */
    void launch(){
        this.isLaunched=true;
        System.out.println(String.format("(%d) Robot simulé démarré", this.simulatedLLConnectionManager.getPort()));
    }

    /* ======================================== Méthode d'update général ======================================= */
    /** Fonction appelée pour tryUpdate la position du robot */
    void tryUpdate(){
        if (this.isLaunched) {
            if (this.timeSinceLastUpdate() > this.MILLIS_BETWEEN_UPDATES) {
                updateOrientation();
                updatePosition();
                tryRaiseStoppedMovingFlag();
                trySendStoppedMovingMessage();
                sendRealtimePosition();
                this.lastUpdateTime = System.currentTimeMillis();
            }
        }
    }

    /* =============================== Méthodes de synchronisation LL<->HL ===================================== */
    public void confirmOrder(String givenOrder) {
        simulatedLLConnectionManager.sendMessage(String.format("%s%s %s\n", Channel.EVENTS.getHeaders(), "confirmOrder", givenOrder));
    }

    public void sendConfirmationForElevator(String side) {
        simulatedLLConnectionManager.sendMessage(String.format("%s%sElevatorStopped\n", Channel.EVENTS.getHeaders(), side));
    }

    public void sendArmConfirmation(RobotSide side) {
        simulatedLLConnectionManager.sendMessage(String.format("%sarmFinishedMovement %s\n", Channel.EVENTS.getHeaders(), side.name().toLowerCase()));
    }

    public void sendPong() {
        simulatedLLConnectionManager.sendMessage(String.format("%s%s\n", Channel.EVENTS.getHeaders(), "pong"));
    }

    /* =============================== Méthodes de signalisation d'arrêt du robot ============================== */
    /** Force the raise of the stoppedMovingFlag */
    public void forceRaiseStoppedMovingFlag(){
        this.stoppedMovingFlag=true;
    }

    /** Try to raise stoppedMovingFlag */
    private void tryRaiseStoppedMovingFlag() {
        if (this.previousMovingState){
            if (!this.isMoving()){
                this.stoppedMovingFlag=true;
            }
        }
        this.previousMovingState=this.isMoving();
    }

    /** Envoie un message quand on a fini un mouvement */
    private void trySendStoppedMovingMessage(){
        if (this.stoppedMovingFlag) {
            System.out.println("stoppedMoving");
            this.simulatedLLConnectionManager.sendMessage(String.format("%s%s\n", Channel.EVENTS.getHeaders(), "stoppedMoving"));
            sendRealtimePosition();
            this.stoppedMovingFlag=false;
        }
    }

    /** Renvoie si le robot bouge */
    private boolean isMoving(){
        return this.turning || this.forwardOrBackward;
    }

    /* =============================== Méthodes de gestion des ascenseurs =================================== */
    public void setElevatorContents(RobotSide side, String[] contents, int startIndex) {
        List<CouleurPalet> stack;
        switch (side) {
            case LEFT:
                stack = leftStack;
                break;

            case RIGHT:
                stack = rightStack;
                break;

            default:
                throw new IllegalArgumentException("Side: "+side);
        }

        synchronized (stack) {
            stack.clear();
            for (int i = startIndex; i < contents.length; i++) {
                stack.add(CouleurPalet.valueOf(contents[i]));
            }
        }
    }

    public List<CouleurPalet> getElevatorOrNull(RobotSide side) {
        switch (side) {
            case LEFT:
                return leftStack;

            case RIGHT:
                return rightStack;

            default: // ne doit jamais arriver
                return null;
        }
    }

    /* =============================== Méthodes d'envoide la position du robot ============================== */
    /** Envoie la position à l'instance de HL qui est en relation avec ce robot simulé */
    private void sendRealtimePosition(){
        this.simulatedLLConnectionManager.sendMessage(String.format("%s%d %d %.5f\n", Channel.ROBOT_POSITION.getHeaders(), this.getX(), this.getY(), this.getOrientation()).replace(",", "."));
    }

    /* ======================== Méthodes de mise à jour de la position et de l'orientation ================== */
    /** Update l'orientation pas à pas en fonction du delta entre l'orientation actuelle et l'orientation cible */
    private void updateOrientation(){
        if(montlheryMode) {
            this.turning = Math.abs(forcedRotationSpeed) > 0.1;
            if(turning) {
                this.orientation += forcedRotationSpeed * MILLIS_BETWEEN_UPDATES/1000.0;
            }

            return;
        }
        if (Math.abs(moduloSpec(this.orientationTarget) - moduloSpec(this.orientation)) > this.ORIENTATION_TOLERANCE){
            if (Math.abs(this.orientationTarget - this.orientation) < this.ROTATION_SPEED * this.timeSinceLastUpdate()){
                this.orientation=moduloSpec(this.orientationTarget);
                this.turning=true;
            }
            else {
                //Formule permettant de tourner dans le bon sens
                this.orientation+=
                        ((this.orientationTarget+2*Math.PI)%(2*Math.PI) - (this.orientation+2*Math.PI)%(2*Math.PI))/
                                Math.abs((this.orientationTarget+2*Math.PI)%(2*Math.PI) - (this.orientation+2*Math.PI)%(2*Math.PI))
                                *this.ROTATION_SPEED *this.timeSinceLastUpdate();
                this.orientation=moduloSpec(this.orientation);
                this.turning=true;
            }
        }
        else{
            this.orientation=moduloSpec(this.orientationTarget);
            this.turning=false;
        }
    }

    /** Update la position pas à pas en fonction de la distance restante entre la position actuelle et la position cible */
    private void updatePosition(){
        if (!this.turning) {
            if(montlheryMode) {
                forwardOrBackward = Math.abs(forcedTranslationSpeed) > 0.01f;
                if(forwardOrBackward) {
                    float dirX = (float) (Math.cos(orientation)*forcedTranslationSpeed);
                    float dirY = (float) (Math.sin(orientation)*forcedTranslationSpeed);
                    setPosition(position.plusVector(new InternalVectCartesian(dirX, dirY)));
                }

                return;
            }

            if (this.positionTarget.distanceTo(this.position) > this.POSITION_TOLERANCE) {
                if (this.positionTarget.distanceTo(this.position) < this.TRANSLATION_SPEED * this.timeSinceLastUpdate()) {
                    this.position = this.positionTarget;
                    this.forwardOrBackward = true;
                } else {
                    this.position.plus(this.positionTarget.minusVector(this.position).homothetie(this.TRANSLATION_SPEED * this.timeSinceLastUpdate() / (float) this.positionTarget.minusVector(this.position).getR()));
                    this.forwardOrBackward = true;
                }
            } else {
                this.position = this.positionTarget;
                this.forwardOrBackward = false;
            }
        }
    }

    /** Renvoie le temps depuis la dernière tryUpdate */
    private long timeSinceLastUpdate(){
        return Math.round((System.currentTimeMillis() - this.lastUpdateTime)*this.speedFactor);
    }

    /* ======================== Méthodes de modification des objectifs cibles du robot ========================== */
    /** Fait avancer le robot de delta */
    void moveLengthwise(int delta){
        if (Math.abs(delta) < this.POSITION_TOLERANCE){
            this.forceRaiseStoppedMovingFlag();
        }
        else {
            Vec2 orientationVector = new VectPolar(1, this.orientation);
            this.positionTarget = this.position.plusVector(orientationVector.homothetie((float) delta));
        }
    }

    /** Fait tourner le robot de delta */
    void turn(float aim) {
        if (Math.abs(moduloSpec(this.orientationTarget)-moduloSpec(aim)) < this.ORIENTATION_TOLERANCE){
            this.forceRaiseStoppedMovingFlag();
        }
        else {
            this.orientationTarget = moduloSpec(aim);
        }
    }

    /** Fait bouger le robot vers un point */
    void goTo(Vec2 position){
        if (this.positionTarget.distanceTo(position)<this.POSITION_TOLERANCE){
            this.forceRaiseStoppedMovingFlag();
        }
        else {
            this.positionTarget = position;
            this.orientationTarget = (float) position.minusVector(this.position).getA();
        }
    }

    /** Fait arrêter le robot */
    void stop(){
        this.positionTarget.set(position);
        this.positionTarget=this.position;
        this.orientationTarget=this.orientation;
        turning = false;
        forwardOrBackward = false;
        this.forceRaiseStoppedMovingFlag();
    }

    /* ========================================= Méthodes de maths ============================================== */
    /** Fait un modulo entre -Pi et Pi d'un angle en radians */
    private double moduloSpec(double angle){
        return Calculs.modulo(angle,Math.PI);
    }

    /* ======================== Setters de la position et de l'orientation du robot ============================= */
    /** Set la position du robot */
    void setPosition(Vec2 position){
        this.positionTarget=position;
        this.position=position;
    }

    /** Set l'orientation du robot */
    void setOrientation(double orientation){
        this.orientationTarget=moduloSpec(orientation);
        this.orientation=moduloSpec(orientation);
    }

    /* ======================== Getters de la position et de l'orientation du robot ========================== */
    /** Renvoie la position en X du robot */
    public int getX(){ return this.position.getX(); }

    /** Renvoie la position en Y du robot */
    public int getY(){ return this.position.getY(); }

    /** Renvoie la position du robot */
    public Vec2 getPosition(){
        return this.position;
    }

    /** Renvoie l'orientation du robot */
    public double getOrientation(){ return this.orientation; }

    /* ======================== Tout ce qui touche au mode Montlhery pour le tester ======================== */
    public void setMontlheryMode() {
        montlheryMode = true;
    }

    public void goForward() {
        if(!montlheryMode) return;
        forcedTranslationSpeed = +MONTLHERY_SPEED;
        forcedRotationSpeed = 0f;
        forcedMovement = true;
    }

    public void goBackwards() {
        if(!montlheryMode) return;
        forcedTranslationSpeed = -MONTLHERY_SPEED;
        forcedRotationSpeed = 0f;
        forcedMovement = true;
    }

    public void turnLeft() {
        if(!montlheryMode) return;
        forcedRotationSpeed = +MONTLHERY_ROT_SPEED;
        forcedTranslationSpeed = 0f;
        forcedMovement = true;
    }

    public void turnRight() {
        if(!montlheryMode) return;
        forcedRotationSpeed = -MONTLHERY_ROT_SPEED;
        forcedTranslationSpeed = 0f;
        forcedMovement = true;
    }

    public void sstop() {
        forcedTranslationSpeed = 0f;
        forcedRotationSpeed = 0f;
        forcedMovement = false;
    }

    public Vec2 getTargetPosition() {
        return positionTarget;
    }

    public int getPort() {
        return port;
    }

    public String getLeftArmPosition() {
        return leftArmPosition;
    }

    public String getRightArmPosition() {
        return rightArmPosition;
    }

    public void setArmPosition(RobotSide side, String position) {
        switch (side) {
            case LEFT:
                leftArmPosition = position;
                break;

            case RIGHT:
                rightArmPosition = position;
                break;
        }
    }

    public void sendJumperOkay() {
        simulatedLLConnectionManager.sendMessage(String.format("%sgogogofast\n", Channel.EVENTS.getHeaders()));
    }

}
