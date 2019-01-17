import utils.math.Vec2;
import utils.math.VectCartesian;
import utils.math.VectPolar;

public class SimulatedRobot {

    private Vec2 position;          //Position actuelle du robot
    private float orientation;      //Orientation actuelle du robot

    private Vec2 positionTarget;        //Position cible du robot
    private float orientationTarget;    //Orientation cible du robot

    private final float POSITION_TOLERANCE = 0.01f;  //Tolérance sur la position
    private final float ORIENTATION_TOLERANCE = 0.01f;  //Tolérance sur l'orientation

    private final float TRANSLATION_SPEED = 0.5f;     //Vitesse de translation en m/s
    private final float ROTATION_SPEED = 2;           //Vitesse de rotation en rad/s

    private final Vec2 START_POSITION = new VectCartesian(400,300);
    private final float START_ORIENTATION = 0;

    private boolean forwardOrBackward;     //Vrai si le robot avance ou recule, faux sinon
    private boolean turning;    //Vrai si le robot tourne, faux sinon

    private long lastUpdateTime;
    private final int MILLIS_BETWEEN_UPDATES=10;

    //Constructeur
    SimulatedRobot(){
        this.forwardOrBackward=false;
        this.lastUpdateTime=System.currentTimeMillis();
        this.turning=false;
        this.position = START_POSITION;
        this.positionTarget = START_POSITION;
        this.orientation = START_ORIENTATION;
        this.orientationTarget = START_ORIENTATION;
    }

    //On update la position et l'orientation
    void update(){
        if (this.deltaTime() > this.MILLIS_BETWEEN_UPDATES) {
            updateOrientation();
            updatePosition();
            System.out.println(this.getX());
            System.out.println(this.deltaTime());
            this.lastUpdateTime = System.currentTimeMillis();
        }
    }

    private void updateOrientation(){
        if (Math.abs(this.orientationTarget -this.orientation) > this.ORIENTATION_TOLERANCE){
            if (Math.abs(this.orientationTarget -this.orientation) < this.ROTATION_SPEED * this.deltaTime()){
                this.orientation=this.orientationTarget;
                this.turning=true;
            }
            else {
                this.orientation+=((this.orientationTarget - this.orientation)/Math.abs(this.orientationTarget - this.orientation))*this.ROTATION_SPEED *this.deltaTime();
                this.turning=true;
            }
        }
        else{
            this.orientation=this.orientationTarget;
            this.turning=false;
        }
    }

    private void updatePosition(){
        if (!this.turning) {
            if (this.positionTarget.distanceTo(this.position) > this.POSITION_TOLERANCE) {
                if (this.positionTarget.distanceTo(this.position) < this.TRANSLATION_SPEED * this.deltaTime()) {
                    this.position = this.positionTarget;
                    this.forwardOrBackward = true;
                } else {
                    this.position.plus(this.positionTarget.minusVector(this.position).homothetie(this.TRANSLATION_SPEED * this.deltaTime() / (float) this.positionTarget.minusVector(this.position).getR()));
                    this.forwardOrBackward = true;
                }
            } else {
                this.position = this.positionTarget;
                this.forwardOrBackward = false;
            }
        }
    }

    private long deltaTime(){
        return (System.currentTimeMillis() - this.lastUpdateTime);
    }

    boolean isMoving(){
        return this.turning && this.forwardOrBackward;
    }

    void moveLengthwise(int delta){
        Vec2 orientationVector = new VectPolar(1,this.orientation);
        this.positionTarget=this.position.plusVector(orientationVector.homothetie((float)delta));
    }

    void turn(float delta){
        this.orientationTarget=this.orientation+delta;
    }

    void stop(){
        this.positionTarget=this.position;
        this.orientationTarget=this.orientation;
    }

    void goTo(Vec2 position){
        this.positionTarget = position;
        this.orientationTarget = (float)this.positionTarget.minusVector(position).getA();
    }

    int getX() { return this.position.getX(); }

    int getY(){
        return this.position.getY();
    }

}
