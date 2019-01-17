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

    private final Vec2 START_POSITION = new VectCartesian(10,10);
    private final float START_ORIENTATION = 0;

    private boolean moving;     //Vrai si le robot avance, recule ou tourne, faux sinon
    private boolean turning;    //Vrai si le robot tourne, faux sinon


    private long lastUpdateTime;

    //Constructeur
    SimulatedRobot(){
        this.moving=false;
        this.position = START_POSITION;
        this.orientation = START_ORIENTATION;
    }

    //On update la position et l'orientation
    void update(){
        this.lastUpdateTime=System.currentTimeMillis();
        updateOrientation();
        updatePosition();
    }

    private void updateOrientation(){
        if (Math.abs(this.orientationTarget -this.orientation) > this.ORIENTATION_TOLERANCE){
            if (Math.abs(this.orientationTarget -this.orientation) < this.ROTATION_SPEED * this.time()){
                this.orientation=this.orientationTarget;
                this.moving=true;
                this.turning=true;
            }
            else {
                this.orientation+=((this.orientationTarget - this.orientation)/Math.abs(this.orientationTarget - this.orientation))*this.ROTATION_SPEED *this.time();
                this.moving=true;
                this.turning=true;
            }
        }
        else{
            this.orientation=this.orientationTarget;
            this.moving=false;
            this.turning=false;
        }
    }

    private void updatePosition(){
        if (!this.turning) {
            if (this.positionTarget.distanceTo(this.position) > this.POSITION_TOLERANCE) {
                if (this.positionTarget.distanceTo(this.position) < this.TRANSLATION_SPEED * this.time()) {
                    this.position = this.positionTarget;
                    this.moving = true;
                } else {
                    this.position = this.positionTarget.minusVector(this.position).homothetie(this.TRANSLATION_SPEED * this.time() / (float) this.positionTarget.minusVector(this.position).getR());
                    this.moving = true;
                }
            } else {
                this.position = this.positionTarget;
                this.moving = false;
            }
        }
    }

    private long time(){
        return (System.currentTimeMillis() - this.lastUpdateTime);
    }

    boolean isMoving(){
        return this.moving;
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

    int getX(){
        return this.position.getX();
    }

    int getY(){
        return this.position.getY();
    }





}
