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

package unitaires;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import utils.HLInstance;
import utils.Log;
import utils.communication.SocketClientInterface;
import utils.communication.SocketServerInterface;

import java.util.Optional;

public class Test_Communication {

    public HLInstance hl;

    public SocketServerInterface interface1;

    public SocketClientInterface interface2;

    @Before
    public void setUp() throws Exception {
        hl = HLInstance.getInstance("Master");
        Log.init();
        interface1 = new SocketServerInterface("localhost", 10200, true);
        interface1.init();
    }

    @After
    public void tearDown() throws Exception {
        interface1.close();
        interface2.close();
        interface1 = null;
        interface2 = null;
        hl = null;
        HLInstance.resetInstance();
        System.gc();
    }

    @Test
    public void testBidirectionnal() throws Exception {
        interface2 = new SocketClientInterface("localhost", 10200, true);
        interface2.init();

        while (!interface1.isInitiated() || !interface2.isInitiated()) {
            Thread.sleep(50);
        }

        interface1.send("M1");
        interface1.send("M2");
        interface2.send("R1");
        interface1.send("M3");

        Thread.sleep(100);
        Optional<String> m1 = interface2.read();
        Optional<String> m2 = interface2.read();
        Optional<String> m3 = interface2.read();
        Optional<String> m4 = interface2.read();

        Optional<String> r1 = interface1.read();
        Optional<String> r2 = interface1.read();

        Assert.assertTrue(m1.isPresent());
        Assert.assertTrue(m2.isPresent());
        Assert.assertTrue(m3.isPresent());
        Assert.assertFalse(m4.isPresent());
        Assert.assertTrue(r1.isPresent());
        Assert.assertFalse(r2.isPresent());

        Assert.assertEquals("M1", m1.get());
        Assert.assertEquals("M2", m2.get());
        Assert.assertEquals("M3", m3.get());
        Assert.assertEquals("R1", r1.get());
    }
}
