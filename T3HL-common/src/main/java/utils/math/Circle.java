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
import java.util.ArrayList;
import java.util.Locale;

/**
 * @see Shape
 *
 * @author yousra
 */
public class Circle extends Shape {

    /**
     * rayon du cercle
     */
    private float radius;

    /**
     * angle où l'arc de cercle commence
     */
    private double angleStart;

    /**
     * angle où l'arc de cercle se termine
     */
    private double angleEnd;

    /**
     * Constructeur d'un cercle
     * @see Shape
     * @param centre centre
     * @param radius rayon du cercle
     */
    public Circle(Vec2 centre, float radius) {
        super(centre);
        this.radius = radius;
        this.angleStart = -Math.PI;
        this.angleEnd = Math.PI - 0.00001;
    }

    /**
     * Constructeur d'un arc de cercle
     * @param centre centre du cercle
     * @param radius rayon du cercle
     * @param angleStart angle de début
     * @param angleEnd angle de fin
     */
    public Circle(Vec2 centre, float radius, double angleStart, double angleEnd) {
        super(centre);
        this.radius = radius;
        this.angleStart = Calculs.modulo(angleStart, Math.PI);
        this.angleEnd = Calculs.modulo(angleEnd, Math.PI);
    }

    /**
     * Cette méthode retourne true s'il y'a intersection centre le cercle et le segment
     * Elle se base sur une méthode de la librairie Line2D, le principe est le suivant :
     * Si on note (D) la droite qui porte le segment
     * (C) le cercle
     * On veut savoir si (D) et (C) sont en intersection
     * On note (P) la droite perpendiculaire à (D) qui passe par le centre de (C)
     * On peut donc connaitre les coordonnées du point de l'intersection entre (P) et (D) qu'on note I vu qu'on dispose de deux équations à deux inconnues
     * (D) et (C) sont en intersection ssi la distance entre I et le centre est inférieure à R le rayon du cercle
     * @param segment segment
     */
    @Override
    public boolean intersect(Segment segment) {
        return Line2D.ptSegDistSq(
                segment.getPointA().getX(), segment.getPointA().getY(),
                segment.getPointB().getX(), segment.getPointB().getY(),
                this.getCenter().getX(), this.getCenter().getY()) <= this.getRadius()*this.getRadius();
    }

    /**
     * @see Shape#isInShape(Vec2)
     * @param   point point
     */
    @Override
    public boolean isInShape(Vec2 point) {
        return point.distanceTo(this.center) <= this.radius;
    }

    /**
     * @see Shape#closestPointToShape(Vec2)
     * Cette méthode prend en paramètre un point et retourne le point du cercle le plus proche de ce point
     * Le point le plus proche du cercle est le point appartenant au cercle et à la droite passante par le centre
     * du cercle et le point en paramètre
     * @param point point
     * @return  le point du cercle (ou de l'arc) le plus proche du point donné en paramètre
     */
    @Override
    public Vec2 closestPointToShape(Vec2 point){
        Vec2 vec = point.minusVector(this.getCenter());

        // Si la direction donnée par le vecteur point qui est hors du cercle intersecte l'arc de cercle, on a le point avec les coordonnées polaires
        if (vec.getA() >= this.getAngleStart() && vec.getA() <= this.getAngleEnd()) {
            vec.setR(this.getRadius());
            return this.getCenter().plusVector(vec);
        }

        // Sinon, on doit choisir entre le point du début de l'arc de cercle et celui de fin
        else {
            Vec2 circleCenterStart = new VectPolar(this.getRadius(), this.getAngleStart());
            Vec2 circleCenterEnd = new VectPolar(this.getRadius(), this.getAngleEnd());

            if (this.getCenter().plusVector(circleCenterStart).distanceTo(point) >= this.getCenter().plusVector(circleCenterEnd).distanceTo(point)){
                return circleCenterEnd.plusVector(this.getCenter());
            } else {
                return circleCenterStart.plusVector(this.getCenter());
            }
        }

    }

    /**
     * Cette méthode retourne true si deux cercles sont sécants, c'est-à-dire si la distance entre les deux centres est entre la valeur absolue de la différence des deux
     * rayons et la valeur absolue de la somme (valeur absolue ici pour la somme par sécurité, on sait jamais...)
     * @param circle cercle
     * @return  true si les cercles s'intersectent
     */
    public boolean intersectsWithCircle(Circle circle){
        float r1 = this.radius;
        float r2 = circle.getRadius();
        //distance entre les deux centres
        double d = this.getCenter().distanceTo(circle.getCenter());
        return d >= Math.abs(r2-r1) && d <= Math.abs(r2+r1);
    }

    /**
     * Cette méthode retourne une liste de points autour d'un cercle, utile pour la construction de noeuds d'un graph autour d'obstacles circulaires ;)
     * @param n : nombre de points qu'on veut autour du cercle
     * @return  points autour du cercle
     */
    public ArrayList<Vec2> pointsAroundCircle(int n){
        ArrayList<Vec2> l=new ArrayList<>();
        for(int i=0;i<n;i++){
            int x=(int)Math.round(this.getRadius()*Math.cos(2*i*Math.PI/n))+this.getCenter().getX();
            int y=(int)Math.round(this.getRadius()*Math.sin(2*i*Math.PI/n))+this.getCenter().getY();
            Vec2 vectToAdd=new InternalVectCartesian(x,y);
            l.add(vectToAdd);
        }
        return l;
    }

    /**
     * @see Cloneable
     */
    @Override
    public Circle clone() {
        return new Circle(this.center, this.radius, this.angleStart, this.angleEnd);
    }

    /**
     * @see Object#equals(Object)
     */
    @Override
    public boolean equals(Object o) {
        if(o instanceof Circle){
            return this.getCenter().equals(((Circle) o).getCenter()) && this.radius==((Circle) o).radius;
        }
        return false;
    }

    /**
     * @see Object#hashCode()
     */
    @Override
    public int hashCode() {
        return (int) (this.center.hashCode() + 1000*this.radius);
    }

    /**
     * @see Object#toString()
     */
    @Override
    public String toString() {
        return String.format(Locale.US, "Circle [center: %s, ray : %.1f, angleStart : %.5f, angleEnd : %.5f",
                this.center.toString(), this.radius, this.angleStart, this.angleEnd);
    }

    /**
     * Getters & Setters
     */
    public float getRadius() {
        return radius;
    }
    public void setRadius(float radius) {
        this.radius = radius;
    }
    public double getAngleStart() {
        return angleStart;
    }
    public double getAngleEnd() {
        return angleEnd;
    }
    public void setAngleStart(float angleStart) {
        this.angleStart = angleStart;
    }
    public void setAngleEnd(float angleEnd) {
        this.angleEnd = angleEnd;
    }

}
