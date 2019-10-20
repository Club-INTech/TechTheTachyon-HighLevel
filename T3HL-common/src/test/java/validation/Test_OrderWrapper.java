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
import utils.HLInstance;
import utils.math.InternalVectCartesian;

import java.util.Optional;

/**
 * Classe permettant de tester l'orderWrapper
 */
public class Test_OrderWrapper {

    /**Container*/
    private HLInstance hl;

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
        hl = HLInstance.getInstance("Master");
        config = hl.getConfig();
        orderWrapper= hl.module(OrderWrapper.class);
        connectionsManager= hl.module(ConnectionManager.class);
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
        String a = MotionOrders.Stop.toLL();
        Assert.assertEquals(a,m.get());
    }

    /**
     * Test d'envoi des ordres
     * @throws Exception
     */
    @Test
    public void sendActionOrder() throws Exception{
        orderWrapper.perform(ActuatorsOrders.ActivateRightValve);
        Thread.sleep(20);
        m=Connection.LOCALHOST_SERVER.read();
        Assert.assertTrue(m.isPresent());
        String a = ActuatorsOrders.ActivateRightValve.toLL();
        Assert.assertEquals(a,m.get());
    }

    /**
     * Test du moveLenghtwise
     * @throws Exception
     */
    @Test
    public void moveLenghtwiseOrder() throws  Exception {
        orderWrapper.moveLenghtwise(15,false);
        Thread.sleep(20);
        m=Connection.LOCALHOST_SERVER.read();
        Assert.assertTrue(m.isPresent());
        String a = MotionOrders.MoveLengthwise.with(15, false);
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
        String a = MotionOrders.Turn.with(Math.PI);
        Assert.assertEquals(a, m.get());
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
        String a = SpeedOrders.SetTranslationSpeed.with(2.0);
        Assert.assertEquals(a, m.get());
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
        String a = SpeedOrders.SetRotationalSpeed.with(3.0);
        Assert.assertEquals(a,m.get());
    }

    /**
     * Test pour set la position et l'orientation
     * @throws Exception
     */
    @Test
    public void setPositionAndOrientationTest() throws Exception {
        orderWrapper.setPositionAndOrientation(new InternalVectCartesian(2, 3), Math.PI / 2, false);
        Thread.sleep(20);
        m = Connection.LOCALHOST_SERVER.read();
        Assert.assertTrue(m.isPresent());
        String a = PositionAndOrientationOrders.SetPositionAndOrientation.with(2, 3, Math.PI/2);
        Assert.assertEquals(a, m.get());
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
        String a = PositionAndOrientationOrders.SetOrientation.with(Math.PI);
        Assert.assertEquals(a, m.get());
    }

    /**
     * test pour configurer les hooks
     * @throws Exception
     */
    @Test
    public void configureHookTest() throws Exception {
        orderWrapper.configureHook(0, new InternalVectCartesian(2, 3), 2, Math.PI, 2, ActuatorsOrders.ActivateRightValve);
        Thread.sleep(20);
        m=Connection.LOCALHOST_SERVER.read();
        Assert.assertTrue(m.isPresent());
        String a = HookOrders.InitialiseHook.with(0, 2, 3, 2, Math.PI, 2.0, ActuatorsOrders.ActivateRightValve.toLL());
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
        String a = HookOrders.EnableHook.with(HookNames.SPEED_DOWN.getId());
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
        String a = HookOrders.DisableHook.with(HookNames.SPEED_DOWN.getId());
        Assert.assertEquals(a,m.get());
    }

    @Test
    public void moveToPoint() throws Exception {
        orderWrapper.moveToPoint(new InternalVectCartesian(54, 647));
        Thread.sleep(20);
        m = Connection.LOCALHOST_SERVER.read();
        Assert.assertTrue(m.isPresent());
        Assert.assertEquals("goto 54 647", m.get());
    }

    /**
     * après chaque test, on ferme les connexions et réinitialise le m
     */
    @After
    public void closeConnection() throws Exception {
        hl = null;
        config = null;
        orderWrapper = null;
        connectionsManager.closeInitiatedConnections();
        connectionsManager = null;
        HLInstance.resetInstance();
    }
}

