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

import connection.ConnectionManager;
import data.Sick;
import data.XYO;
import data.controlers.Listener;
import data.controlers.SensorControler;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import utils.math.Vec2;
import utils.math.VectCartesian;

public class Test_SensorControler {

    private SensorControler sensorControler;

    private Listener listener;

    private ConnectionManager connectionManager;
    // TODO

    @Before
    public void setUp() throws Exception{
        //Les constructeurs de connecionManager et listener ont été mis en public pour pouvoir faire les tests
        connectionManager=new ConnectionManager();
        listener=new Listener(connectionManager);
        sensorControler=new SensorControler(listener);
    }
    @Test
    public void run() throws Exception {

        int dsick = 173;
        int xtheo = 0;
        int ytheo = 0;
        int esick = 173;
        double rapport= esick/dsick;
        double teta = Math.atan(rapport);
        int xCalcule = (int) ((300- xtheo) * Math.cos(teta));
        int yCalcule = (int) ((300- ytheo) * Math.cos(teta));

        VectCartesian newPosition = new VectCartesian(xCalcule,yCalcule);
        double newOrientation = teta;
        XYO newXYO = new XYO(newPosition, newOrientation);
        //Assert.assertEquals(new VectCartesian(300,300 ), newPosition);
        //Assert.assertEquals(0, newOrientation,0.1);
        System.out.println(teta);

    }
}
