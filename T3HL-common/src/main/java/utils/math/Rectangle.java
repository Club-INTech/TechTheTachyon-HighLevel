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
 * Définit un rectangle
 *
 *           A______0______B
 *           |             |
 *           |             |
 *          3|             |
 *           |             |1
 *   y       |             |
 *   ^       |             |
 *   |       |_____________|
 *   |       D      2       C
 *   |--> x
 * @see Shape
 *
 * @author yousra
 */
public class Rectangle extends Shape {

    /**
     * Longueur du rectangle (en x)
     */
    private float length;

    /**
     * Largeur du rectangle (en y)
     */
    private float width;

    /**
     * Segments délimitant le rectangle
     */
    private ArrayList<Segment> segments;

    /**
     * Construit un rectangle
     * @param centre    centre du rectangle
     * @param length    longueur (x)
     * @param width     largeur (y)
     */
    public Rectangle(Vec2 centre, float length, float width) {
        super(centre);
        this.length = length;
        this.width = width;

        Vec2 hg = center.plusVector(new InternalVectCartesian(-length/2, width/2));
        Vec2 hd = center.plusVector(new InternalVectCartesian(length/2, width/2));
        Vec2 bg = center.plusVector(new InternalVectCartesian(-length/2, -width/2));
        Vec2 bd = center.plusVector(new InternalVectCartesian(length/2, -width/2));
        this.segments = new ArrayList<>();

        segments.add(new Segment(hg, hd));
        segments.add(new Segment(hd, bd));
        segments.add(new Segment(bd, bg));
        segments.add(new Segment(bg, hg));
    }

    /**
     * Cette méthode retourne vrai s'il y'a intersection entre le segment et le rectangle:
     * Il y'a intersection entre un segment et un rectangle s'il y'a intersection entre ce segment
     * et l'un des quatre segments du rectangle ou si le segment est dans le rectangle
     * @see Shape
     */
    @Override
    public boolean intersect(Segment segment) {
        for (Segment seg : segments) {
            if (seg.intersect(segment)) {
                return true;
            }
        }
        return isInShape(segment.getPointA()) && isInShape(segment.getPointB());
    }

    /**
     * Cette méthode retourne true si notre rectangle contient un point
     * @param point point
     * @return      true si le rectangle contient un point
     */
    @Override
    public boolean isInShape(Vec2 point){
        return Math.abs(point.getX() - center.getX()) < this.length/2 &&
                Math.abs(point.getY() - center.getY()) < this.width/2;
    }

    /**
     * @see Shape#closestPointToShape(Vec2)
     */
    @Override
    public Vec2 closestPointToShape(Vec2 point) {
        Segment seg = new Segment(point, this.center);

        for (Segment segment : this.segments) {
            if (segment.intersect(seg)) {
                return segment.intersectionPoint(seg);
            }
        }
        // TODO Traiter le cas ou le point est dans le rectangle
        return null;
    }

    /**
     * Cette méthode retourne les diagonales d'un rectangle
     * @return  les diagonales du rectangle
     */
    public ArrayList<Segment> getDiagonals(){
        ArrayList<Segment> diagonals = new ArrayList<>();
        diagonals.add(new Segment(segments.get(0).getPointA(), segments.get(1).getPointB()));
        diagonals.add(new Segment(segments.get(3).getPointA(), segments.get(0).getPointB()));
        return diagonals;
    }

    /**
     * Cette méthode retourne les segments d'un rectangle
     *       (0) A______________B (1)
     *           |             |
     *           |             |
     *           |             |
     *           |             |
     *           |             |
     *           |             |
     *           |_____________|
     *        (3) D              C (2)
     * @return A B C D
     */
    public ArrayList<Vec2> getPoints(){
        ArrayList<Vec2> points = new ArrayList<>();
        points.add(segments.get(0).getPointA());
        points.add(segments.get(1).getPointA());
        points.add(segments.get(2).getPointA());
        points.add(segments.get(3).getPointA());
        return points;
    }

    /**
     * On vérifie si le rectangle a le même centre, la même largeur et la même longueur
     * @see Object#equals(Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Rectangle){
            return ((Rectangle) obj).getCenter().equals(this.getCenter()) && ((Rectangle) obj).getLength()==this.getLength() && ((Rectangle) obj).getWidth()==this.getWidth();
        }
        return false;
    }

    /**
     * @see Cloneable
     */
    @Override
    public Shape clone() throws CloneNotSupportedException {
        return new Rectangle(this.center.clone(), this.length, this.width);
    }

    /**
     * @see Object#hashCode()
     */
    @Override
    public int hashCode() {
        return (int) (this.center.hashCode() + this.length + this.width*1000);
    }

    /**
     * @see Object#toString()
     */
    @Override
    public String toString() {
        return "rectangle [center : " + center.toString() + ", length (x) : " + this.length + ", width (y) : " + this.width + "]";
    }

    /**
     * Getters
     */
    public float getLength() {
        return length;
    }
    public float getWidth() {
        return width;
    }
}
