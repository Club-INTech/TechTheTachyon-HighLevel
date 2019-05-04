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

package data.table;

import utils.math.Circle;
import utils.math.Segment;
import utils.math.Shape;
import utils.math.Vec2;

/**
 * Classe implémentant les obstacles mobiles circulaires, dans notre cas les autres robots (adverse & buddy)
 *
 * @author rem
 */
public class MobileCircularObstacle extends Obstacle {
    /**
     * Temps de vie de l'obstacle (en millisecondes) : sert à retirer l'obstacle lorsqu'il n'est plus détecté
     */
    private long outDatedTime;

    /**
     * Temps de vie lors de la création
     * override par la config500
     */
    private static final int DEFAULT_LIFE_TIME    = 2*100;

    /**
     * Marge par défaut (en mm) pour éviter que les robots se cognent
     */
    private static final int DEFAULT_MARGIN = 60;

    private Circle pathShape;

    /**
     * Constructeur à partir d'une position et d'un rayon
     * @param position  position du centre du cercle représentant l'avdversaire
     * @param ray       rayon du cercle représentant l'adversaire
     */
    public MobileCircularObstacle(Vec2 position, int ray) {
        this(position, ray, DEFAULT_MARGIN);
    }

    public MobileCircularObstacle(Vec2 position, int ray, int margin) {
        this(new Circle(position, ray), new Circle(position, ray+margin));
    }

    /**
     * Constructeur à partir d'un cercle
     * @param circle    cercle représentant l'obstacle
     */
    private MobileCircularObstacle(Circle circle, Circle pathShape) {
        super(circle);
        this.pathShape = pathShape;
        this.outDatedTime = DEFAULT_LIFE_TIME + System.currentTimeMillis();
    }

    @Override
    public void setPosition(Vec2 position) {
        super.setPosition(position);
        pathShape.setCenter(position);
    }

    /**
     * Met à jour la position et le temps de vie de l'obstacle
     * @param newPosition   nouvelle position
     */
    public void update(Vec2 newPosition) {
        this.shape.setCenter(newPosition);
        this.pathShape.setCenter(newPosition);
        this.outDatedTime = DEFAULT_LIFE_TIME + System.currentTimeMillis();
    }

    /**
     * Forme géométrique utilisée pour le pathfinding. C'est celle de {@link #getShape()} gonflée de qq cm pour laisser
     * une marge entre les robots adversaires et notre robot
     */
    public Circle getPathfindingShape() {
        return pathShape;
    }

    @Override
    public Obstacle clone() throws CloneNotSupportedException {
        Circle clonedShape = (Circle) this.shape.clone();
        // nécessite le même centre (pas juste en égalité mais en référence)
        return new MobileCircularObstacle(clonedShape, new Circle(clonedShape.getCenter(), pathShape.getRadius()));
    }

    @Override
    public String toString() {
        return "Obstacle mobile circulaire " + this.shape.toString();
    }

    @Override
    public int hashCode() {
        return shape.getCenter().hashCode();
    }

    /**
     * Getters & Setters
     */
    public long getOutDatedTime() {
        return outDatedTime;
    }
    public void setLifeTime(int lifeTime) {
        this.outDatedTime = lifeTime;
    }

    /**
     * Utilisé pour les tests
     */
    public static int getDefaultLifeTime() {
        return DEFAULT_LIFE_TIME;
    }
}
