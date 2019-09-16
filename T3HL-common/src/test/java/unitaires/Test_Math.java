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

import org.junit.Assert;
import org.junit.Test;
import utils.math.*;

public class Test_Math {

    @Test
    public void vec2Init() {
        Vec2 vectCartesian = new InternalVectCartesian(50, 50);
        Vec2 vectPolar = new VectPolar(100, -Math.PI/4);

        Assert.assertEquals(Math.sqrt(5000), vectCartesian.getR(), 0.1);
        Assert.assertEquals(Math.PI/4, vectCartesian.getA(), 0.001);
        Assert.assertEquals(Math.round(Math.sqrt(5000)), vectPolar.getX());
        Assert.assertEquals(-Math.round(Math.sqrt(5000)), vectPolar.getY());
    }

    @Test
    public void vec2PlusMinus() {
        Vec2 vec1 = new InternalVectCartesian(50, 60);
        Vec2 vec2 = new InternalVectCartesian(12, -32);

        Assert.assertEquals(new InternalVectCartesian(62, 28), vec1.plusVector(vec2));
        Assert.assertEquals(new InternalVectCartesian(38, 92), vec1.minusVector(vec2));

        vec1.plus(vec2);
        vec2.minus(vec1);

        Assert.assertEquals(new InternalVectCartesian(62, 28), vec1);
        Assert.assertEquals(new InternalVectCartesian(-50, -60), vec2);
    }

    @Test
    public void vec2DotCross() {
        Vec2 vec1 = new InternalVectCartesian(20, 20);
        Vec2 vec2 = new InternalVectCartesian(0, 50);

        Assert.assertEquals((Math.sqrt(2)/2)*Math.sqrt(800)*50, vec1.dot(vec2), 1);
        Assert.assertEquals((Math.sqrt(2)/2)*Math.sqrt(800)*50, vec2.dot(vec1), 1);
        Assert.assertEquals((Math.sqrt(2)/2)*Math.sqrt(800)*50, vec1.crossProduct(vec2), 1);
        Assert.assertEquals(-(Math.sqrt(2)/2)*Math.sqrt(800)*50, vec2.crossProduct(vec1), 1);

        vec1 = new InternalVectCartesian(23, 0);
        Assert.assertEquals(0, vec1.dot(vec2));
        Assert.assertEquals(0, vec2.dot(vec1));
        Assert.assertEquals(23*50, vec1.crossProduct(vec2), 1);
        Assert.assertEquals(-23*50, vec2.crossProduct(vec1), 1);

        vec1 = new InternalVectCartesian(0, 68);
        Assert.assertEquals(68*50, vec1.dot(vec2));
        Assert.assertEquals(68*50, vec2.dot(vec1));
        Assert.assertEquals(0, vec1.crossProduct(vec2));
        Assert.assertEquals(0, vec2.crossProduct(vec1));
    }

    @Test
    public void vec2DistanceTo() {
        Vec2 vec1 = new InternalVectCartesian(120, 100);
        Vec2 vec2 = new InternalVectCartesian(-58, -69);

        Assert.assertEquals(Math.sqrt(178*178 + 169*169), vec1.distanceTo(vec2), 0.1);
        Assert.assertEquals(Math.sqrt(178*178 + 169*169), vec2.distanceTo(vec1), 0.1);
    }

    @Test
    public void vec2Symetrize() {
        Vec2 vec1 = new InternalVectCartesian(157, -56);

        Assert.assertEquals(new InternalVectCartesian(-157, -56), vec1.symetrizeVector());
        Assert.assertEquals(new InternalVectCartesian(157, -56), vec1);

        vec1.symetrize();
        Assert.assertEquals(new InternalVectCartesian(-157, -56), vec1);
    }

    @Test
    public void vec2Equals() {
        Vec2 vec1 = new InternalVectCartesian(67, 67);
        Vec2 vec2 = new VectPolar(Math.sqrt(67*67*2), Math.PI/4);
        Vec2 vec3 = new InternalVectCartesian(-58, -1248);

        Assert.assertTrue(vec1.equals(vec2));
        Assert.assertFalse(vec1.equals(vec3));
        Assert.assertFalse(vec2.equals(vec3));
    }

    @Test
    public void segmentIntersect() {
        Segment segment1 = new Segment(new InternalVectCartesian(40, 8), new InternalVectCartesian(57, 247));
        Segment segment2 = new Segment(new InternalVectCartesian(-2, 0), new InternalVectCartesian(140,152));
        Assert.assertTrue(segment1.intersect(segment2));
    }

    @Test
    public void segmentDistanceTo() {
        Segment segment = new Segment(new InternalVectCartesian(-157, 42), new InternalVectCartesian(68, 87));
        Vec2 vec = new InternalVectCartesian(58, -12);
        Assert.assertEquals(95, segment.distanceTo(vec), 0.5);
    }

    @Test
    public void segmentVecteurDirecteur() {
        Segment segment = new Segment(new InternalVectCartesian(12, 58), new InternalVectCartesian(98, -47));
        Assert.assertEquals(new InternalVectCartesian(-86, 105), segment.vectorDirector());
    }

    @Test
    public void segmentIntersectionPoint() {
        Segment segment = new Segment(new InternalVectCartesian(0, 10), new InternalVectCartesian(40, 90));
        Segment seg = new Segment(new InternalVectCartesian(15, 0), new InternalVectCartesian(0, 100));
        Segment seg1 = new Segment(new InternalVectCartesian(20, -10), new InternalVectCartesian(20, 100));

        Assert.assertEquals(new InternalVectCartesian(10, 31), segment.intersectionPoint(seg));
        Assert.assertEquals(segment.intersectionPoint(seg), seg.intersectionPoint(segment));
        Assert.assertEquals(new InternalVectCartesian(20, 50), seg1.intersectionPoint(segment));
        Assert.assertEquals(segment.intersectionPoint(seg1), seg1.intersectionPoint(segment));
        Assert.assertNull(seg1.intersectionPoint(seg));
        Assert.assertNull(seg.intersectionPoint(seg1));
    }

    @Test
    public void segmentEquals() {
        Segment segment = new Segment(new InternalVectCartesian(89, -457), new InternalVectCartesian(-42, 584));
        Segment segment1 = new Segment(new InternalVectCartesian(-42, 584), new InternalVectCartesian(89, -457));
        Segment segment2 = new Segment(new InternalVectCartesian(-42, 547), new InternalVectCartesian(98, 512));

        Assert.assertTrue(segment.equals(segment1));
        Assert.assertFalse(segment.equals(segment2));
        Assert.assertFalse(segment1.equals(segment2));
    }

    @Test
    public void circleIntersect() {
        Circle circle = new Circle(new InternalVectCartesian(2,5),10);
        Segment segment = new Segment(new InternalVectCartesian(5,10), new InternalVectCartesian(4,5));
        Assert.assertTrue(circle.intersect(segment));
    }

    @Test
    public void circleIsInShape() {
        Circle circle = new Circle(new InternalVectCartesian(58, -14),14);
        Vec2 vec1 = new InternalVectCartesian(54, -8);
        Vec2 vec2 = new InternalVectCartesian(58, 0);
        Vec2 vec3 = new InternalVectCartesian(52, 145);

        Assert.assertTrue(circle.isInShape(vec1));
        Assert.assertTrue(circle.isInShape(vec2));
        Assert.assertFalse(circle.isInShape(vec3));
    }

    @Test
    public void circleIntersectsWithCircle() {
        Circle circle = new Circle(new InternalVectCartesian(0,0),5);
        Circle circle2 = new Circle(new InternalVectCartesian(0,0),5);
        Assert.assertTrue(circle2.intersectsWithCircle(circle));
    }

    @Test
    public void circleClosestPointToShape() {
        Circle circle = new Circle(new InternalVectCartesian(50, 20), 50);
        Vec2 vec1 = new InternalVectCartesian(150, 120);
        Vec2 vec2 = new InternalVectCartesian(55, 25);

        Assert.assertEquals(new InternalVectCartesian((float) (50 + (Math.sqrt(2)/2) * 50), (float) (20 + (Math.sqrt(2)/2) * 50)), circle.closestPointToShape(vec1));
        Assert.assertEquals(new InternalVectCartesian((float) (50 + (Math.sqrt(2)/2) * 50), (float) (20 + (Math.sqrt(2)/2) * 50)), circle.closestPointToShape(vec2));

        circle = new Circle(new InternalVectCartesian(0, 50), 80, -Math.PI/4, Math.PI/2);
        vec1 = new InternalVectCartesian(-421, 96);
        vec2 = new InternalVectCartesian(-94, -214);

        Assert.assertEquals(new InternalVectCartesian(0, 130), circle.closestPointToShape(vec1));
        Assert.assertEquals(new InternalVectCartesian((float) ((Math.sqrt(2)/2) * 80), 50 - (float) ((Math.sqrt(2)/2) * 80)), circle.closestPointToShape(vec2));
    }

    @Test
    public void circleEquals() {
        Circle circle = new Circle(new InternalVectCartesian(58, -58), 64);
        Circle circle1 = new Circle(new VectPolar(Math.sqrt(58 * 58 * 2), -Math.PI/4), 64);
        Circle circle2 = new Circle(new VectPolar(Math.sqrt(58 * 58 * 2), -Math.PI/4), 24);

        Assert.assertTrue(circle.equals(circle1));
        Assert.assertFalse(circle.equals(circle2));
        Assert.assertFalse(circle1.equals(circle2));
    }

    @Test
    public void rectangleIntersect() {
        Rectangle rectangle = new Rectangle(new InternalVectCartesian(0,0),50,30);
        Segment segment = new Segment(new InternalVectCartesian(11,0), new InternalVectCartesian(72,91));
        Segment segment1 = new Segment(new InternalVectCartesian(21, 16), new InternalVectCartesian(58, 97));

        Assert.assertTrue(rectangle.intersect(segment));
        Assert.assertFalse(rectangle.intersect(segment1));
    }

    @Test
    public void rectangleIsInShape() {
        Rectangle rectangle = new Rectangle(new InternalVectCartesian(20, 80), 40, 60);
        Vec2 vec1 = new InternalVectCartesian(32, 68);
        Vec2 vec2 = new InternalVectCartesian(28, 124);

        Assert.assertTrue(rectangle.isInShape(vec1));
        Assert.assertFalse(rectangle.isInShape(vec2));
    }

    @Test
    public void rectangleGetDiagonalsAndPoints() {
        Rectangle rectangle = new Rectangle(new InternalVectCartesian(40, 60), 60, 30);

        Assert.assertEquals(new Segment(new InternalVectCartesian(10, 75), new InternalVectCartesian(70, 45)), rectangle.getDiagonals().get(0));
        Assert.assertEquals(new Segment(new InternalVectCartesian(70, 75), new InternalVectCartesian(10, 45)), rectangle.getDiagonals().get(1));
        Assert.assertEquals(new InternalVectCartesian(10, 75), rectangle.getPoints().get(0));
        Assert.assertEquals(new InternalVectCartesian(70, 75), rectangle.getPoints().get(1));
        Assert.assertEquals(new InternalVectCartesian(70, 45), rectangle.getPoints().get(2));
        Assert.assertEquals(new InternalVectCartesian(10, 45), rectangle.getPoints().get(3));
    }

    @Test
    public void rectangleEquals() {
        Rectangle rectangle = new Rectangle(new InternalVectCartesian(92, 57), 54, 69);
        Rectangle rectangle1 = new Rectangle(new InternalVectCartesian(92, 57), 54, 69);
        Rectangle rectangle2 = new Rectangle(new InternalVectCartesian(92, 57), 52, 71);

        Assert.assertTrue(rectangle.equals(rectangle1));
        Assert.assertFalse(rectangle.equals(rectangle2));
        Assert.assertFalse(rectangle1.equals(rectangle2));
    }

    @Test
    public void circularRectangleInit() {
        CircularRectangle rectangle = new CircularRectangle(new InternalVectCartesian(80, 30), 40, 20, 10);

        Assert.assertEquals(new Circle(new InternalVectCartesian(60, 40), 10, Math.PI/2, Math.PI), rectangle.getCircleArcs().get(0));
        Assert.assertEquals(new Circle(new InternalVectCartesian(100, 40), 10, 0, Math.PI/2), rectangle.getCircleArcs().get(1));
        Assert.assertEquals(new Circle(new InternalVectCartesian(100, 20), 10, -Math.PI/2, 0), rectangle.getCircleArcs().get(2));
        Assert.assertEquals(new Circle(new InternalVectCartesian(60, 20), 10, -Math.PI, -Math.PI/2), rectangle.getCircleArcs().get(3));
        Assert.assertEquals(new Rectangle(new InternalVectCartesian(80, 45), 40, 10), rectangle.getSideRectangles().get(0));
        Assert.assertEquals(new Rectangle(new InternalVectCartesian(105, 30), 10, 20), rectangle.getSideRectangles().get(1));
        Assert.assertEquals(new Rectangle(new InternalVectCartesian(80, 15), 40, 10), rectangle.getSideRectangles().get(2));
        Assert.assertEquals(new Rectangle(new InternalVectCartesian(55, 30), 10, 20), rectangle.getSideRectangles().get(3));
    }

    @Test
    public void circularRectangleIsInShape() {
        CircularRectangle rectangle = new CircularRectangle(new InternalVectCartesian(-50, 50), 80, 40, 10);
        Vec2 vec1 = new InternalVectCartesian(-1, 79);
        Vec2 vec2 = new InternalVectCartesian(-50, 68);

        Assert.assertFalse(rectangle.isInShape(vec1));
        Assert.assertTrue(rectangle.isInShape(vec2));
    }

    @Test
    public void circularRectangleIntersect() {
        CircularRectangle rectangle = new CircularRectangle(new InternalVectCartesian(50, -50), 60, 80, 10);
        Segment segment1 = new Segment(new InternalVectCartesian(-32, -10), new InternalVectCartesian(60, 5));
        Segment segment2 = new Segment(new InternalVectCartesian(-40, -65), new InternalVectCartesian(20, 64));

        Assert.assertTrue(rectangle.intersect(segment1));
        Assert.assertFalse(rectangle.intersect(segment2));
    }

    @Test
    public void circularRectangleEquals() {
        CircularRectangle rectangle = new CircularRectangle(new InternalVectCartesian(50, -50), 60, 80, 20);
        CircularRectangle rectangle1 = new CircularRectangle(new InternalVectCartesian(50, -50), 60, 80, 20);
        CircularRectangle rectangle2 = new CircularRectangle(new InternalVectCartesian(50, -50), 60, 80, 25);
        CircularRectangle rectangle3 = new CircularRectangle(new InternalVectCartesian(50, -50), 60, 100, 20);

        Assert.assertTrue(rectangle.equals(rectangle1));
        Assert.assertFalse(rectangle.equals(rectangle2));
        Assert.assertFalse(rectangle.equals(rectangle3));
        Assert.assertFalse(rectangle2.equals(rectangle3));
    }

    @Test
    public void circularRectangleClosest() {
        float radius = 10;
        CircularRectangle rectangle = new CircularRectangle(new InternalVectCartesian(0, 0), 80, 40, radius);

        Assert.assertEquals(new InternalVectCartesian(0, -20 - radius), rectangle.closestPointToShape(new InternalVectCartesian(0, -500))); // au nord
        Assert.assertEquals(new InternalVectCartesian(0, 20 + radius), rectangle.closestPointToShape(new InternalVectCartesian(0, 500))); // au sud
        Assert.assertEquals(new InternalVectCartesian(40 + radius, 0), rectangle.closestPointToShape(new InternalVectCartesian(500, 0))); // à l'est
        Assert.assertEquals(new InternalVectCartesian(-40 - radius, 0), rectangle.closestPointToShape(new InternalVectCartesian(-500, 0))); // à l'ouest

        // coins
        // coin sud-ouest
        Assert.assertEquals(
                new InternalVectCartesian((float) (-40 + radius * Math.cos(3*Math.PI/4)), (float) (-20 - radius * Math.sin(3*Math.PI/4))),
                rectangle.closestPointToShape(new InternalVectCartesian(-500, -500)));

        // coin sud-est
        Assert.assertEquals(
                new InternalVectCartesian((float) (40 + radius * Math.cos(Math.PI/4)), (float) (-20 - radius * Math.sin(Math.PI/4))),
                rectangle.closestPointToShape(new InternalVectCartesian(500, -500))
        );

        // coin nord-ouest
        Assert.assertEquals(
                new InternalVectCartesian((float) (-40 + radius * Math.cos(5*Math.PI/4)), (float) (20 - radius * Math.sin(5*Math.PI/4))),
                rectangle.closestPointToShape(new InternalVectCartesian(-500, 500))
        );

        // coin nord-est
        Assert.assertEquals(
                new InternalVectCartesian((float) (40 + radius * Math.cos(7*Math.PI/4)), (float) (20 - radius * Math.sin(7*Math.PI/4))),
                rectangle.closestPointToShape(new InternalVectCartesian(500, 500))
        );

        // TODO: test qd point dans le rectangle
    }
}
