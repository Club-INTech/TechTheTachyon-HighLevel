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
import unitaires.container.A;
import unitaires.container.C;
import unitaires.container.D;
import unitaires.container.E;
import utils.HLInstance;
import utils.container.ContainerException;

public class Test_HLInstance
{
    /**
     * L'instance à tester
     */
    private HLInstance hl;

    @Before
    public void setUp()
    {
        hl = HLInstance.getInstance("Master");
        Assert.assertNotNull(hl.getConfig());
    }

    @After
    public void tearDown()
    {
        hl = null;
        HLInstance.resetInstance();
    }

    @Test(expected = ContainerException.class)
    public void testCircularDependencies() throws Exception
    {
        hl.module(A.class);
    }

    @Test(expected = ContainerException.class)
    public void testMultipleConstructors() throws Exception
    {
        hl.module(C.class);
    }

    @Test
    public void testSimple() throws Exception
    {
        D d = hl.module(D.class);
        Assert.assertTrue(hl.getInstanciedServices().containsKey(E.class.getSimpleName()));
        Assert.assertTrue(hl.getInstanciedServices().containsKey(D.class.getSimpleName()));
        Assert.assertTrue(((E) hl.getInstanciedServices().get(E.class.getSimpleName())).isConfig());
        Assert.assertTrue(((D) hl.getInstanciedServices().get(D.class.getSimpleName())).isConfig());
    }
}
