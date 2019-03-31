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

import connection.Connection;
import data.Table;
import data.XYO;
import pfg.config.Config;
import utils.ConfigData;
import utils.Log;
import utils.communication.CommunicationException;
import utils.communication.CopyIOThread;
import utils.container.Service;
import utils.container.ServiceThread;
import utils.math.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Gère les données du lidar pour modifier la table
 *
 * @author rem
 */
public class LidarControler extends ServiceThread {

    /**
     * Temps d'attente entre deux vérification de la queue
     */
    private static final int TIME_LOOP                  = 20;

    /**
     * Separateur entre deux points
     */
    private static final String POINT_SEPARATOR         = ";";

    /**
     * Separateur entre deux coordonnées d'un point
     */
    private static final String COORDONATE_SEPARATOR    = ":";

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
        Log.LIDAR.debug("Démarrage du processus LiDAR_UST_10LX...");
        try {
            Process lidarProcess = new ProcessBuilder("../bin/LiDAR_UST_10LX").start();
            new CopyIOThread(lidarProcess, Log.LIDAR).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.LIDAR.debug("Processus OK");

        while (!listener.isAlive()) {
            try {
                Thread.sleep(Listener.TIME_LOOP);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        try {
            Connection.LIDAR_DATA.send("R");
        } catch (CommunicationException e) {
            e.printStackTrace();
        }

        Log.LIDAR.debug("Controller opérationnel");

        String[] points;
        ArrayList<Vec2> mobileObstacles = new ArrayList<>();
        Rectangle tableBB = new Rectangle(new VectCartesian(0,1000), 1500, 1000);
        while (!Thread.currentThread().isInterrupted()) {
            while (messageQueue.peek() == null) {
                try {
                    Thread.sleep(TIME_LOOP);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            points = messageQueue.poll().split(POINT_SEPARATOR);
            mobileObstacles.clear();
            for (String point : points) {
                Vec2 obstacleCenter = new VectPolar(Double.parseDouble(point.split(COORDONATE_SEPARATOR)[0]),
                        Double.parseDouble(point.split(COORDONATE_SEPARATOR)[1]));
                obstacleCenter.setA(Calculs.modulo(obstacleCenter.getA() + XYO.getRobotInstance().getOrientation(), Math.PI));
                obstacleCenter.plus(XYO.getRobotInstance().getPosition());
                if (symetrie) {
                    obstacleCenter.setX(-obstacleCenter.getX());
                }
                // on ajoute l'obstacle que s'il est dans la table
//                if(tableBB.isInShape(obstacleCenter)) {
                    mobileObstacles.add(obstacleCenter);
  //              }
            }
            table.updateMobileObstacles(mobileObstacles);
            synchronized (table.getGraphe()) {
                table.getGraphe().setUpdated(true);
            }
        }
    }

    @Override
    public void updateConfig(Config config) {
        this.symetrie = config.getString(ConfigData.COULEUR).equals("jaune");
    }
}
