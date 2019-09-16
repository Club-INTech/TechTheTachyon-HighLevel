package utils.math;

import utils.ConfigData;

/**
 * Version d'InternalVectCartesian avec un repère changé (0,0 est dans le coin de départ du robot)
 *
 * @author jglrxavpok
 */
public class VectCartesian extends InternalVectCartesian {

    public VectCartesian(int x, int y) {
        super(x-TABLE_WIDTH/2, y);
    }

    public VectCartesian(float x, float y) {
        super(x-TABLE_WIDTH/2, y);
    }

    public VectCartesian(double x, double y) {
        super(x-TABLE_WIDTH/2, y);
    }
}
