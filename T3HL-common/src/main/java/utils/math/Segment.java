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

import java.awt.geom.Line2D;

/**
 * Cette classe construit la structure segment : un segment est constitué de deux points
 *
 * @author yousra
 */
public class Segment implements Cloneable {

    /**
     * Premier point du segment
     */
    private Vec2 pointA;

    /**
     * Deuxième point du segment
     */
    private Vec2 pointB;

    /**
     * Longueur du segment
     */
    private double length;

    /**
     * Constructeur
     * @param pointA pointA
     * @param pointB pointB
     */
    public Segment(Vec2 pointA, Vec2 pointB) {
        this.pointA = pointA;
        this.pointB = pointB;
        this.length = computeLength();
    }

    /**
     * @param segment segment
     * @return  true si intersection des segments
     */
    public boolean intersect(Segment segment){
        double xA1=this.getPointA().getX();
        double yA1=this.getPointA().getY();
        double xB1=this.getPointB().getX();
        double yB1=this.getPointB().getY();
        double xA2=segment.getPointA().getX();
        double yA2=segment.getPointA().getY();
        double xB2=segment.getPointB().getX();
        double yB2=segment.getPointB().getY();
        return Line2D.linesIntersect(xA1,yA1,xB1,yB1,xA2,yA2,xB2,yB2);
    }

    /**
     * @param segment
     *              un autre segment
     * @return  le point d'intersection des deux segments
     * TODO : implémenter le test
     */
    public Vec2 intersectionPoint(Segment segment) {
        if (!this.intersect(segment)) {
            return null;
        }
        Vec2 p1A = segment.pointA;
        Vec2 p1B = segment.pointB;
        Vec2 p2A = this.pointA;
        Vec2 p2B = this.pointB;

        double a1, a2, b1, b2;

        if (p1A.getX() == p1B.getX()) {
            return computeIntersection(p1A, p2A, p2B);
        } else if (p2A.getX() == p2B.getX()) {
            return computeIntersection(p2A, p1A, p1B);
        }

        a1 = (p1A.getY() - p1B.getY()) / (double) (p1A.getX() - p1B.getX());
        a2 = (p2A.getY() - p2B.getY()) / (double) (p2A.getX() - p2B.getX());
        b1 = p1B.getY() - a1*p1B.getX();
        b2 = p2B.getY() - a2*p2B.getX();

        float d = (float) (a2 - a1);

        return new InternalVectCartesian((float) ((b1 - b2) / d), (float) ((a2* b1 - a1* b2) / d));
    }

    private Vec2 computeIntersection(Vec2 p1A, Vec2 p2A, Vec2 p2B) {
        double a2;
        double b2;
        a2 = (p2A.getY() - p2B.getY()) / (double) (p2A.getX() - p2B.getX());
        b2 = p2B.getY() - a2* p2B.getX();
        return new InternalVectCartesian(p1A.getX(), (float)(a2* p1A.getX() + b2));
    }

    /**
     * Cette méthode détermine la distance entre une droite et un point
     * @param point point
     * @return  la distance entre le point et la droite qui porte le segment
     */
    public double distanceTo(Vec2 point){
        if(pointA.getX()==pointB.getX()){
            return Math.abs(point.getX() - pointA.getX() );
        }
        else{
            double a=(pointB.getY() - pointA.getY())/ (double)(pointB.getX() - pointA.getX());
            double b= pointB.getY() - a*pointB.getX();
            return Math.abs(point.getY() - a*point.getX() - b)/Math.sqrt(1 + a*a);
        }
    }

    /**
     * Cette méthode retourne la longueur d'un segment
     */
    private double computeLength(){
        double xB=pointB.getX();
        double yB=pointB.getY();
        double xA=pointA.getX();
        double yA=pointA.getY();
        return Math.sqrt((xB - xA)*(xB - xA) + (yB - yA)*(yB - yA));
    }

    /**
     * Cette méthode détermine le vecteur directeur d'une droite
     */
    public Vec2 vectorDirector(){
        return pointA.minusVector(pointB);
    }

    /**
     * @see Cloneable#clone()
     */
    @Override
    public Segment clone() throws CloneNotSupportedException {
        return new Segment(this.pointA.clone(),this.pointB.clone());
    }

    /**
     * @see Object#hashCode()
     */
    @Override
    public int hashCode() {
        return this.getPointA().hashCode() + getPointB().hashCode();
    }

    /**
     * @see Object#equals(Object)
     */
    @Override
    public boolean equals(Object o) {
        if (o instanceof Segment) {
            return (pointA.equals(((Segment) o).pointA) && pointB.equals(((Segment) o).pointB)) ||
                    (pointA.equals(((Segment) o).pointB)) && pointB.equals(((Segment) o).pointA);
        }
        return false;
    }

    /**
     * @see Object#toString()
     */
    @Override
    public String toString() {
        return String.format("[%s:%s]", this.pointA.toString(), this.pointB.toString());
    }

    /**
     * Getters & Setters
     */
    public Vec2 getPointA() {
        return pointA;
    }
    public Vec2 getPointB() {
        return pointB;
    }
    public void setPointA(Vec2 pointA) {
        this.pointA = pointA;
        this.length = computeLength();
    }
    public void setPointB(Vec2 pointB) {
        this.pointB = pointB;
        this.length = computeLength();
    }
    public double getLength() {
        return length;
    }
}
