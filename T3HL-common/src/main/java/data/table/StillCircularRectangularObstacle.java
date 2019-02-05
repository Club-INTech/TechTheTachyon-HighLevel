package data.table;

import utils.math.CircularRectangle;

/**
 * Classe représentant les obstacles circulaires
 *
 * @author gwenser
 */
public class StillCircularRectangularObstacle extends Obstacle {

    /**
     * Constructeur rectangle arrondi
     * @param   rectangleArrondi  rectangle arrondi
     */
    public StillCircularRectangularObstacle(CircularRectangle rectangleArrondi) {
        super(rectangleArrondi);
    }

    /**
     * @see Obstacle#clone()
     */
    @Override
    public Obstacle clone() throws CloneNotSupportedException {
        return new StillCircularRectangularObstacle((CircularRectangle) this.shape.clone());
    }

    /**
     * @see Obstacle#toString()
     */
    @Override
    public String toString() {
        return "Obstacle fixe rectangulaireArrondi " + shape.toString();
    }
}
