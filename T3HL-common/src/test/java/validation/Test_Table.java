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

import data.Graphe;
import data.Table;
import data.XYO;
import data.graphe.Ridge;
import data.table.StillCircularObstacle;
import data.table.StillRectangularObstacle;
import data.table.MobileCircularObstacle;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import utils.ConfigData;
import utils.Container;
import utils.container.ContainerException;
import utils.math.*;

import java.util.ArrayList;

public class Test_Table {
    /**
     * La table à tester
     */
    private Table table;
    private Container container;

    @Before
    public void setUp() throws ContainerException {
        container = Container.getInstance("Master");
        table = container.getService(Table.class);
        container.getService(Graphe.class);
    }

    @After
    public void tearDown() {
        table = null;
        container = null;
        Container.resetInstance();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddFixedObstacle1() {
        table.addFixedObstacle(new MobileCircularObstacle(new VectCartesian(0, 50), 200));
    }

    @Test
    public void testAddFixedObstacle2() throws Exception {
        table.addFixedObstacle(new StillCircularObstacle(new VectCartesian(0, 100), 200));
        Assert.assertEquals(1, table.getFixedObstacles().size());
    }

    @Test
    public void testRemoveFixedObstacle() throws Exception {
        table.addFixedObstacle(new StillCircularObstacle(new VectCartesian(100, 0), 200));
        table.removeFixedObstacle(new StillCircularObstacle(new VectCartesian(100, 0), 200));

        Assert.assertEquals(0, table.getFixedObstacles().size());
    }

    @Test
    public void testIsPositionInFixedObstacle() throws Exception {
        table.addFixedObstacle(new StillCircularObstacle(new VectCartesian(300, 200), 120));
        table.addFixedObstacle(new StillRectangularObstacle(new VectCartesian(600, 500), 400, 200));
        Vec2 p1 = new VectCartesian(350, 240);
        Vec2 p2 = new VectCartesian(678, 599);
        Vec2 p3 = new VectCartesian(-420, 200);

        Assert.assertTrue(table.isPositionInFixedObstacle(p1));
        Assert.assertTrue(table.isPositionInFixedObstacle(p2));
        Assert.assertFalse(table.isPositionInFixedObstacle(p3));
    }

    @Test
    public void testIsPositionInMobileObstacle() throws Exception {
        Vec2 p1 = new VectCartesian(100, 500);
        Vec2 p2 = new VectCartesian(500, 800);
        Vec2 po = p1.plusVector(new VectPolar(0.5* (int) ConfigData.BUDDY_RAY.getDefaultValue(), Math.PI/4));
        ArrayList<Vec2> points = new ArrayList<>();

        XYO.getBuddyInstance();
        points.add(po);
        table.updateMobileObstacles(points);

        Assert.assertTrue(table.isPositionInMobileObstacle(p1));
        Assert.assertFalse(table.isPositionInMobileObstacle(p2));
    }

    @Test
    public void testIntersectAnyFixedObstacle() throws Exception {
        table.addFixedObstacle(new StillRectangularObstacle(new VectCartesian(200, 300), 300, 200));
        table.addFixedObstacle(new StillCircularObstacle(new VectCartesian(100, 100), 200));
        Segment s1 = new Segment(new VectCartesian(0, 0), new VectCartesian(600, 400));
        Segment s2 = new Segment(new VectCartesian(0, 0), new VectCartesian(-21, 100));
        Segment s3 = new Segment(new VectCartesian(320, 250), new VectCartesian(500, 400));
        Segment s4 = new Segment(new VectCartesian(-100, -200), new VectCartesian(300, -150));

        Assert.assertTrue(table.intersectAnyFixedObstacle(s1));
        Assert.assertTrue(table.intersectAnyFixedObstacle(s2));
        Assert.assertTrue(table.intersectAnyFixedObstacle(s3));
        Assert.assertFalse(table.intersectAnyFixedObstacle(s4));
    }

    @Test
    public void testIntersectAnyMobileObstacle_Segment() throws Exception {
        Vec2 po = new VectCartesian(540, 620);
        Segment s1 = new Segment(new VectCartesian(500, 600), new VectCartesian(700, 900));
        Segment s2 = new Segment(new VectCartesian(300, 400), new VectCartesian(1200, 320));
        ArrayList<Vec2> points = new ArrayList<>();

        XYO.getBuddyInstance();
        points.add(po);
        table.updateMobileObstacles(points);

        Assert.assertTrue(table.intersectAnyMobileObstacle(s1));
        Assert.assertFalse(table.intersectAnyMobileObstacle(s2));
    }

    @Test
    public void testIntersectAnyMobileObstacle_Circle() throws Exception {
        Vec2 po = new VectCartesian(540, 620);
        Circle c1 = new Circle(new VectCartesian(480, 500), 50);
        Circle c2 = new Circle(new VectCartesian(1000, 600), 200);
        ArrayList<Vec2> points = new ArrayList<>();

        XYO.getBuddyInstance();
        points.add(po);
        table.updateMobileObstacles(points);

        Assert.assertTrue(table.intersectAnyMobileObstacle(c1));
        Assert.assertFalse(table.intersectAnyMobileObstacle(c2));
    }

    @Test
    public void testUpdateMobileObstaclesTableOnly() throws Exception {
        XYO.getBuddyInstance();
        ArrayList<Vec2> points = new ArrayList<>();
        points.add(new VectCartesian(300, 100));
        points.add(new VectCartesian(800, 150));
        points.add(new VectCartesian(20, 10));

        int ennemyRay = container.getConfig().getInt(ConfigData.ENNEMY_RAY);
        int buddyRay = container.getConfig().getInt(ConfigData.BUDDY_RAY);

        table.updateMobileObstacles(points);

        Assert.assertEquals(3, table.getMobileObstacles().size());
        Assert.assertEquals(ennemyRay, ((Circle) table.getMobileObstacles().get(0).getShape()).getRadius(), 0.1);
        Assert.assertEquals(ennemyRay, ((Circle) table.getMobileObstacles().get(1).getShape()).getRadius(), 0.1);
        Assert.assertEquals(buddyRay, ((Circle) table.getMobileObstacles().get(2).getShape()).getRadius(), 0.1);

        Thread.sleep(MobileCircularObstacle.getDefaultLifeTime() + 1);

        table.updateMobileObstacles(new ArrayList<>());

        Assert.assertEquals(0, table.getMobileObstacles().size());
    }

    @Test
    public void testUpdateMobileObstaclesFull() throws Exception {
        Graphe graphe = container.getService(Graphe.class);
        XYO.getBuddyInstance();
        ArrayList<Vec2> points = new ArrayList<>();
        points.add(new VectCartesian(300, 100));
        points.add(new VectCartesian(800, 150));
        points.add(new VectCartesian(20, 10));

        ArrayList<MobileCircularObstacle> obstacles = new ArrayList<>();
        obstacles.add(new MobileCircularObstacle(points.get(0), (int) ConfigData.ENNEMY_RAY.getDefaultValue()));
        obstacles.add(new MobileCircularObstacle(points.get(1), (int) ConfigData.ENNEMY_RAY.getDefaultValue()));
        obstacles.add(new MobileCircularObstacle(points.get(2), (int) ConfigData.BUDDY_RAY.getDefaultValue()));
        boolean intersect;

        table.updateMobileObstacles(points);

        Assert.assertEquals(obstacles, table.getMobileObstacles());
        for (Ridge ridge : graphe.getRidges()) {
            intersect = false;
            for (MobileCircularObstacle obstacle : obstacles) {
                if (obstacle.intersect(ridge.getSeg())) {
                    intersect = true;
                }
            }
            Assert.assertEquals(!intersect, ridge.isReachable());
        }

        Thread.sleep(MobileCircularObstacle.getDefaultLifeTime() + 1);

        table.updateMobileObstacles(new ArrayList<>());

        Assert.assertEquals(new ArrayList<>(), table.getMobileObstacles());
        for (Ridge ridge : graphe.getRidges()) {
            Assert.assertTrue(ridge.isReachable());
        }
    }
}
