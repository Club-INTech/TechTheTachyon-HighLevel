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

package validation;

import connection.Connection;
import connection.ConnectionManager;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import pfg.config.Config;
import orders.OrderWrapper;
import orders.order.ActuatorsOrder;
import orders.order.HooksOrder;
import orders.order.MotionOrder;
import orders.order.PositionAndOrientationOrder;
import utils.ConfigData;
import utils.Container;
import utils.math.VectCartesian;

import java.util.Locale;
import java.util.Optional;

public class Test_OrderWrapper_Symetry {

    /**Container*/
    private Container container;
    /**orderWrapper*/
    private OrderWrapper orderWrapper;
    /**connectionManager pour établir et fermer les connexions en local pour run les tests*/
    private ConnectionManager connectionsManager;
    /**String ajouté pour les asserts : voir tests*/
    private Optional<String> m;
    /**config*/
    private Config config;
    /**
     * Etablir ce dont on a besoin pour faire les tests
     */
    @Before
    public void setUp()throws Exception{
        container = Container.getInstance("Master");
        config = container.getConfig();
        config.override(ConfigData.COULEUR,"jaune");
        orderWrapper=container.getService(OrderWrapper.class);
        connectionsManager=container.getService(ConnectionManager.class);
        connectionsManager.initConnections(Connection.LOCALHOST_SERVER);
        Thread.sleep(50);
        connectionsManager.initConnections(Connection.LOCALHOST_CLIENT);
        Thread.sleep(50);
        orderWrapper.setConnection(Connection.LOCALHOST_CLIENT);
    }

    /**
     * Test pour tester le turn avec la symétrie
     * @throws Exception
     */
    @Test
    public void turnTestWithSymetry() throws Exception {
        orderWrapper.turn(Math.PI / 3);
        Thread.sleep(10);
        m=Connection.LOCALHOST_SERVER.read();
        Assert.assertTrue(m.isPresent());
        String a = String.format(Locale.US, "%s %.3f", MotionOrder.TURN.getOrderStr(), 2 * Math.PI / 3);
        Assert.assertEquals(a, m.get());

    }

    /**
     * test pour set la position et l'orientation avec la symétrie
     */
    @Test
    public void setPositionAndOrientationTestWithSymetry() throws Exception {
        orderWrapper.setPositionAndOrientation(new VectCartesian(2, 3), Math.PI / 3);
        Thread.sleep(10);
        m=Connection.LOCALHOST_SERVER.read();
        Assert.assertTrue(m.isPresent());
        String a = String.format(Locale.US, "%s %d %d %.3f",
                PositionAndOrientationOrder.SET_POSITION_AND_ORIENTATION.getOrderStr(), -2, 3, 2* Math.PI/3);
        Assert.assertEquals(a, m.get());

    }

    /**
     * Test pour configurer les hooks en symétrie
     */
    @Test
    public void configureHookTestWithSymetry() throws Exception {
        orderWrapper.configureHook(0, new VectCartesian(2, 3), 2, Math.PI / 3, 2, ActuatorsOrder.FERME_PORTE_DROITE);
        Thread.sleep(10);
        m=Connection.LOCALHOST_SERVER.read();
        Assert.assertTrue(m.isPresent());
        String a = String.format(Locale.US, "%s %d %d %d %d %.3f %.3f %s",
                HooksOrder.INITIALISE_HOOK.getOrderStr(), 0, -2, 3, 2, 2* Math.PI/3, 2.0, ActuatorsOrder.FERME_PORTE_GAUCHE.getOrderStr());
        Assert.assertEquals(a, m.get());

    }

    @Test
    public void moveToPointSymetry() throws Exception {
        orderWrapper.moveToPoint(new VectCartesian(158, 587));
        Thread.sleep(20);
        m = Connection.LOCALHOST_SERVER.read();
        Assert.assertTrue(m.isPresent());
        Assert.assertEquals("p -158 587", m.get());
    }

    /**
     * après chaque test, on ferme les connexions, on réinitialise le m
     * et on remet la symetrie à false
     */
    @After
    public void closeConnection() throws Exception{
        container = null;
        config.override(ConfigData.COULEUR,"violet");
        config = null;
        orderWrapper = null;
        connectionsManager.closeInitiatedConnections();
        connectionsManager = null;
        Container.resetInstance();
    }
}
