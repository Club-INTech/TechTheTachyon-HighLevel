package data.table;

import utils.math.CircularRectangle;

public class StillCircularRectangularObstacle extends Obstacle{

    public StillCircularRectangularObstacle(CircularRectangle rectangleArrondi) {
        super(rectangleArrondi);
    }

    @Override
    public Obstacle clone() throws CloneNotSupportedException {
        return new StillCircularRectangularObstacle((CircularRectangle) this.shape.clone());
    }

    @Override
    public String toString() {
        return "Obstacle " + shape.toString();
    }
}
