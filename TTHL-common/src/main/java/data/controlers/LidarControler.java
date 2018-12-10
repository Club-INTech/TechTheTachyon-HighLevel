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

package data.controlers;

import data.Table;
import data.XYO;
import pfg.config.Config;
import utils.ConfigData;
import utils.Log;
import utils.container.Service;
import utils.math.Calculs;
import utils.math.Vec2;
import utils.math.VectPolar;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Gère les données du lidar pour modifier la table
 *
 * @author rem
 */
public class LidarControler extends Thread implements Service {

    /**
     * Temps d'attente entre deux vérification de la queue
     */
    private static final int TIME_LOOP                  = 20;

    /**
     * Separateur entre deux points
     */
    private static final String POINT_SEPARATOR         = ":";

    /**
     * Separateur entre deux coordonnées d'un point
     */
    private static final String COORDONATE_SEPARATOR    = ",";

    /**
     * Table à mettre à jour
     */
    private Table table;

    /**
     * Listener
     */
    private Listener listener;

    /**
     * File de communication avec le Listener
     */
    private ConcurrentLinkedQueue<String> messageQueue;

    /**
     * True si autre couleur
     */
    private boolean symetrie;

    /**
     * Construit un gestionnaire des données du Lidar
     * @param table     la table
     * @param listener  le listener
     */
    public LidarControler(Table table, Listener listener) {
        this.table = table;
        this.listener = listener;
        this.messageQueue = new ConcurrentLinkedQueue<>();
        listener.addQueue(Channel.LIDAR, this.messageQueue);
    }

    @Override
    public void run() {
        Log.LIDAR.debug("Controller lancé : en attente du listener...");
        // TODO : lancer le processus du Lidar !
        while (!listener.isAlive()) {
            try {
                Thread.sleep(Listener.TIME_LOOP);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Log.LIDAR.debug("Controller opérationnel");

        String[] points;
        ArrayList<Vec2> Vec2s = new ArrayList<>();
        Vec2 Vec2;
        while (!Thread.currentThread().isInterrupted()) {
            while (messageQueue.peek() == null) {
                try {
                    Thread.sleep(TIME_LOOP);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            points= messageQueue.poll().split(POINT_SEPARATOR);
            Vec2s.clear();
            for (String point : points) {
                Vec2 = new VectPolar(Double.parseDouble(point.split(COORDONATE_SEPARATOR)[0]),
                        Double.parseDouble(point.split(COORDONATE_SEPARATOR)[1]));
                Vec2.setA(Calculs.modulo(Vec2.getA() + XYO.getRobotInstance().getOrientation(), 2*Math.PI));
                Vec2.plus(XYO.getRobotInstance().getPosition());
                if (symetrie) {
                    Vec2.setX(-Vec2.getX());
                }
                Vec2s.add(Vec2);
            }
            table.updateMobileObstacles(Vec2s);
            table.getGraphe().setUpdated(true);
        }
    }

    @Override
    public void updateConfig(Config config) {
        this.symetrie = config.getString(ConfigData.COULEUR).equals("jaune");
    }
}
