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
import data.SensorState;
import data.Table;
import data.XYO;
import pfg.config.Config;
import utils.ConfigData;
import utils.LastElementCollection;
import utils.Log;
import utils.MatchTimer;
import utils.communication.CopyIOThread;
import utils.container.ServiceThread;
import utils.math.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Gère les données du lidar pour modifier la table
 *
 * @author rem
 */
public class LidarControler extends ServiceThread {

    /**
     * Temps d'attente entre deux vérification de la queue
     */
    private static final int TIME_LOOP                  = 25;

    /**
     * Separateur entre deux points
     */
    private static final String POINT_SEPARATOR         = ";";

    /**
     * Separateur entre deux coordonnées d'un point
     */
    private static final String COORDONATE_SEPARATOR    = ":";
    private final MatchTimer timer;

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
    private LastElementCollection<String> messageStack;

    /**
     * File de communication avec le Listener
     */
    private LastElementCollection<String> recalageDataStack;


    /**
     * True si autre couleur
     */
    private boolean symetrie;
    private int robotRadius;
    private int enemyRadius;

    /**
     * Chemin du processus gèrant le Lidar (absolu ou relatif au dossier d'exécution)
     */
    private String processPath;
    private boolean shouldRun;


    /**
     * Objets servant au traitement des obstacles
     */
    private String[] points;
    private List<Vec2> mobileObstacles = new LinkedList<>();
    private float margin = 10;
    private Rectangle tableBB;

    /**
     * Objets servant au recalage du Lidar
     */
    private Vec2 potoCenter1 = new VectCartesian(1550,50);
    private Vec2 potoCenter2 = new VectCartesian(1550, 1950);
    private Vec2 potoCenter3 = new VectCartesian(-1550, 1000);
    private ArrayList<Vec2> pointsInPoto1 = new ArrayList<>();
    private ArrayList<Vec2> pointsInPoto2 = new ArrayList<>();
    private ArrayList<Vec2> pointsInPoto3 = new ArrayList<>();

    /**
     * Construit un gestionnaire des données du Lidar
     * @param table     la table
     * @param listener  le listener
     */
    public LidarControler(Table table, Listener listener, MatchTimer timer) {
        this.timer = timer;
        this.table = table;
        this.listener = listener;
        this.messageStack = new LastElementCollection<>();
        this.recalageDataStack = new LastElementCollection<>();
        listener.addCollection(Channel.OBSTACLES, this.messageStack);
        listener.addCollection(Channel.RAW_LIDAR_DATA, this.recalageDataStack);
    }

    @Override
    public void run() {
        if(!shouldRun) {
            return;
        }
        Log.LIDAR_PROCESS.debug("Controller lancé : en attente du listener...");
        Log.LIDAR_PROCESS.debug("Démarrage du processus LiDAR_UST_10LX...");
        try {
            Process lidarProcess = new ProcessBuilder(processPath).start();

            // force l'extinction du programme quand la VM s'arrête
            Runtime.getRuntime().addShutdownHook(new Thread(lidarProcess::destroyForcibly));
            new CopyIOThread(lidarProcess, Log.LIDAR_PROCESS).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        waitWhileTrue(() -> !Connection.LIDAR_DATA.isInitiated());
        Log.LIDAR_PROCESS.debug("Processus OK");

        while (!listener.isAlive()) {
            try {
                Thread.sleep(Listener.TIME_LOOP);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        /* Si jamais besoin du mode RAW:
        try {
            Log.LIDAR.debug("Setting mode to RAW data...");
            Connection.LIDAR_DATA.send("!!R");
            Log.LIDAR.debug("Mode RAW set!");
        } catch (CommunicationException e) {
            e.printStackTrace();
        }
*/

        Log.LIDAR_PROCESS.debug("Controller opérationnel");

        String[] points;
        List<Vec2> mobileObstacles = new LinkedList<>();
        float margin = 10;
        tableBB = new Rectangle(new VectCartesian(0f, table.getWidth()/2), table.getLength()-2*enemyRadius- 2*margin, table.getWidth()-2*enemyRadius- 2*margin);
        while (true) {
            if (!recalageDataStack.isEmpty()) {
                handleRecalageData();
            }
            if (!messageStack.isEmpty()) {
                handleObstacles();
            }
            else {
                try {
                    Thread.sleep(TIME_LOOP);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void handleObstacles(){
        if(SensorState.DISABLE_LIDAR.getData()) {
            return; // on ignore les valeurs renvoyées par le lidar pendant qu'il est désactivé
        }
        XYO currentXYO = XYO.getRobotInstance();
        points = messageStack.pop().split(POINT_SEPARATOR);
        mobileObstacles.clear();
        for (String point : points) {
            Vec2 obstacleCenter = new VectPolar(Double.parseDouble(point.split(COORDONATE_SEPARATOR)[0]),
                    Double.parseDouble(point.split(COORDONATE_SEPARATOR)[1]));
            if(symetrie) {
                obstacleCenter.setA(-obstacleCenter.getA());
            }

            if(obstacleCenter.getR() <= robotRadius)
                continue;
            obstacleCenter.setA(Calculs.modulo(obstacleCenter.getA() + currentXYO.getOrientation(), Math.PI));
            obstacleCenter.plus(currentXYO.getPosition());


            // on ajoute l'obstacle que s'il est dans la table et qu'il est pas dans les rampes
            if(tableBB.isInShape(obstacleCenter) && !table.isPositionInBalance(obstacleCenter)) {
                // signes différents, on est de deux côtés de la table différents
                if(SensorState.DISABLE_ENNEMIES_OTHER_SIDE.getData() && obstacleCenter.getX() * currentXYO.getPosition().getX() <= 0) {
                    //         Log.LIDAR.warning("On ignore l'ennemi à "+obstacleCenter+" parce qu'on a désactivé les ennemis de l'autre côté de la table");
                    continue;
                }
                mobileObstacles.add(obstacleCenter);
            }
        }
        messageStack.clear();
        //table.getGraphe().writeLock().lock();

        //   long start = System.currentTimeMillis();
        table.updateMobileObstacles(mobileObstacles);
        //     System.out.println(">>> "+(System.currentTimeMillis()-start)+" ms");
    }

    @SuppressWarnings("Duplicates")
    private void handleRecalageData(){
        if(SensorState.DISABLE_LIDAR.getData()) {
            return; // on ignore les valeurs renvoyées par le lidar pendant qu'il est désactivé
        }
        points = recalageDataStack.pop().split(POINT_SEPARATOR);
        Vec2 currentRobotPos = XYO.getRobotInstance().getPosition();
        double currentRobotOr = XYO.getRobotInstance().getOrientation();
        Vec2 vectToPoto1 = potoCenter1.minusVector(currentRobotPos);
        Vec2 vectToPoto2 = potoCenter2.minusVector(currentRobotPos);
        Vec2 vectToPoto3 = potoCenter3.minusVector(currentRobotPos);
        Circle circlePoto1 = new Circle(new VectPolar(vectToPoto1.getR(), Calculs.modulo(vectToPoto1.getA()-currentRobotOr, Math.PI)),50);
        Circle circlePoto2 = new Circle(new VectPolar(vectToPoto2.getR(), Calculs.modulo(vectToPoto2.getA()-currentRobotOr, Math.PI)),50);
        Circle circlePoto3 = new Circle(new VectPolar(vectToPoto3.getR(), Calculs.modulo(vectToPoto3.getA()-currentRobotOr, Math.PI)),50);
        System.out.println("Circle1: " + circlePoto1);
        System.out.println("Circle2: " + circlePoto2);
        System.out.println("Circle3: " + circlePoto3);
        Vec2 pointVector;
        for (String point : points) {
            pointVector = new VectPolar(
                    Double.parseDouble(point.split(COORDONATE_SEPARATOR)[0]),
                    Double.parseDouble(point.split(COORDONATE_SEPARATOR)[1])
            );
            if (circlePoto1.isInShape(pointVector)){
                pointsInPoto1.add(pointVector);
            }
            else if (circlePoto2.isInShape(pointVector)){
                pointsInPoto2.add(pointVector);
            }
            else if (circlePoto3.isInShape(pointVector)){
                pointsInPoto3.add(pointVector);
            }
        }

        System.out.println("PointsInPoto1:");
        for (Vec2 point : pointsInPoto1){
            if(symetrie) {
                point.setA(-point.getA());
            }
            point.setA(Calculs.modulo(point.getA() + currentRobotOr, Math.PI));
            point.plus(currentRobotPos);
            System.out.println(point);
        }

        System.out.println("PointsInPoto2:");
        for (Vec2 point : pointsInPoto2){
            if(symetrie) {
                point.setA(-point.getA());
            }
            point.setA(Calculs.modulo(point.getA() + currentRobotOr, Math.PI));
            point.plus(currentRobotPos);
            System.out.println(point);
        }

        System.out.println("PointsInPoto3:");
        for (Vec2 point : pointsInPoto3){
            if(symetrie) {
                point.setA(-point.getA());
            }
            point.setA(Calculs.modulo(point.getA() + currentRobotOr, Math.PI));
            point.plus(currentRobotPos);
            System.out.println(point);
        }

        SensorState.RECALAGE_LIDAR_EN_COURS.setData(false);
    }

    @Override
    public void updateConfig(Config config) {
        this.symetrie = config.getString(ConfigData.COULEUR).equals("violet");
        this.robotRadius = config.getInt(ConfigData.ROBOT_RAY);
        this.enemyRadius = config.getInt(ConfigData.ENNEMY_RAY);
        this.processPath = config.getString(ConfigData.LIDAR_PROCESS_PATH);
        this.shouldRun = config.getBoolean(ConfigData.USING_LIDAR);
    }
}
