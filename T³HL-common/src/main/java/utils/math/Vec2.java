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

/**
 * Il s'agit d"une classe définissant des méthodes de calculs spécifiques pour les vecteurs, le constructeur de cette classe est protected
 *
 * @author yousra, rem
 */
public abstract class Vec2 {

    /**abscisse du vecteur*/
    private int x;

    /**ordonnée du vecteur*/
    private int y;

    /**coordonnée radiale*/
    private double r;

    /**calculateAngle polaire du point*/
    private double a;

    /**
     * Construit un vecteur nul
     */
    protected Vec2() {
        this.x = 0;
        this.y = 0;
        this.a = 0;
        this.r = 0;
    }

    /**
     * Construit un vecteur
     * @param x abscisse
     * @param y ordonnée
     */
    protected Vec2(int x, int y) {
        this.x = x;
        this.y = y;
        this.a = this.calculateAngle();
        this.r = this.calculateRay();
    }

    /**
     * Construit un vecteur
     * @param r rayon
     * @param a angle
     */
    protected Vec2(double r, double a) {
        this.r = r;
        this.a = a;
        this.x = (int) Math.round(r*Math.cos(a));
        this.y = (int) Math.round(r*Math.sin(a));
    }

    /**
     * Produit scalaire
     * @param vecteur   autre vecteur
     * @return  le produit scalaire des deux vecteurs
     */
    public int dot(Vec2 vecteur){
        return this.x * vecteur.getX() + this.y*vecteur.getY();
    }

    /**
     * Produit vectoriel (ATTENTION : anti-symétrique)
     * @param vecteur   autre vecteur
     * @return  le module du vecteur issu du produit vectoriel des deux vecteurs
     */
    public int crossProduct(Vec2 vecteur){
        return x * vecteur.getY() - y * vecteur.getX();
    }

    /**
     * Ajout de vecteur
     * @param vecteur
     *              autre vecteur
     * @return  un nouveau vecteur qui est l'addition des deux
     */
    public Vec2 plusVector(Vec2 vecteur){
        return new VectCartesian(this.x + vecteur.getX(), this.y + vecteur.getY());
    }

    /**
     * Retranche un vecteur (ATTENTION : anti-symétrique)
     * @param vecteur
     *              autre vecteur
     * @return  un nouveau vecteur qui est égale à this - vecteur
     */
    public Vec2 minusVector(Vec2 vecteur){
        return new VectCartesian(this.x - vecteur.getX(), this.y - vecteur.getY());
    }

    /**
     * Ajout de vecteur à this
     * @param vecteur
     *              autre vecteur
     */
    public void plus(Vec2 vecteur){
        this.x+=vecteur.getX();
        this.y+=vecteur.getY();
    }

    /**
     * Retrait de vecteur à this
     * @param vecteur
     *              autre vecteur
     */
    public void minus(Vec2 vecteur){
        this.x-=vecteur.getX();
        this.y-=vecteur.getY();
    }

    /**
     * @param other
     *              autre vecteur
     * @return  la distance séparant les deux vecteurs
     */
    public double distanceTo(Vec2 other){
        int x2=(this.x - other.getX()) * (this.x - other.getX());
        int y2=(this.y - other.getY()) * (y - other.getY());
        return Math.sqrt( x2 +y2 );

    }

    /**
     * Homthétie du vecteur par a
     * @param a facteur de multiplication
     * @return  un nouveau vecteur égale à a*this
     */
    public Vec2 homothetie(float a){
        return new VectCartesian(Math.round(a*this.x), Math.round(a*this.y));
    }

    /**
     * @return  r²
     */
    public double squaredLength(){
        return (this.x * this.x + this.y + this.y);
    }

    /**
     * Calcul le symétrique de this
     * @return  le vecteur symétrique de this
     */
    public VectCartesian symetrizeVector(){
        return new VectCartesian(-x,y);
    }

    /**
     * Symétrise le vecteur
     */
    public void symetrize() {
        this.x = -x;
        this.r = calculateRay();
        this.a = calculateAngle();
    }

    /**
     * On calcule l'angle du vecteur entre [-pi, pi[ (non inclus)
     * @return  a
     */
    private double calculateAngle() {
        if (this.squaredLength() == 0)
            return 0;

        double a = Math.min((double) Math.abs(x), Math.abs(y)) / Math.max(Math.abs(x), Math.abs(y));
        double s = a * a;
        double r = ((-0.0464964749 * s + 0.15931422) * s - 0.327622764) * s * a + a;

        if (Math.abs(y) > Math.abs(x))
            r = 1.57079637 - r;
        if (x < 0)
            r = 3.14159274 - r;
        if (y < 0)
            r = -r;
        return r;
    }

    /**
     * Calcul de r à partir de x et y
     * @return  r
     */
    private double calculateRay() {
        return Math.sqrt(this.x * this.x + this.y * this.y);
    }

    /**
     * Spécifie le format utilisé pour discuter avec le LL
     */
    public String toStringEth(){
        return x + " " + y;
    }

    /**
     * @see Object#equals(Object)
     */
    @Override
    public boolean equals(Object obj){
        if (obj instanceof Vec2) {
            return ((Vec2) obj).getX() == this.getX() && ((Vec2) obj).getY() == this.getY();
        }
        else{
            return false;
        }
    }

    /**
     * @see Object#hashCode()
     */
    @Override
    public int hashCode() {
        return this.y + this.x*2000;
    }

    /**
     * @see Object#clone()
     */
    @Override
    public Vec2 clone() {
        return new VectCartesian(this.x, this.y);
    }

    /**
     * @see Object#toString()
     */
    @Override
    public String toString() {
        return String.format("(%s,%s)",this.x,this.y);
    }

    /**
     * Getters & Setters
     */
    public int getX() {
        return x;
    }
    public void setX(int x) {
        this.x = x;
        this.a = this.calculateAngle();
        this.r = this.calculateRay();
    }
    public int getY() {
        return y;
    }
    public void setY(int y) {
        this.y = y;
        this.a = this.calculateAngle();
        this.r = this.calculateRay();
    }
    public double getR() {
        return r;
    }
    public void setR(double r) {
        this.r = r;
        this.x = (int) Math.round(r*Math.cos(a));
        this.y = (int) Math.round(r*Math.sin(a));
    }
    public double getA() {
        return a;
    }
    public void setA(double a) {
        this.a = a;
        this.x = (int) Math.round(r*Math.cos(a));
        this.y = (int) Math.round(r*Math.sin(a));
    }
    public void setXY(int x, int y) {
        this.x = x;
        this.y = y;
        this.a = this.calculateAngle();
        this.r = this.calculateRay();
    }
}
