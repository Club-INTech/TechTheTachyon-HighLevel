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
import utils.ConfigData;
import utils.Log;
import utils.container.Service;
import utils.math.Circle;
import utils.math.Segment;
import utils.math.Vec2;
import utils.math.VectCartesian;

import java.util.ArrayList;

/**
 * Classe permettant de paramétrer la table pour faciliter la navigation du robot
 * // TODO Ajouter des logs
 *
 * @author rem
 */
public class Graphe implements Service {
    /**
     * Table
     */
    private Table table;

    /**
     * Liste des obstacles fixes
     */
    private ArrayList<Obstacle> fixedObstacles;

    /**
     * Liste des obstacles mobiles
     */
    private ArrayList<MobileCircularObstacle> mobileCircularObstacles;

    /**
     * Liste des noeuds
     */
    private ArrayList<Node> nodes;

    /**
     * Liste des arrêtes
     */
    private ArrayList<Ridge> ridges;

    /**
     * Permet de synchroniser la mise à jour du graphe avec le pathfinding
     */
    private boolean updated;

    /**
     * Paramètres du graphe
     */
    private int nodeCricleNbr;
    private double spaceCircleParameter;
    private int nodeXNbr;
    private int nodeYNbr;

    /**
     * Construit un graphe : un ensemble de noeuds relié par des arrêtes servant à discrétiser la surface de la table pour simplifier la navigation du robot
     * @param table la table à paramétrer
     */
    private Graphe(Table table) {
        this.table = table;
        table.setGraphe(this);
        this.fixedObstacles = table.getFixedObstacles();
        this.mobileCircularObstacles = table.getMobileObstacles();
    }

    /**
     * Place les noeuds & arrêtes du graphe
     */
    private void init() {
        Log.GRAPHE.debug("Initialisation du Graphe...");
        this.nodes = new ArrayList<>();
        this.ridges = new ArrayList<>();
        Vec2 pos;

        try {
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

    /**
     * Ré-initialise totalement le graphe
     * ATTENTION : opération coûteuse !
     */
    void reInit() {
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
        Vec2 pos = new VectCartesian(0, 0);
        if (obstacle instanceof StillCircularObstacle) {
            for (int i = 0; i < nodeCricleNbr; i++) {
                pos.setR(spaceCircleParameter * ((Circle) obstacle.getShape()).getRadius());
                pos.setA(i * 2 * Math.PI / nodeCricleNbr);
                pos.plus(obstacle.getPosition());

                if (!table.isPositionInFixedObstacle(pos)) {
                    nodes.add(new Node(pos.clone()));
                }
            }
        }
    }

    /**
     * Place des noeuds sur le terrain en quadrillage
     * @throws CloneNotSupportedException   exception qui n'arrive jamais...
     */
    private void placeNodes() throws CloneNotSupportedException {
        Vec2 pos = new VectCartesian(0, 0);
        int xStep = table.getLength()/nodeXNbr;
        int yStep = table.getWidth()/nodeYNbr;

        for (int i=0; i<nodeXNbr; i++) {
            pos.setX(i * xStep);
            for (int j=0; j<nodeYNbr; j++) {
                pos.setY(j * yStep);

                if (!table.isPositionInFixedObstacle(pos)) {
                    nodes.add(new Node(pos.clone()));
                }
            }
        }
    }

    /**
     * Initialise les arretes, ici les voisins des noeuds et le cout pour accéder à ce voisin
     * @throws CloneNotSupportedException   exception qui n'arrive jamais...
     */
    private void placeRidges() throws CloneNotSupportedException {
        Segment segment = new Segment(new VectCartesian(0, 0), new VectCartesian(0, 0));
        for (int i = 0; i < nodes.size(); i++) {
            Node node1 = nodes.get(i);
            segment.setPointA(node1.getPosition());
            for (int j = i + 1; j < nodes.size(); j++) {
                Node node2 = nodes.get(j);
                segment.setPointB(node2.getPosition());
                constructRidge(node1, node2, segment);
            }
        }
    }

    /**
     * Met à jour la disponibilité des arrêtes en fonction des obstacles mobiles
     */
    void update() {
        Log.LIDAR.debug("Mise à jour du graphe...");
        int counter = 0;
        for (Ridge ridge : ridges) {
            ridge.setReachable(true);
            for (MobileCircularObstacle obstacle : mobileCircularObstacles) {
                if (obstacle.intersect(ridge.getSeg())) {
                    ridge.setReachable(false);
                    counter++;
                    break;
                }
            }
        }
        Log.LIDAR.debug(String.format("Mise à jour du graphe : %d/%d arrêtes non-accessibles", counter, ridges.size()));
    }

    /**
     * Créé un noeud provisoire si un noeud permanent n'existe pas déjà à la position souhaitée
     * @param position  position du noeud
     * @return le noeud qui est à la position souhaitée
     */
    public Node addProvisoryNode(Vec2 position) {
        Node n = null;
        for (Node node : nodes) {
            if (node.getPosition().equals(position)) {
                n = node;
            }
        }
        if (n != null) {
            return n;
        }
        else {
            try {
                n = new Node(position, false);
                Segment seg = new Segment(position, new VectCartesian(0, 0));
                for (Node neighbour : nodes) {
                    seg.setPointB(neighbour.getPosition());
                    constructRidge(n, neighbour, seg);
                }
                nodes.add(n);
                return n;
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
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
            nodes.remove(node);
            for (Node neighbour : node.getNeighbours().keySet()) {
                ridges.remove(neighbour.getNeighbours().get(node));
                neighbour.getNeighbours().remove(node);
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
            Ridge ridge = new Ridge((int) node1.getPosition().distanceTo(node2.getPosition()), segment.clone());
            ridges.add(ridge);
            node1.addNeighbour(node2, ridge);
            node2.addNeighbour(node1, ridge);
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
    public boolean isUpdated() {
        return updated;
    }
    public void setUpdated(boolean updated) {
        this.updated = updated;
    }

    /**
     * @see Service#updateConfig(Config)
     */
    @Override
    public void updateConfig(Config config) {
        nodeXNbr = config.getInt(ConfigData.NBR_NOEUDS_X);
        nodeYNbr = config.getInt(ConfigData.NBR_NOEUDS_Y);
        spaceCircleParameter = config.getDouble(ConfigData.ESPACEMENT_CIRCLE);
        nodeCricleNbr = config.getInt(ConfigData.NBR_NOEUDS_CIRCLE);
        // L'initialisation du Graphe a besoin des données de la config :'(
        this.init();
    }
}
