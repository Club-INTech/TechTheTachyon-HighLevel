import data.controlers.Channel;
import utils.math.Vec2;
import utils.math.VectCartesian;
import utils.math.VectPolar;

public class SimulatedRobot {

    private Vec2 position;          //Position actuelle du robot
    private double orientation;      //Orientation actuelle du robot

    private Vec2 positionTarget;        //Position cible du robot
    private double orientationTarget;    //Orientation cible du robot

    private final float POSITION_TOLERANCE = 0.01f;  //Tolérance sur la position
    private final float ORIENTATION_TOLERANCE = 0.001f;  //Tolérance sur l'orientation

    private final float TRANSLATION_SPEED = 0.5f;     //Vitesse de translation en m/s
    private final float ROTATION_SPEED = 0.001f;      //Vitesse de rotation en rad/s

    private Vec2 START_POSITION = new VectCartesian(0,1000);
    private float START_ORIENTATION = 0;

    private boolean forwardOrBackward;     //Vrai si le robot avance ou recule, faux sinon
    private boolean turning;    //Vrai si le robot tourne, faux sinon

    private long lastUpdateTime;
    private final int MILLIS_BETWEEN_UPDATES=10;

    private boolean previousMovingState = false;

    private SimulatedConnectionManager simulatedLLConnectionManager;

    /** Constructeur */
    SimulatedRobot(SimulatedConnectionManager simulatedLLConnectionManager){
        this.forwardOrBackward=false;
        this.lastUpdateTime=System.currentTimeMillis();
        this.turning=false;
        this.position = START_POSITION;
        this.positionTarget = START_POSITION;
        this.orientation = START_ORIENTATION;
        this.orientationTarget = START_ORIENTATION;
        this.simulatedLLConnectionManager=simulatedLLConnectionManager;
    }

    /** Fonction appelée pour tryUpdate la position du robot */
    void tryUpdate(){
        if (this.timeSinceLastUpdate() > this.MILLIS_BETWEEN_UPDATES) {
            updateOrientation();
            updatePosition();
            trySendingStoppedMovingMessage();
            sendRealtimePosition();
            this.lastUpdateTime = System.currentTimeMillis();
        }
    }

    /** Envoie un message quand on a fini un mouvement */
    private void trySendingStoppedMovingMessage() {
        if (this.previousMovingState){
            if (!this.isMoving()){
                System.out.println("test");
                this.simulatedLLConnectionManager.sendMessage(String.format("%s%s\n", Channel.EVENT.getHeaders(),"stoppedMoving"));
                this.simulatedLLConnectionManager.sendMessage(String.format("%s%d %d %.3f\n", Channel.ROBOT_POSITION.getHeaders(), this.getX(), this.getY(), this.getOrientation()).replace(",", "."));
            }
        }
        this.previousMovingState=this.isMoving();
    }

    /** Envoie la position à l'instance de HL qui est en relation avec ce robot simulé */
    private void sendRealtimePosition(){
        if (this.isMoving()) {
            this.simulatedLLConnectionManager.sendMessage(String.format("%s%d %d %.3f\n", Channel.ROBOT_POSITION.getHeaders(), this.getX(), this.getY(), this.getOrientation()).replace(",", "."));
        }
    }

    /** Update l'orientation pas à pas en fonction du delta entre l'orientation actuelle et l'orientation cible */
    private void updateOrientation(){
        if (Math.abs(this.orientationTarget - this.orientation) > this.ORIENTATION_TOLERANCE){
            if (Math.abs(this.orientationTarget - this.orientation) < this.ROTATION_SPEED * this.timeSinceLastUpdate()){
                this.orientation=this.orientationTarget;
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
            this.orientation=this.orientationTarget;
            this.turning=false;
        }
    }

    /** Update la position pas à pas en fonction de la distance restante entre la position actuelle et la position cible */
    private void updatePosition(){
        if (!this.turning) {
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
        return (System.currentTimeMillis() - this.lastUpdateTime);
    }

    /** Renvoie si le robot bouge */
    boolean isMoving(){
        return this.turning || this.forwardOrBackward;
    }

    /** Fait avancer le robot de delta */
    void moveLengthwise(int delta){
        Vec2 orientationVector = new VectPolar(1,this.orientation);
        this.positionTarget=this.position.plusVector(orientationVector.homothetie((float)delta));
    }

    /** Fait tourner le robot de delta */
    void turn(float aim){
        this.orientationTarget=moduloSpec(aim);
    }

    /** Fait arrêter le robot */
    void stop(){
        this.positionTarget=this.position;
        this.orientationTarget=this.orientation;
    }

    /** Fait bouger le robot vers un point */
    void goTo(Vec2 position){
        this.positionTarget = position;
        this.orientationTarget = (float)position.minusVector(this.position).getA();
    }

    /** Set la position du robot */
    void setPosition(Vec2 position){
        this.positionTarget=position;
        this.position=position;
    }

    /** Set l'orientation du robot */
    void setOrientation(float orientation){
        this.orientationTarget=orientation;
        this.orientation=orientation;
    }

    /** Fait un modulo entre -Pi et Pi d'un angle en radians */
    private double moduloSpec(double angle){
        double moduloedAngle = angle % (float)(2*Math.PI);
        if (moduloedAngle>Math.PI){
            moduloedAngle-=2*Math.PI;
        }
        return moduloedAngle;
    }

    /** Renvoie la position en X du robot */
    int getX(){ return this.position.getX(); }

    /** Renvoie la position en Y du robot */
    int getY(){ return this.position.getY(); }

    /** Renvoie l'orientation du robot */
    double getOrientation(){ return this.orientation; }

}
