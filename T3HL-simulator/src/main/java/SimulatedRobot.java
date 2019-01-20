import utils.math.Vec2;
import utils.math.VectCartesian;
import utils.math.VectPolar;

public class SimulatedRobot {

    private Vec2 position;          //Position actuelle du robot
    private float orientation;      //Orientation actuelle du robot

    private Vec2 positionTarget;        //Position cible du robot
    private float orientationTarget;    //Orientation cible du robot

    private final float POSITION_TOLERANCE = 0.01f;  //Tolérance sur la position
    private final float ORIENTATION_TOLERANCE = 0.001f;  //Tolérance sur l'orientation

    private final float TRANSLATION_SPEED = 0.5f;     //Vitesse de translation en m/s
    private final float ROTATION_SPEED = 0.001f;           //Vitesse de rotation en rad/s

    private final Vec2 START_POSITION = new VectCartesian(400,300);
    private final float START_ORIENTATION = 0;

    private boolean forwardOrBackward;     //Vrai si le robot avance ou recule, faux sinon
    private boolean turning;    //Vrai si le robot tourne, faux sinon

    private long lastUpdateTime;
    private final int MILLIS_BETWEEN_UPDATES=10;

    private boolean stoppedMovingMessageToSendFlag = false;
    private boolean previousMovingState = false;

    /** Constructeur */
    SimulatedRobot(){
        this.forwardOrBackward=false;
        this.lastUpdateTime=System.currentTimeMillis();
        this.turning=false;
        this.position = START_POSITION;
        this.positionTarget = START_POSITION;
        this.orientation = START_ORIENTATION;
        this.orientationTarget = START_ORIENTATION;
    }

    /** Fonction appelée pour tryUpdate la position du robot */
    void tryUpdate(){
        if (this.timeSinceLastUpdate() > this.MILLIS_BETWEEN_UPDATES) {
            updateOrientation();
            updatePosition();
            updateStopMovingMessage();
            this.lastUpdateTime = System.currentTimeMillis();
        }
    }

    /** Up un flag pour dire si on doit envoyer un message pour dire que le robot a fini son mouvement */
    private void updateStopMovingMessage() {
        if (previousMovingState){
            if (!this.isMoving()){
                stoppedMovingMessageToSendFlag = true;
            }
        }
        this.previousMovingState=this.isMoving();
    }

    public boolean mustSendStoppedMovingMessage(){
        if (this.stoppedMovingMessageToSendFlag){
            this.stoppedMovingMessageToSendFlag=false;
            return true;
        }
        else{
            return false;
        }
    }

    /** Update l'orientation pas à pas en fonction du delta entre l'orientation actuelle et l'orientation cible */
    private void updateOrientation(){
        if (Math.abs(this.orientationTarget - this.orientation) > this.ORIENTATION_TOLERANCE){
            if (Math.abs(this.orientationTarget -this.orientation) < this.ROTATION_SPEED * this.timeSinceLastUpdate()){
                this.orientation=this.orientationTarget;
                this.turning=true;
            }
            else {
                this.orientation+=((this.orientationTarget - this.orientation)/Math.abs(this.orientationTarget - this.orientation))*this.ROTATION_SPEED *this.timeSinceLastUpdate();
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
    void turn(float delta){
        this.orientationTarget=this.orientation+delta;
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

    /** Renvoie la position en X du robot */
    int getX(){ return this.position.getX(); }

    /** Renvoie la position en Y du robot */
    int getY(){ return this.position.getY(); }

    /** Renvoie l'orientation du robot */
    float getOrientation(){ return this.orientation; }

}
