/**
 * Copyright (c) 2018, INTech.
 * this file is part of INTech's HighLevel.

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

package data;

import data.table.MobileCircularObstacle;
import data.table.Obstacle;
import pfg.config.Config;
import utils.ConfigData;
import utils.Log;
import utils.container.Service;
import utils.math.Circle;
import utils.math.Segment;
import utils.math.Vec2;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Classe représentant la table et gérant les obstacles
 * // TODO Ajouter des logs
 *
 * @author rem
 */
public class Table implements Service {
    /**
     * Graphe
     */
    private Graphe graphe;

    /**
     * Liste des obstacles fixes
     */
    private ArrayList<Obstacle> fixedObstacles;

    /**
     * Liste des obstacles mobiles
     */
    private ArrayList<MobileCircularObstacle> mobileObstacles;

    /**
     * Longueur de la table (en x, en mm)
     */
    private int length;

    /**
     * Longueur de la table (en y, en mm)
     */
    private int width;

    /**
     * Rayon du robot
     */
    private int robotRay;

    /**
     * Rayon du buddy robot !
     */
    private int buddyRobotRay;

    /**
     * Rayon des robots adverses
     */
    private int ennemyRobotRay;

    /**
     * Limite lorque l'on compare deux positions
     */
    private int compareThreshold;

    /**
     * Constructeur de la table
     */
    public Table() {
        this.fixedObstacles = new ArrayList<>();
        this.mobileObstacles = new ArrayList<>();
        this.initObstacle();
    }

    /**
     * Initialisation des obstacles fixes de la table
     */
    private void initObstacle() {
        // TODO : Remplir avec les obstacles de l'année !
    }

    /**
     * Met à jour la table à partir d'une liste de points représentant le centre les obstacles détectés
     * @param points    liste des centres des obstacles
     */
    public void updateMobileObstacles(ArrayList<Vec2> points) {
        Iterator<MobileCircularObstacle> iterator = mobileObstacles.iterator();
        MobileCircularObstacle obstacle;
        Iterator<Vec2> it = points.iterator();
        Vec2 point;
        Log.LIDAR.debug("Mise à jour des Obstacle...");

        while (iterator.hasNext()) {
            obstacle = iterator.next();
            while (it.hasNext()) {
                point = it.next();
                if (obstacle.isInObstacle(point)) {
                    obstacle.update(point);
                    it.remove();
                }
            }
            if (obstacle.getOutDatedTime() < System.currentTimeMillis()) {
                iterator.remove();
            }
        }

        for (Vec2 pt : points) {
            int ray = ennemyRobotRay;
            if (pt.distanceTo(XYO.getBuddyInstance().getPosition()) < compareThreshold) {
                ray = buddyRobotRay;
            }
            MobileCircularObstacle obst = new MobileCircularObstacle(pt, ray);
            Log.LIDAR.debug("Obstacle mobile ajouté : " + obst);
            mobileObstacles.add(obst);
        }
        if (graphe != null) {
            this.graphe.update();
        } else {
            Log.LIDAR.warning("Graphe non instancié");
        }
        Log.LIDAR.debug("Mise à jour des obstacles terminées");
    }

    /**
     * @param point  position à tester
     * @return  true si le point est dans un obstacle fixe
     */
    public boolean isPositionInFixedObstacle(Vec2 point) {
        Iterator<Obstacle> iterator = fixedObstacles.iterator();
        Obstacle obstacle;
        while (iterator.hasNext()) {
            obstacle = iterator.next();
            if (obstacle.isInObstacle(point)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param point position à tester
     * @return  true si le point est dans un obstacle mobile
     */
    public boolean isPositionInMobileObstacle(Vec2 point) {
        Iterator<MobileCircularObstacle> iterator = mobileObstacles.iterator();
        MobileCircularObstacle obstacle;
        while (iterator.hasNext()) {
            obstacle = iterator.next();
            if (obstacle.isInObstacle(point)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Sert à savoir si un segment intersecte l'un des obstacles
     * @param segment   segment à tester
     * @return  true si le segment intersecte l'un des obstacles fixes
     */
    public boolean intersectAnyFixedObstacle(Segment segment) {
        Iterator<Obstacle> iterator = fixedObstacles.iterator();
        Obstacle obstacle;
        while (iterator.hasNext()) {
            obstacle = iterator.next();
            if (obstacle.intersect(segment)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Sert à savoir si un segment intersecte l'un des obstacles mobiles
     * @param segment   segment à tester
     * @return  true si le segment intersecte l'un des obstacles mobiles
     */
    public boolean intersectAnyMobileObstacle(Segment segment) {
        Iterator<MobileCircularObstacle> iterator = mobileObstacles.iterator();
        Obstacle obstacle;
        while (iterator.hasNext()) {
            obstacle = iterator.next();
            if (obstacle.intersect(segment)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Sert à savoir si un cercle intersecte l'un des obstacles mobiles
     * @param circle   cercle à tester
     * @return  true si le cercle intersecte l'un des obstacles mobiles
     */
    public boolean intersectAnyMobileObstacle(Circle circle) {
        Iterator<MobileCircularObstacle> iterator = mobileObstacles.iterator();
        MobileCircularObstacle obstacle;
        while (iterator.hasNext()) {
            obstacle = iterator.next();
            if (obstacle.getPosition().distanceTo(circle.getCenter()) < ((Circle) obstacle.getShape()).getRadius() + circle.getRadius()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Ajoute un obstacle fixe à la table et met à jour le graphe
     * ATTENTION : méthode coûteuse car le graphe doit être recalculé
     * @param obstacle  nouvel obstacle
     */
    public void addFixedObstacle(Obstacle obstacle) {
        if (obstacle instanceof MobileCircularObstacle) {
            throw new IllegalArgumentException("L'obstacle ajouté n'est pas fixe !");
        }
        this.fixedObstacles.add(obstacle);
        if (graphe != null) {
            this.graphe.reInit();
        } else {
            Log.LIDAR.warning("Graphe non instancié");
        }
    }

    /**
     * Retire un obstacle fixe de la table et met à jour le graphe
     * ATTENTION : méthode coûteuse car le graphe doit être recalculé
     * @param obstacle  obstacle à retirer
     */
    public void removeFixedObstacle(Obstacle obstacle) {
        if (obstacle instanceof MobileCircularObstacle) {
            throw new IllegalArgumentException("L'obstacle ajouté n'est pas fixe !");
        }
        this.fixedObstacles.remove(obstacle);
        if (graphe != null) {
            this.graphe.reInit();
        } else {
            Log.LIDAR.warning("Graphe non instancié");
        }
    }

    /**
     * Getters & Setters
     */
    public Graphe getGraphe() {
        return this.graphe;
    }
    void setGraphe(Graphe graphe) {
        this.graphe = graphe;
    }
    public ArrayList<Obstacle> getFixedObstacles() {
        return fixedObstacles;
    }
    public ArrayList<MobileCircularObstacle> getMobileObstacles() {
        return mobileObstacles;
    }
    public int getLength() {
        return length;
    }
    public int getWidth() {
        return width;
    }

    /**
     * @see Service#updateConfig(Config)
     */
    @Override
    public void updateConfig(Config config) {
        this.robotRay = config.getInt(ConfigData.ROBOT_RAY);
        this.buddyRobotRay = config.getInt(ConfigData.BUDDY_RAY);
        this.ennemyRobotRay = config.getInt(ConfigData.ENNEMY_RAY);
        this.length = config.getInt(ConfigData.TABLE_X);
        this.width = config.getInt(ConfigData.TABLE_Y);
        this.compareThreshold = config.getInt(ConfigData.VECTOR_COMPARISON_THRESHOLD);
    }
}
