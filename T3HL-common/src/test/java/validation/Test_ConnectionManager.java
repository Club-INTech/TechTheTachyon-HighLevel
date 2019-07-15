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
import utils.HLInstance;

public class Test_ConnectionManager {

    private ConnectionManager connectionManager;

    private HLInstance hl;

    @Before
    public void setUp() throws Exception {
        hl = HLInstance.getInstance("Master");
        connectionManager = hl.module(ConnectionManager.class);
    }

    @After
    public void tearDown() throws Exception {
        connectionManager = null;
        hl = null;
        HLInstance.resetInstance();
    }

    @Test
    public void testInitConnections() throws Exception {
        connectionManager.initConnections(Connection.LOCALHOST_SERVER);
        Thread.sleep(100);
        connectionManager.initConnections(Connection.LOCALHOST_CLIENT);
        Thread.sleep(100);

        Assert.assertTrue(Connection.LOCALHOST_SERVER.isInitiated());
        Assert.assertTrue(Connection.LOCALHOST_CLIENT.isInitiated());
        Assert.assertTrue(connectionManager.getInitiatedConnections().contains(Connection.LOCALHOST_SERVER));
        Assert.assertTrue(connectionManager.getInitiatedConnections().contains(Connection.LOCALHOST_CLIENT));
    }

    @Test
    public void testCloseConnections() throws Exception {
        connectionManager.initConnections(Connection.LOCALHOST_SERVER);
        Thread.sleep(20);
        connectionManager.initConnections(Connection.LOCALHOST_CLIENT);
        Thread.sleep(20);

        connectionManager.closeInitiatedConnections();

        Assert.assertTrue(connectionManager.getInitiatedConnections().isEmpty());
    }
}
