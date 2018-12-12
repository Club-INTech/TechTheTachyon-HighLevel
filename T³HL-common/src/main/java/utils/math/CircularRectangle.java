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

package utils.math;

import java.util.ArrayList;

/**
 * Il s'agit d'une classe pour modéliser les obstacles rectangulaires en respectant notre modélisation du robot : On modélise le robot par un point et on grossit
 * tous les obstacles du rayon du robot
 *
 *               _____________
 *               |            |
 *               |R    (0)    |
 *          __R_0|____________|1__R___
 *         |     |    l       |      |
 *  *      |     |            |      |
 *  *      |  (3)|            |      |
 *  *      |   L |            | (1)  |
 *         |     |            |      |
 *         |     |            |      |
 *        _|R____|____________|__R___|
 *              3|            |2
 *              R|     (2)    |R
 *               |___________ |
 *
 * On relie les arcs de cercles
 *
 * @author yousra, rem
 */
public class CircularRectangle extends Shape {

    /**
     * Rayon des angles
     */
    private float radius;

    /**
     * Rectangle interieur
     */
    private Rectangle mainRectangle;

    /**
     * Arc de cercles repésentant les angles
     */
    private ArrayList<Circle> circleArcs;

    /**
     * Les 4 rectangles entourant le principal
     */
    private ArrayList<Rectangle> sideRectangles;

    /**
     * Construit un rectangle à angles arrondis
     * @param centre    le centre du rectangle
     * @param length    longueur (en x) du rectangle principal
     * @param width     largeur (en y) du rectangle principal
     * @param radius    rayon des angles
     */
    public CircularRectangle(Vec2 centre, float length, float width, float radius) {
        super(centre);
        this.mainRectangle = new Rectangle(centre, length, width);
        this.radius = radius;
        this.circleArcs = new ArrayList<>();
        this.sideRectangles = new ArrayList<>();

        // Pour la numérotation des arcs de cercles, voir le schéma ci-dessus
        for (int i = 0; i<4; i++){
            circleArcs.add(i, new Circle(this.mainRectangle.getPoints().get(i), this.radius, (1 - i)*Math.PI/2, Math.PI - i*Math.PI/2));
        }

        // Pour la numéroation des rectangles, voir schema ci-dessus
        sideRectangles.add(new Rectangle(mainRectangle.getCenter().plusVector(new VectCartesian(0, mainRectangle.getWidth()/2 + radius/2)),
                mainRectangle.getLength(), radius));
        sideRectangles.add(new Rectangle(mainRectangle.getCenter().plusVector(new VectCartesian(mainRectangle.getLength()/2 + radius/2, 0)),
                radius, mainRectangle.getWidth()));
        sideRectangles.add(new Rectangle(mainRectangle.getCenter().plusVector(new VectCartesian(0, -mainRectangle.getWidth()/2 - radius/2)),
                mainRectangle.getLength(), radius));
        sideRectangles.add(new Rectangle(mainRectangle.getCenter().plusVector(new VectCartesian(-mainRectangle.getLength()/2 - radius/2, 0)),
                radius, mainRectangle.getWidth()));
    }

    /**
     * @see Shape#intersect(Segment)
     */
    @Override
    public boolean intersect(Segment segment) {
        for (Rectangle rectangle : sideRectangles) {
            if (rectangle.intersect(segment)) {
                return true;
            }
        }
        for (Circle circle : circleArcs) {
            if (circle.intersect(segment)) {
                return true;
            }
        }
        if (mainRectangle.intersect(segment)) {
            return true;
        }
        return mainRectangle.isInShape(segment.getPointA()) && mainRectangle.isInShape(segment.getPointB());
    }

    /**
     * @see Shape#isInShape(Vec2)
     */
    @Override
    public boolean isInShape(Vec2 point) {
        for (Rectangle rectangle : sideRectangles) {
            if (rectangle.isInShape(point)) {
                return true;
            }
        }
        for (Circle circle : circleArcs) {
            if (circle.isInShape(point)) {
                return true;
            }
        }
        return mainRectangle.isInShape(point);
    }

    /**
     * @see Cloneable#clone()
     */
    @Override
    public Shape clone() throws CloneNotSupportedException {
        return new CircularRectangle(this.center.clone(), this.mainRectangle.getLength(), this.mainRectangle.getWidth(), this.radius);
    }

    /**
     * @see Object#equals(Object)
     */
    @Override
    public boolean equals(Object object) {
        if (object instanceof CircularRectangle) {
            return this.mainRectangle.equals(((CircularRectangle) object).mainRectangle) &&
                    this.circleArcs.equals(((CircularRectangle) object).circleArcs) &&
                    this.sideRectangles.equals(((CircularRectangle) object).sideRectangles);
        }
        return false;
    }

    /**
     * @see Object#hashCode()
     */
    @Override
    public int hashCode() {
        return this.mainRectangle.hashCode() +
                10*circleArcs.get(0).hashCode() +
                100*sideRectangles.get(0).hashCode();
    }

    /**
     * @see Object#toString()
     */
    @Override
    public String toString() {
        return "Circular Rectangle [mainRectangle :" + this.mainRectangle +
                ", circle arcs 0 : " + circleArcs.get(0) +
                ", side rectangle 1 & 2 : " + sideRectangles.get(0) + " & " + sideRectangles.get(1) + "]";
    }

    /**
     * Getters & Setters
     */
    public float getRadius() {
        return radius;
    }
    public Rectangle getMainRectangle() {
        return mainRectangle;
    }
    public ArrayList<Circle> getCircleArcs() {
        return circleArcs;
    }
    public ArrayList<Rectangle> getSideRectangles() {
        return sideRectangles;
    }
}
