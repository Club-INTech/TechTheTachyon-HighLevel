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

import connection.Connection;
import data.controlers.Listener;
import org.junit.*;
import utils.HLInstance;

public class Test_Listener {

    private HLInstance hl;

    private Listener listener;

    @Ignore
    @Before
    public void setUp() throws Exception {
        hl = HLInstance.getInstance("Master");
        listener = hl.module(Listener.class);
    }

    @Ignore
    @After
    public void tearDown() {
        listener = null;
        hl = null;
        HLInstance.resetInstance();
    }

    @Ignore
    @Test
    public void testInitialisation() throws Exception {
        hl.startInstanciedThreads();
        Thread.sleep(5000);
        // TODO : lancer le processus de gestion du Lidar !

        Assert.assertTrue(Connection.LIDAR_DATA.isInitiated());
        Assert.assertTrue(Connection.TEENSY_MASTER.isInitiated());
        Assert.assertTrue(Connection.SLAVE.isInitiated());
    }
}
