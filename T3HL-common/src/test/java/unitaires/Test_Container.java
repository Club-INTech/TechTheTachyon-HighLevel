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
import utils.Container;
import utils.container.ContainerException;

public class Test_Container
{
    /**
     * L'instance à tester
     */
    private Container container;

    @Before
    public void setUp()
    {
        container = Container.getInstance("Master");
        Assert.assertNotNull(container.getConfig());
    }

    @After
    public void tearDown()
    {
        container = null;
        Container.resetInstance();
    }

    @Test(expected = ContainerException.class)
    public void testCircularDependencies() throws Exception
    {
        container.getService(A.class);
    }

    @Test(expected = ContainerException.class)
    public void testMultipleConstructors() throws Exception
    {
        container.getService(C.class);
    }

    @Test
    public void testSimple() throws Exception
    {
        D d = container.getService(D.class);
        Assert.assertTrue(container.getInstanciedServices().containsKey(E.class.getSimpleName()));
        Assert.assertTrue(container.getInstanciedServices().containsKey(D.class.getSimpleName()));
        Assert.assertTrue(((E)container.getInstanciedServices().get(E.class.getSimpleName())).isConfig());
        Assert.assertTrue(((D)container.getInstanciedServices().get(D.class.getSimpleName())).isConfig());
    }
}
