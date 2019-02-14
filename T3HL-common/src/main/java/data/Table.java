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
import data.table.StillCircularObstacle;
import data.table.StillCircularRectangularObstacle;
import pfg.config.Config;
import utils.ConfigData;
import utils.Log;
import utils.container.Service;
import utils.math.Circle;
import utils.math.Segment;
import utils.math.Vec2;
import utils.math.VectCartesian;
import utils.math.CircularRectangle;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

/**
 * Classe représentant la table et gérant les obstacles
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
    private final ArrayList<Obstacle> fixedObstacles;

    public Obstacle paletRougeDroite;
    public Obstacle paletBleuDroite;
    public Obstacle paletVertDroite;
    public Obstacle paletRougeGauche;
    public Obstacle paletBleuGauche;
    public Obstacle paletVertGauche;
    public Obstacle paletRougeUnZoneChaosDroite;
    public Obstacle paletRougeDeuxZoneChaosDroite;
    public Obstacle paletVertZoneChaosDroite;
    public Obstacle paletBleuZoneChaosDroite;
    public Obstacle paletRougeUnZoneChaosGauche;
    public Obstacle paletRougeDeuxZoneChaosGauche;
    public Obstacle paletVertZoneChaosGauche;
    public Obstacle paletBleuZoneChaosGauche;

    /**
     * Obstacle mobile simulé
     */

    private MobileCircularObstacle simulatedObstacle;

    /**
     * Liste des obstacles mobiles. SYNCHRONISER LES ACCES!
     */
    private final ArrayList<MobileCircularObstacle> mobileObstacles;

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

    // ====================================================================================
    // |  Variables temporaires pour éviter d'instancier des millions d'objets par match  |
    // ====================================================================================

    /**
     * Liste temporaires des obstacles mobiles, utilisée pour pouvoir écrire tous les obstacles d'un coup
     */
    private final List<MobileCircularObstacle> mobileObstacleBuffer;

    /**
     * Constructeur de la table
     */
    public Table() {
        this.fixedObstacles = new ArrayList<>();
        this.mobileObstacles = new ArrayList<>();
        this.mobileObstacleBuffer = new ArrayList<>();

        Vec2 vecteurPaletRougeUnZoneChaosDroite = new VectCartesian(461, 1050);
        Obstacle paletRougeUnZoneChaosDroite = new StillCircularObstacle(vecteurPaletRougeUnZoneChaosDroite, 219);
        this.paletRougeUnZoneChaosDroite = paletRougeUnZoneChaosDroite;

        Vec2 vecteurPaletRougeDeuxZoneChaosDroite = new VectCartesian(539, 1050);
        Obstacle paletRougeDeuxZoneChaosDroite = new StillCircularObstacle(vecteurPaletRougeDeuxZoneChaosDroite, 219);
        this.paletRougeDeuxZoneChaosDroite = paletRougeDeuxZoneChaosDroite;

        Vec2 vecteurPaletRougeUnZoneChaosGauche = new VectCartesian(-461, 1050);
        Obstacle paletRougeUnZoneChaosGauche = new StillCircularObstacle(vecteurPaletRougeUnZoneChaosGauche, 219);
        this.paletRougeUnZoneChaosGauche = paletRougeUnZoneChaosGauche;

        Vec2 vecteurPaletRougeDeuxZoneChaosGauche = new VectCartesian(-539, 1050);
        Obstacle paletRougeDeuxZoneChaosGauche = new StillCircularObstacle(vecteurPaletRougeDeuxZoneChaosGauche, 219);
        this.paletRougeDeuxZoneChaosGauche = paletRougeDeuxZoneChaosGauche;

        Vec2 vecteurPaletVertZoneChaosDroite = new VectCartesian(500, 1089);
        Obstacle paletVertZoneChaosDroite = new StillCircularObstacle(vecteurPaletVertZoneChaosDroite, 219);
        this.paletVertZoneChaosDroite = paletVertZoneChaosDroite;

        Vec2 vecteurPaletVertZoneChaosGauche = new VectCartesian(-500, 1089);
        Obstacle paletVertZoneChaosGauche = new StillCircularObstacle(vecteurPaletVertZoneChaosGauche, 219);
        this.paletVertZoneChaosGauche = paletVertZoneChaosGauche;

        Vec2 vecteurPaletBleuZoneChaosDroite = new VectCartesian(500, 1011);
        Obstacle paletBleuZoneChaosDroite = new StillCircularObstacle(vecteurPaletBleuZoneChaosDroite, 219);
        this.paletBleuZoneChaosDroite = paletBleuZoneChaosDroite;

        Vec2 vecteurPaletBleuZoneChaosGauche = new VectCartesian(-500, 1011);
        Obstacle paletBleuZoneChaosGauche = new StillCircularObstacle(vecteurPaletBleuZoneChaosGauche, 219);
        this.paletBleuZoneChaosGauche = paletBleuZoneChaosGauche;

        /**
         * Couleur représente la couleur de la zone se situant derrière le palet !
         */

        ///* FIXME: re-add
        Vec2 vecteurPaletRougeDroiteCentre = new VectCartesian(1000,450);
        Obstacle paletRougeDroite = new StillCircularObstacle(vecteurPaletRougeDroiteCentre,199);
        this.paletRougeDroite=paletRougeDroite;

        Vec2 vecteurPaletVertDroiteCentre = new VectCartesian(1000,750);
        Obstacle paletVertDroite = new StillCircularObstacle(vecteurPaletVertDroiteCentre,199);
        this.paletVertDroite=paletVertDroite;

        Vec2 vecteurPaletBleuDroiteCentre = new VectCartesian(1000,1050);
        Obstacle paletBleuDroite = new StillCircularObstacle(vecteurPaletBleuDroiteCentre,199);
        this.paletBleuDroite=paletBleuDroite;

        Vec2 vecteurPaletRougeGaucheCentre = new VectCartesian(-1000,450);
        Obstacle paletRougeGauche = new StillCircularObstacle(vecteurPaletRougeGaucheCentre,199);
        this.paletRougeGauche=paletRougeGauche;

        Vec2 vecteurPaletVertGaucheCentre = new VectCartesian(-1000,750);
        Obstacle paletVertGauche = new StillCircularObstacle(vecteurPaletVertGaucheCentre,199);
        this.paletVertGauche=paletVertGauche;

        Vec2 vecteurPaletBleuGaucheCentre = new VectCartesian(-1000,1050);
        Obstacle paletBleuGauche = new StillCircularObstacle(vecteurPaletBleuGaucheCentre,199);
        this.paletBleuGauche=paletBleuGauche;
    }

    /**
     * Initialisation des obstacles fixes de la table
     */
    public void initObstacles() {
        initObstaclesNoInit();
        this.updateTableAfterFixedObstaclesChanges();
    }

    public void initObstaclesNoInit() {
        Vec2 vecteurRampeCentre = new VectCartesian(0,1789);
        CircularRectangle formeRampe = new CircularRectangle(vecteurRampeCentre,2100,422,180);
        Obstacle rampe = new StillCircularRectangularObstacle(formeRampe);

        Vec2 vecteurSupportPaletRampeDroiteCentre = new VectCartesian(750,1561);           //arrondi
        CircularRectangle formePaletSupportRampeDroite = new CircularRectangle(vecteurSupportPaletRampeDroiteCentre,600,18,180);
        Obstacle paletSupportRampeDroite = new StillCircularRectangularObstacle(formePaletSupportRampeDroite);

        Vec2 vecteurSupportPaletRampeGaucheCentre = new VectCartesian(-750,1561);           //arrondi
        CircularRectangle formePaletSupportRampeGauche = new CircularRectangle(vecteurSupportPaletRampeGaucheCentre,600,18,180);
        Obstacle paletSupportRampeGauche = new StillCircularRectangularObstacle(formePaletSupportRampeGauche);

        Vec2 vecteurSeparationRampeCentre = new VectCartesian(0,1478);
        CircularRectangle formeSeparationRampe = new CircularRectangle(vecteurSeparationRampeCentre,40,200,180);
        Obstacle separationRampe = new StillCircularRectangularObstacle(formeSeparationRampe);

        Vec2 vecteurAccelerateurCentre = new VectCartesian(0,18);                                 //arrondi
        CircularRectangle formeAccelerateur = new CircularRectangle(vecteurAccelerateurCentre,2000,36,180);
        Obstacle accelerateur = new StillCircularRectangularObstacle(formeAccelerateur);

        Vec2 vecteurGoldeniumDroiteCentre = new VectCartesian(736,46);
        CircularRectangle formeGoldeniumDroite = new CircularRectangle(vecteurGoldeniumDroiteCentre,102,22,180);
        Obstacle goldeniumDroite = new StillCircularRectangularObstacle(formeGoldeniumDroite);

        Vec2 vecteurGoldeniumGaucheCentre = new VectCartesian(-736,46);
        CircularRectangle formeGoldeniumGauche = new CircularRectangle(vecteurGoldeniumGaucheCentre,102,22,180);
        Obstacle goldeniumGauche = new StillCircularRectangularObstacle(formeGoldeniumGauche);

        this.addFixedObstacleNoGraphChange(rampe);
        this.addFixedObstacleNoGraphChange(paletSupportRampeDroite);
        this.addFixedObstacleNoGraphChange(paletSupportRampeGauche);
        this.addFixedObstacleNoGraphChange(separationRampe);
        this.addFixedObstacleNoGraphChange(accelerateur);
        this.addFixedObstacleNoGraphChange(goldeniumDroite);
        this.addFixedObstacleNoGraphChange(goldeniumGauche);


        // zone de chaos
        this.addFixedObstacleNoGraphChange(paletRougeUnZoneChaosDroite);
        this.addFixedObstacleNoGraphChange(paletRougeDeuxZoneChaosDroite);
        this.addFixedObstacleNoGraphChange(paletRougeUnZoneChaosGauche);
        this.addFixedObstacleNoGraphChange(paletRougeDeuxZoneChaosGauche);
        this.addFixedObstacleNoGraphChange(paletVertZoneChaosDroite);

        this.addFixedObstacleNoGraphChange(paletVertZoneChaosGauche);
        this.addFixedObstacleNoGraphChange(paletBleuZoneChaosDroite);
        this.addFixedObstacleNoGraphChange(paletBleuZoneChaosGauche);

        this.addFixedObstacleNoGraphChange(paletRougeDroite);
        this.addFixedObstacleNoGraphChange(paletVertDroite);
        this.addFixedObstacleNoGraphChange(paletBleuDroite);
        this.addFixedObstacleNoGraphChange(paletRougeGauche);
        this.addFixedObstacleNoGraphChange(paletVertGauche);
        this.addFixedObstacleNoGraphChange(paletBleuGauche);
    }

    /**
     * Met à jour la table à partir d'une liste de points représentant le centre les obstacles détectés
     * @param points    liste des centres des obstacles
     */
    public void updateMobileObstacles(List<Vec2> points) {
        MobileCircularObstacle obstacle;
        Iterator<Vec2> pointIterator = points.iterator();
        Vec2 point;
        Log.LIDAR.debug("Mise à jour des Obstacle...");

        mobileObstacleBuffer.clear();
        synchronized (mobileObstacles) {
            mobileObstacles.clear();
            Iterator<MobileCircularObstacle> mobileObstacleIterator = mobileObstacles.iterator();

            while (mobileObstacleIterator.hasNext()) {
                obstacle = mobileObstacleIterator.next();
                while (pointIterator.hasNext()) {
                    point = pointIterator.next();
                    if (obstacle.isInObstacle(point)) {
                        obstacle.update(point);
                        pointIterator.remove();
                    }
                }
                if (obstacle.getOutDatedTime() < System.currentTimeMillis()) {
                    mobileObstacleIterator.remove();
                }
            }
        }

        for (Vec2 pt : points) {
            int ray = ennemyRobotRay;
            if (pt.distanceTo(XYO.getBuddyInstance().getPosition()) < compareThreshold) {
                ray = buddyRobotRay;
            }
            MobileCircularObstacle obst = new MobileCircularObstacle(pt, ray);
            Log.LIDAR.debug("Obstacle mobile ajouté : " + obst);
            mobileObstacleBuffer.add(obst);
        }

        synchronized (mobileObstacles) {
            mobileObstacles.addAll(mobileObstacleBuffer); // on envoie tout d'un coup
        }

        if (this.graphe != null) {
            this.graphe.writeLock().lock();
            this.graphe.update();
            this.graphe.writeLock().unlock();
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
     * Trouve l'obstacle potentiel dans la position
     * @param point la position à tester
     * @return <pre>Optional.empty()</pre> s'il n'y a aucun obstacle, <pre>Optional.of(some)</pre> avec <pre>some</pre> un obstacle s'il y en a un
     */
    public Optional<Obstacle> findFixedObstacleInPosition(Vec2 point) {
        Iterator<Obstacle> iterator = fixedObstacles.iterator();
        Obstacle obstacle;
        while (iterator.hasNext()) {
            obstacle = iterator.next();
            if (obstacle.isInObstacle(point)) {
                return Optional.of(obstacle);
            }
        }
        return Optional.empty();
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
        return intersectObstacle(segment, fixedObstacles);
    }

    /**
     * Sert à savoir si un segment intersecte l'un des obstacles mobiles
     * @param segment   segment à tester
     * @return  true si le segment intersecte l'un des obstacles mobiles
     */
    public boolean intersectAnyMobileObstacle(Segment segment) {
        return intersectObstacle(segment, mobileObstacles);
    }

    /**
     * @param segment   segment à tester
     * @param obstacles liste d'obstacles à tester
     * @return  true si le segment intersecte l'un des obstacles
     */
    private boolean intersectObstacle(Segment segment, List<? extends Obstacle> obstacles) {
        Iterator<? extends Obstacle> iterator = obstacles.iterator();
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

    public void addFixedObstacleNoGraphChange(Obstacle obstacle) {
        if (obstacle instanceof MobileCircularObstacle) {
            throw new IllegalArgumentException("L'obstacle ajouté n'est pas fixe !");
        }
        synchronized (this.fixedObstacles) {
            this.fixedObstacles.add(obstacle);
            Log.TABLE.debug("ajout de l'obstacle "+obstacle);
        }
    }

    /**
     * Ajoute un obstacle fixe à la table et met à jour le graphe
     * ATTENTION : méthode coûteuse car le graphe doit être recalculé
     * @param obstacle  nouvel obstacle
     */
    public void addFixedObstacle(Obstacle obstacle) {
        addFixedObstacleNoGraphChange(obstacle);
        updateTableAfterFixedObstaclesChanges();
    }

    public void updateTableAfterFixedObstaclesChanges() {
        if (this.graphe != null) {
            this.graphe.writeLock().lock();
            this.graphe.reInit();
            this.graphe.writeLock().unlock();
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
        Log.TABLE.debug("suppression de l'obstacle "+obstacle);
        if (graphe != null) {
            this.graphe.reInit();
        } else {
            Log.LIDAR.warning("Graphe non instancié");
        }
    }

    /**
     * Ajoute l'obstacle mobile SIMULÉ à la liste des obstacles mobiles
     */
    public void SIMULATEDaddMobileObstacle() {
        this.simulatedObstacle = new MobileCircularObstacle(new VectCartesian(0,-1000), 100);
        this.simulatedObstacle.setLifeTime(100000);
        this.mobileObstacles.add(this.simulatedObstacle);
    }

    /**
     * Déplace l'obstacle mobile SIMULÉ à la table
     * @param newPosition nouvelle position de l'obstacle SIMULÉ
     */
    public void SIMULATEDmoveMobileObstacle(Vec2 newPosition) {
        this.simulatedObstacle.update(newPosition);
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
