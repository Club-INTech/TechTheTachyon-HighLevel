/**
 * Copyright (c) 2018, INTech.
 * this file is part of INTech's HighLevel.
 *
 * INTech's HighLevel is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * INTech's HighLevel is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with it.  If not, see <http://www.gnu.org/licenses/>.
 **/
//CHECKSTYLE OFF

package embedded;

import org.junit.Test;
import utils.math.Vec2;
import utils.math.VectCartesian;

public class Test_SensorControler {
    // TODO

    @Test
    public VectCartesian newPosition(int sick1, int sick2, int sick3) {

        int dsick = 180;
        int xtheo = 300;
        int ytheo = 300;
        int esick = sick1 - sick2;
        double rapport= esick/dsick;
        double teta = rapport- Math.pow(rapport, 3) / 3 + Math.pow(rapport,5)/5;
        int x = (int) ((sick1 - xtheo) * Math.cos(teta));
        int y = (int) ((sick3 - ytheo) * Math.cos(teta));
        VectCartesian pos = new VectCartesian(x,y);
        return pos;
    }


    @Test
    public double newOrientation(int sick1, int sick2) {

        int dsick = 180;
        int esick = sick1 - sick2;
        double rapport= esick/dsick;
        double teta = rapport- Math.pow(rapport, 3) / 3 + Math.pow(rapport,5)/5;

        return teta;
    }





}
