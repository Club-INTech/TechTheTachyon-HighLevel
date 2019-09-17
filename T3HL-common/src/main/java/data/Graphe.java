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

import data.graphe.Node;
import data.graphe.Ridge;
import data.table.StillCircularObstacle;
import data.table.MobileCircularObstacle;
import data.table.Obstacle;
import pfg.config.Config;
import pfg.config.Configurable;
import utils.Log;
import utils.container.Module;
import utils.math.Circle;
import utils.math.Segment;
import utils.math.Vec2;
import utils.math.InternalVectCartesian;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Classe permettant de paramétrer la table pour faciliter la navigation du robot
 *
 * @author rem
 */
public class Graphe implements Module {

    /**
     * Mémoire qui contient les chemins déjà calculés pour le pathfinding (vidée dès que le graphe change)
     */
    public final Map<Node, Map<Node, LinkedList<Vec2>>> cache = new HashMap<>();

    public final ReentrantReadWriteLock cacheLocks = new ReentrantReadWriteLock(true);

    /**
     * Liste des obstacles temporaires (eg les palets de la zone de chaos)
     */
    private final ArrayList<Obstacle> temporaryObstacles;

    /**
     * Est-ce que le graphe est déjà initialisé? On évite de recharger le graphe quand le panneau change la config
     */
    private boolean initialized;

    // pour pouvoir créer des tableaux d'arraylist
    private static class NodeList extends ArrayList<Node> {}

    /**
     * Table
     */
    private Table table;

    /**
     * Liste des obstacles fixes
     * @see Table#getFixedObstacles()
     */
    private final ArrayList<Obstacle> fixedObstacles;

    /**
     * Liste des obstacles mobiles
     * @see Table#getMobileObstacles()
     */
    private final ConcurrentLinkedQueue<MobileCircularObstacle> mobileCircularObstacles;

    /**
     * Liste des noeuds
     */
    private final ArrayList<Node> nodes;

    /**
     * Liste des arrêtes
     */
    private final ArrayList<Ridge> ridges;

    /**
     * Permet de synchroniser la mise à jour du graphe avec le pathfinding
     */
    private boolean updated;

    /**
     * Paramètres du graphe
     */
    @Configurable("nbrNoeudsCircle")
    private int nodeCircleNbr;
    @Configurable("espacementCircle")
    private double spaceCircleParameter;
    @Configurable("nbrNoeudsX")
    private int nodeXNbr;
    @Configurable("nbrNoeudsY")
    private int nodeYNbr;

    /**
     * Verrous de synchronisation
     */
    private ReadWriteLock locks = new ReentrantReadWriteLock(false);

    /**
     * Dernier noeud auquel on a voulu se rendre, utilisé pour l'heuristique
     */
    private NodeList[][] partitions;

    @Configurable("partitionWidth")
    private int partitioningX = 25;
    @Configurable("partitionHeight")
    private int partitioningY = 25;

    /**
     * Construit un graphe : un ensemble de noeuds relié par des arrêtes servant à discrétiser la surface de la table pour simplifier la navigation du robot
     * @param table la table à paramétrer
     */
    public Graphe(Table table) {
        partitions = new NodeList[partitioningX][partitioningY];
        this.table = table;
        table.setGraphe(this);
        this.fixedObstacles = table.getFixedObstacles();
        this.mobileCircularObstacles = table.getMobileObstacles();
        this.temporaryObstacles = table.getTemporaryObstacles();
        this.nodes = new ArrayList<>();
        this.ridges = new ArrayList<>();
    }

    /**
     * Place les noeuds & arrêtes du graphe
     */
    private void init() {
        resetCache();
        Log.GRAPHE.debug("Initialisation du Graphe...");
        try {
            for (int x = 0; x < partitioningX; x++) {
                for (int y = 0; y < partitioningY; y++) {
                    partitions[x][y] = new NodeList();
                }
            }
            for (Obstacle obstacle : fixedObstacles) {
                placeNodes(obstacle);
            }
            placeNodes();
            placeRidges();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        Log.GRAPHE.debug("Initialisation terminée : " + this.nodes.size() + " noeuds, " + this.ridges.size() + " arrêtes");
    }

    public void clear() {
        fixedObstacles.clear();
        mobileCircularObstacles.clear();
        nodes.clear();
        ridges.clear();
    }

    /**
     * Ré-initialise totalement le graphe
     * ATTENTION : opération coûteuse !
     */
    public void reInit() {
        this.nodes.clear();
        this.ridges.clear();
        init();
    }

    /**
     * Place des noeuds autour d'un obstacle circulaire
     * @param obstacle  obstacle autour duquel placer les noeuds
     * @throws CloneNotSupportedException   exception qui n'arrive jamais...
     */
    private void placeNodes(Obstacle obstacle) throws CloneNotSupportedException {
        Vec2 pos = new InternalVectCartesian(0, 0);
        Log.GRAPHE.debug("Placement de noeuds autour de l'obstacle "+obstacle);
        if (obstacle instanceof StillCircularObstacle) {
            for (int i = 0; i < nodeCircleNbr; i++) {
                pos.setR(spaceCircleParameter * ((Circle) obstacle.getShape()).getRadius());
                pos.setA(i * 2 * Math.PI / nodeCircleNbr);
                pos.plus(obstacle.getPosition());

                if (!table.isPositionInFixedObstacle(pos, false)) {
                    addNode(new Node(pos.clone()));
                    Log.GRAPHE.debug("Ajout d'un noeud en "+pos+" à cause d'un obstacle en "+obstacle);
                }
            }
        }
        Log.GRAPHE.debug("Fin du placement de noeuds autour de l'obstacle "+obstacle);
    }

    private void addNode(Node node) {
        nodes.add(node);
        int indexX = partitionIndexX(node.getPosition());
        int indexY = partitionIndexY(node.getPosition());
/*        System.out.println(">>>> "+partitionIndexX+"/"+partitionIndexY+" ("+nodeMap.length+"/"+nodeMap[0].length+")");
        System.out.println(">> "+node.getPosition()+" ("+table.getLength()+"/"+table.getWidth()+")");*/
        NodeList list = partition(indexX, indexY);
        if(list == null) {
            Log.GRAPHE.critical("Impossible d'ajouter un noeud dans les partitions à la position "+node.getPosition()+"! Ca déborde de la table :(");
        } else {
            list.add(node);
        }
    }

    private int partitionIndexX(Vec2 pos) {
        return partitionIndex(pos.getX()+table.getLength()/2, partitioningX, table.getLength()); // +w/2 pour prendre en compte les positions < 0
    }

    private int partitionIndexY(Vec2 pos) {
        return partitionIndex(pos.getY(), partitioningY, table.getWidth());
    }

    private int partitionIndex(int val, int count, int size) {
        int step = size/count;
        return val / step;
    }

    /**
     * Place des noeuds sur le terrain en quadrillage
     * @throws CloneNotSupportedException   exception qui n'arrive jamais...
     */
    private void placeNodes() throws CloneNotSupportedException {
        Vec2 pos = new InternalVectCartesian(0, 0);
        int xStep = table.getLength()/nodeXNbr;
        int yStep = table.getWidth()/nodeYNbr;

        Log.GRAPHE.debug("Placement des noeuds en quadrillage");
        for (int i=0; i<nodeXNbr; i++) {
            pos.setX(i * xStep - table.getLength()/2);
            for (int j=0; j<nodeYNbr; j++) {
                pos.setY(j * yStep);

                if (!table.isPositionInFixedObstacle(pos, false)) {
                    addNode(new Node(pos.clone()));
                    Log.GRAPHE.debug("Ajout d'un noeud en "+pos);
                }
            }
        }
        Log.GRAPHE.debug("Fin du placement des noeuds en quadrillage");
    }

    /**
     * Initialise les arretes, ici les voisins des noeuds et le cout pour accéder à ce voisin
     * @throws CloneNotSupportedException   exception qui n'arrive jamais...
     */
    private void placeRidges() throws CloneNotSupportedException {
        Segment segment = new Segment(new InternalVectCartesian(0, 0), new InternalVectCartesian(0, 0));
        Log.GRAPHE.debug("Initialisation des arrêtes");
        for (int i = 0; i < nodes.size(); i++) {
            Node node1 = nodes.get(i);
            segment.setPointA(node1.getPosition());
            for (int j = i + 1; j < nodes.size(); j++) {
                Node node2 = nodes.get(j);
                segment.setPointB(node2.getPosition());
                constructRidge(node1, node2, segment);
            }
        }
        Log.GRAPHE.debug("Fin d'initialisation des arrêtes, "+ridges.size()+" arrêtes créées");
    }

    /**
     * Met à jour la disponibilité des arrêtes en fonction des obstacles mobiles
     */
    public void update() {
        resetCache();
    }

    private NodeList partition(int indexX, int indexY) {
        if(indexX < 0 || indexX >= partitioningX
        || indexY < 0 || indexY >= partitioningY)
            return null;
        return partitions[indexX][indexY];
    }

    /**
     * Créé un noeud provisoire si un noeud permanent n'existe pas déjà à la position souhaitée
     * @param position  position du noeud
     * @return le noeud qui est à la position souhaitée
     */
    public Node addProvisoryNode(Vec2 position) {
        Node n = null; // closest
       // double closestDist = Double.POSITIVE_INFINITY;

        int px = partitionIndexX(position);
        int py = partitionIndexY(position);
        // recherche du plus proche dans les partitions autour
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                NodeList list = partition(px+dx, py+dy);
                if(list != null) {
                    // on n'utilise pas un foreach pour éviter de créer des itérateurs pour rien!
                    for (int i = 0; i < list.size(); i++) {
                        Node nodeI = list.get(i);
                        if(nodeI.getPosition().equals(position)) {
                            n = nodeI;
                        }
                    }
                }
            }
        }
        if (n != null) {
            Log.GRAPHE.debug("Le noeud provisoire à "+position+" existe déjà.");
            return n;
        }
        else {
            try {
                //writeLock().lock();
                n = new Node(position, true);
                Log.GRAPHE.debug("Ajout d'un noeud provisoire à "+position);
                Segment seg = new Segment(position, new InternalVectCartesian(0, 0));
                for (Node node : nodes) {
                    seg.setPointB(node.getPosition());
                    constructRidge(n, node, seg);
                }
                addNode(n);

                // reset cache
                resetCache();
                return n;
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            } finally {
                //writeLock().unlock();
            }
            return null;
        }
    }

    /**
     * Supprime un noeud s'il est provisoire
     * @param node  noeud à supprimer
     */
    public void removeProvisoryNode(Node node) {
        if (!node.isPermanent()) {
            try {
                //writeLock().lock();
                Log.GRAPHE.debug("Retrait du noeud provisoire "+node);
                for (Node neighbour : node.getNeighbours().keySet()) {
                    ridges.remove(neighbour.getNeighbours().get(node));
                    neighbour.getNeighbours().remove(node);
                }
                node.getNeighbours().clear(); //test
                nodes.remove(node);
                // reset cache
                resetCache();
            } finally {
                //writeLock().unlock();
            }
        }
    }

    /**
     * Réinitialise les noeuds du graphe
     */
    public void reInitNodes() {
        for (Node node : nodes) {
            node.setPred(null);
            node.setCout(Node.getDefaultCost());
            node.setHeuristique(Node.getDefaultHeuristic());
        }
        resetCache();
    }

    private void resetCache() {
        try {
            cacheLocks.writeLock().lock();
            cache.clear();
        } finally {
            cacheLocks.writeLock().unlock();
        }
    }

    /**
     * Set l'heuristique de toutes les nodes en fonction du point visé
     */
    public void updateHeuristique(Node aim, Node lastAim, Map<Node, Double> heuristiques) {
        if(lastAim != null && lastAim.equals(aim)) {
            return;
        }
        for (Node node : nodes) {
            heuristiques.put(node, aim.getPosition().squaredDistanceTo(node.getPosition()));
        }
    }

    /**
     * Ajoute une arrête au graphe et lie les noeuds entre eux
     * @param node1 le premier noeud à relier
     * @param node2 le second noeud à relier
     * @param segment   segment représentant la possible arrête
     * @throws CloneNotSupportedException   exception qui n'arrive jamais...
     */
    private void constructRidge(Node node1, Node node2, Segment segment) throws CloneNotSupportedException {
        if (!table.intersectAnyFixedObstacle(segment)) {
            Ridge ridge = new Ridge(node1.getPosition().distanceTo(node2.getPosition()), segment.clone());
            ridges.add(ridge);
            node1.addNeighbour(node2, ridge);
            node2.addNeighbour(node1, ridge);
            // Log.GRAPHE.debug("Ajout d'une arrête de "+node1.toString()+" à "+node2.toString()); //ça spamme trop :c
        }
    }

    /**
     * Getters & Setters
     */
    public ArrayList<Node> getNodes() {
        return nodes;
    }
    public ArrayList<Ridge> getRidges() {
        return ridges;
    }

    public ArrayList<Obstacle> getTemporaryObstacles() {
        return temporaryObstacles;
    }

    public ConcurrentLinkedQueue<MobileCircularObstacle> getMobileObstacles() {
        return mobileCircularObstacles;
    }

    public boolean isUpdated() {
        return updated;
    }
    public void setUpdated(boolean updated) {
        if(updated)
            Log.GRAPHE.debug("Le graphe vient d'être mis à jour.");
        else
            Log.GRAPHE.debug("Le graphe ne vient plus d'être tout juste mis à jour.");
        this.updated = updated;
    }

    public Lock writeLock() {
        return locks.writeLock();
    }

    public Lock readLock() {
        return locks.readLock();
    }

    /**
     * @see Module#updateConfig(Config)
     */
    @Override
    public void updateConfig(Config config) {
        // L'initialisation du Graphe a besoin des données de la config :'(
        if( ! initialized) {
            synchronized (fixedObstacles) {
                synchronized (mobileCircularObstacles) {
                    this.init();
                    initialized = true;
                }
            }
        }
    }
}
