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
import data.XYO;
import data.controlers.DataController;
import data.controlers.Listener;
import data.synchronization.SynchronizationWithBuddy;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import utils.HLInstance;
import utils.math.InternalVectCartesian;

public class Test_DataController {

    private DataController dataController;

    private Listener listener;

    private ConnectionManager connectionManager;
    // TODO

    @Before
    public void setUp() throws Exception{
        //Les constructeurs de connecionManager et listener ont été mis en public pour pouvoir faire les tests
        connectionManager=new ConnectionManager();
        HLInstance hl = HLInstance.getInstance("Master");
        listener=new Listener(hl, connectionManager, new SynchronizationWithBuddy(hl));
        dataController =new DataController(hl, listener, null, null);
    }

    @Test
    public void run() throws Exception {

        int[] sickMeasurements = new int[6];
        int[] significantSicks = new int[3];
        sickMeasurements[0]=0 ;
        sickMeasurements[1]=300 ;
        sickMeasurements[2]=300 ;
        sickMeasurements[3]=300 ;
        sickMeasurements[4]=0 ;
        significantSicks[0]=3 ;
        significantSicks[1]=1 ;
        significantSicks[2]=2 ;
        int dsick = 173;
        int esick = sickMeasurements[significantSicks[1]] - sickMeasurements[significantSicks[2]];
        double rapport = (double) esick / dsick;
        int xCalcule;
        int yCalcule;
        double teta;
            double orien= 0;

            teta = Math.atan(rapport);
            xCalcule = (int) (1500 - (sickMeasurements[significantSicks[0]]) * Math.cos(teta));
            if (-Math.PI/2 < orien && orien < Math.PI/2) { //modifier car arctan est toujours inférieur à PI
                if (significantSicks[1] == 4 || significantSicks[1] == 5) {
                    yCalcule = (int) (2000 - (sickMeasurements[significantSicks[2]]) * Math.cos(teta));
                } else {
                    yCalcule = (int) ((sickMeasurements[significantSicks[2]]) * Math.cos(teta));
                }
            } else {
                if (significantSicks[1] == 4 || significantSicks[1] == 5) {
                    yCalcule = (int) ((sickMeasurements[significantSicks[2]]) * Math.cos(teta));
                } else {
                    yCalcule = (int) (2000 - ((sickMeasurements[significantSicks[2]])) * Math.cos(teta));
                }

            }

        InternalVectCartesian newPosition = new InternalVectCartesian(xCalcule, yCalcule);
        Double newOrientation = teta + Math.PI;
        XYO newXYO = new XYO(newPosition, newOrientation);
        InternalVectCartesian vecttest = new InternalVectCartesian(1200 , 300);
        Double orientest = 0.0 + Math.PI;
        Assert.assertEquals(vecttest,newPosition);
        Assert.assertEquals(orientest,newOrientation);
    }

    }

