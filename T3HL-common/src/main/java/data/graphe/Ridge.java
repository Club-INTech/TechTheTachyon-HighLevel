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

package data.graphe;

import data.Graphe;
import data.table.MobileCircularObstacle;
import data.table.Obstacle;
import utils.math.Segment;

import java.util.List;
import java.util.Set;

/**
 * Classe implémentant les arrêtes pour le graphe
 *
 * @author rem
 */
public class Ridge {
    /**
     * Segment représentant l'arrête
     */
    private Segment seg;

    /**
     * Coût de l'arrête
     */
    private final double cost;

    /**
     * Coût fixe de l'arrête
     */
    private static int staticCost;

    /**
     * Constructeur
     * @param cost  coût pour franchire l'arrête
     * @param segment   segment représentant l'arrête
     */
    public Ridge(double cost, Segment segment) {
        this.cost = cost + staticCost;
        this.seg = segment;
    }

    /** Getters & Setters */
    public Segment getSeg() {
        return seg;
    }
    public double getCost() {
        return cost;
    }

    public boolean isReachable(Graphe graphe) {
        return isReachable(graphe, null);
    }

    public boolean isReachable(Graphe graphe, Set<MobileCircularObstacle> encounteredEnemies) {
        synchronized (graphe.getMobileObstacles()) {
            for(MobileCircularObstacle obstacle : graphe.getMobileObstacles()) {
                if(obstacle.isValidated() && obstacle.getPathfindingShape().intersect(seg)) {
                    if(encounteredEnemies != null) {
                        encounteredEnemies.add(obstacle);
                    }
                    return false;
                }
            }
        }

        // On vérifie s'il y a un obstacle temporaire
        synchronized (graphe.getTemporaryObstacles()) {
            for(Obstacle obstacle : graphe.getTemporaryObstacles()) {
                if(obstacle.intersect(seg)) {
                    return false;
                }
            }
        }
        return true;
    }

    public static void setStaticCost(int staticCost) {
        Ridge.staticCost = staticCost;
    }

}
