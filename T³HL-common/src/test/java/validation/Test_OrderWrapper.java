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
import orders.OrderWrapper;
import orders.hooks.HookNames;
import orders.order.*;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import pfg.config.Config;
import utils.Container;
import utils.math.VectCartesian;

import java.util.Locale;
import java.util.Optional;

/**
 * Classe permettant de tester l'orderWrapper
 */
public class Test_OrderWrapper {

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

    @Before
    public void setUp() throws Exception{
        container = Container.getInstance("Master");
        config = container.getConfig();
        orderWrapper=container.getService(OrderWrapper.class);
        connectionsManager=container.getService(ConnectionManager.class);
        connectionsManager.initConnections(Connection.LOCALHOST_SERVER);
        Thread.sleep(50);
        connectionsManager.initConnections(Connection.LOCALHOST_CLIENT);
        Thread.sleep(50);
        orderWrapper.setConnection(Connection.LOCALHOST_CLIENT);
    }

    /**
     * Test de immobilise
     * @throws Exception
     */
    @Test
    public void stopOrderTest()throws Exception{
        orderWrapper.immobilise();
        Thread.sleep(20);
        m=Connection.LOCALHOST_SERVER.read();
        Assert.assertTrue(m.isPresent());
        String a = MotionOrder.STOP.getOrderStr();
        Assert.assertEquals(a,m.get());
    }

    /**
     * Test d'envoi des ordres
     * @throws Exception
     */
    @Test
    public void sendActionOrder() throws Exception{
        orderWrapper.useActuator(ActuatorsOrder.DESACTIVE_LA_POMPE);
        Thread.sleep(20);
        m=Connection.LOCALHOST_SERVER.read();
        Assert.assertTrue(m.isPresent());
        String a = ActuatorsOrder.DESACTIVE_LA_POMPE.getOrderStr();
        Assert.assertEquals(a,m.get());
    }

    /**
     * Test du moveLenghtwise
     * @throws Exception
     */
    @Test
    public void moveLenghtwiseOrder() throws  Exception {
        orderWrapper.moveLenghtwise(15);
        Thread.sleep(20);
        m=Connection.LOCALHOST_SERVER.read();
        Assert.assertTrue(m.isPresent());
        String a = String.format(Locale.US, "%s %d", MotionOrder.MOVE_LENTGHWISE.getOrderStr(), 15);
        Assert.assertEquals(a, m.get());
    }

    /**
     * Test du turn
     * @throws Exception
     */
    @Test
    public void turnTest() throws Exception {
        orderWrapper.turn(Math.PI);
        Thread.sleep(20);
        m=Connection.LOCALHOST_SERVER.read();
        Assert.assertTrue(m.isPresent());
        String a = String.format(Locale.US, "%s %.3f", MotionOrder.TURN.getOrderStr(), Math.PI);
        Assert.assertEquals(a,m.get());
    }

    /**
     * test pour set les translations et les rotations
     * @throws Exception
     */
    @Test
    public void setTranslationnalSpeedTest() throws Exception {
        orderWrapper.setTranslationnalSpeed(2);
        Thread.sleep(20);
        m = Connection.LOCALHOST_SERVER.read();
        Assert.assertTrue(m.isPresent());
        String a = String.format(Locale.US, "%s %.3f", SpeedOrder.SET_TRANSLATION_SPEED.getOrderStr(), 2.0);
        Assert.assertEquals(a,m.get());
    }

    /**
     * Test pour set la vitesse de rotation
     * @throws Exception
     */
    @Test
    public void setRotationalSpeedTest() throws Exception {
        orderWrapper.setRotationnalSpeed(3);
        Thread.sleep(20);
        m = Connection.LOCALHOST_SERVER.read();
        Assert.assertTrue(m.isPresent());
        String a = String.format(Locale.US, "%s %.3f", SpeedOrder.SET_ROTATIONNAL_SPEED.getOrderStr(), 3.0);
        Assert.assertEquals(a,m.get());
    }

    /**
     * Test pour set la position et l'orientation
     * @throws Exception
     */
    @Test
    public void setPositionAndOrientationTest() throws Exception {
        orderWrapper.setPositionAndOrientation(new VectCartesian(2, 3), Math.PI / 2);
        Thread.sleep(20);
        m = Connection.LOCALHOST_SERVER.read();
        Assert.assertTrue(m.isPresent());
        String a = String.format(Locale.US, "%s %d %d %.3f", PositionAndOrientationOrder.SET_POSITION_AND_ORIENTATION.getOrderStr(), 2, 3, Math.PI/2);
        Assert.assertEquals(a,m.get());
    }

    /**
     * test pour set l'orientation
     * @throws Exception
     */
    @Test
    public void setOrientationTest() throws Exception {
        orderWrapper.setOrientation(Math.PI);
        Thread.sleep(20);
        m=Connection.LOCALHOST_SERVER.read();
        Assert.assertTrue(m.isPresent());
        String a = String.format(Locale.US, "%s %.3f", PositionAndOrientationOrder.SET_ORIENTATION.getOrderStr(), Math.PI);
        Assert.assertEquals(a, m.get());
    }

    /**
     * test pour configurer les hooks
     * @throws Exception
     */
    @Test
    public void configureHookTest() throws Exception {
        orderWrapper.configureHook(0, new VectCartesian(2, 3), 2, Math.PI, 2, ActuatorsOrder.FERME_PORTE_AVANT);
        Thread.sleep(20);
        m=Connection.LOCALHOST_SERVER.read();
        Assert.assertTrue(m.isPresent());
        String a = String.format(Locale.US, "%s %d %d %d %d %.3f %.3f %s", HooksOrder.INITIALISE_HOOK.getOrderStr(), 0, 2, 3, 2, Math.PI, 2.0, ActuatorsOrder.FERME_PORTE_AVANT.getOrderStr());
        Assert.assertEquals(a, m.get());
    }

    /**
     *Test pour activer les hooks
     * @throws Exception
     */
    @Test
    public void enableHookTest() throws Exception {
        orderWrapper.enableHook(HookNames.SPEED_DOWN);
        Thread.sleep(20);
        m = Connection.LOCALHOST_SERVER.read();
        Assert.assertTrue(m.isPresent());
        String a = String.format(Locale.US, "%s %d", HooksOrder.ENABLE_HOOK.getOrderStr(),  HookNames.SPEED_DOWN.getId());
        Assert.assertEquals(a, m.get());
    }

    /**
     * Test pour désactiver les hooks
     * @throws Exception
     */
    @Test
    public void disableHookTest() throws Exception {
        orderWrapper.disableHook(HookNames.SPEED_DOWN);
        Thread.sleep(20);
        m = Connection.LOCALHOST_SERVER.read();
        Assert.assertTrue(m.isPresent());
        String a = String.format(Locale.US, "%s %d", HooksOrder.DISABLE_HOOK.getOrderStr(),  HookNames.SPEED_DOWN.getId());
        Assert.assertEquals(a,m.get());
    }

    @Test
    public void moveToPoint() throws Exception {
        orderWrapper.moveToPoint(new VectCartesian(54, 647));
        Thread.sleep(20);
        m = Connection.LOCALHOST_SERVER.read();
        Assert.assertTrue(m.isPresent());
        Assert.assertEquals("p 54 647", m.get());
    }

    /**
     * après chaque test, on ferme les connexions et réinitialise le m
     */
    @After
    public void closeConnection() throws Exception {
        container = null;
        config = null;
        orderWrapper = null;
        connectionsManager.closeInitiatedConnections();
        connectionsManager = null;
        Container.resetInstance();
    }
}

