/**
 * Copyright (c) 2019, INTech.
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

package locomotion;

import data.Graphe;
import data.graphe.Node;
import data.graphe.Ridge;
import data.table.MobileCircularObstacle;
import utils.container.Module;
import utils.math.Vec2;

import java.util.*;

/**
 * Module déstiner à calculer un chemin entre deux points de la table
 *
 * @author rem
 */
public class Pathfinder implements Module {

    /**
     * Graphe de recherche de chemin
     */
    private Graphe graphe;

    /**
     * Liste des noeuds à visité
     */
    private PriorityQueue<Node> openList;

    /**
     * Liste des noeuds déjà visités
     */
    private LinkedList<Node> closedList;

    private Map<Node, Double> gScore;
    private Map<Node, Node> parents;
    private Map<Node, Double> heuristiques;

    private Node lastAim;

    /**
     * Construit un pathfinder
     * @param graphe    graphe paramétrant la table
     */
    public Pathfinder(Graphe graphe) {
        heuristiques = new HashMap<>();
        gScore = new HashMap<>();
        parents = new HashMap<>();

        this.graphe = graphe;
        this.openList = new PriorityQueue<>(new NodeComparator(heuristiques));
        this.closedList = new LinkedList<>();
    }

    /**
     * Algorithme permettant de calculer le chemin entre deux noeuds du graphe
     * @param start noeud de départ
     * @param aim   noeud d'arrivé
     * @return      le plus court chemin du noeud de départ au noeud d'arrivé
     * @throws NoPathFound
     *              s'il n'existe pas de chemin entre les deux noeuds
     */
    public LinkedList<Vec2> findPath(Node start, Node aim, Set<MobileCircularObstacle> encounteredEnemies) throws NoPathFound {
        graphe.cacheLocks.readLock().lock();
        try {
            Map<Node, LinkedList<Vec2>> alreadyComputedPaths = graphe.cache.get(start);
            if(alreadyComputedPaths != null) {
                LinkedList<Vec2> computedPath = alreadyComputedPaths.get(aim);
                if(computedPath != null) {
                    return computedPath;
                }
            }
        } finally {
            graphe.cacheLocks.readLock().unlock();
        }

        Node currentNode;
        Set<Node> neighbours;

        // On clean la liste des noeuds à visiter et celles des noeuds visités et on ajoute le noeud de départ
        closedList.clear();
        openList.clear();
        openList.add(start);

        gScore.clear();
        parents.clear();

        try {
            graphe.readLock().lock();
            graphe.updateHeuristique(aim, lastAim, heuristiques);

            lastAim = aim;

            gScore.put(start, 0.0);

            // Tant qu'il y a des noeuds à visiter
            while (!openList.isEmpty()) {
                currentNode = openList.poll();
                if(currentNode == null)
                    continue;

                // Si c'est le noeud d'arrivé, on s'arrête
                if (currentNode.equals(aim)) {
                    return reconstructPath(start, aim);
                }

                // Sinon on parcours tout ses voisins
                neighbours = currentNode.getNeighbours().keySet();
                for (Node neighbour : neighbours) {
                    Ridge ridge = currentNode.getNeighbours().get(neighbour);
                    if(ridge == null)
                        continue; // TODO: trouver pourquoi ça arrive avec l'IA

                    // Si le voisin est accessible (s'il n'y a pas d'obstacle mobile entre les deux noeuds)
                    if (ridge.isReachable(graphe, encounteredEnemies)) {
                        double ridgeCost = ridge.getCost();

                        double currentCost = gScore.getOrDefault(currentNode, Node.DEFAULT_COST) + ridgeCost;
                        if(neighbour.equals(aim)) {
                            parents.put(neighbour, currentNode);
                            return reconstructPath(start, neighbour);
                        }

                        // Si l'on a déjà visiter ce noeud et que l'on a trouvé un meilleur chemin, on met à jour le noeud
                        boolean visited = (openList.contains(neighbour) || closedList.contains(neighbour));
                        if (visited && currentCost < gScore.getOrDefault(neighbour, Node.DEFAULT_COST)) {
                            gScore.put(neighbour, currentCost);
                            parents.put(neighbour, currentNode);

                            if (closedList.contains(neighbour)) {
                                closedList.remove(neighbour);
                                openList.add(neighbour);
                            }

                            double heuristicNeighbour = neighbour.costTo(aim);
                            heuristiques.put(neighbour, gScore.get(neighbour) + heuristicNeighbour);
                        } else if (!visited) {
                            // Sinon, si le noeud n'as jamais été visité, lui assigne le coût courant et le noeud courant comme prédecesseur
                            gScore.put(neighbour, currentCost);
                            parents.put(neighbour, currentNode);
                            openList.add(neighbour);

                            double heuristicNeighbour = neighbour.costTo(aim);
                            heuristiques.put(neighbour, gScore.get(neighbour) + heuristicNeighbour);
                        }
                    }

                }
                closedList.add(currentNode);
            }
/*
            // Theta*
            // https://en.wikipedia.org/wiki/Theta*
            closedList.clear();
            openList.clear();
            parents.clear();
            gScore.clear();
            heuristiques.clear();

            gScore.put(start, 0.0);
            parents.put(start, start);
            openList.add(start);
            while(!openList.isEmpty()) {
                Node s = openList.poll();
                if (s.equals(aim)) {
                    return reconstructPath(start, aim);
                }

                closedList.push(s);

                neighbours = s.getNeighbours().keySet();
                for (Node neighbour : neighbours) {
                    Ridge ridge = s.getNeighbours().get(neighbour);
                    if( ! ridge.isReachable(graphe)) {
                        continue;
                    }
                    if (!closedList.contains(neighbour)) {
                        if (!openList.contains(neighbour)) {
                            gScore.put(neighbour, Double.POSITIVE_INFINITY);
                            parents.put(neighbour, null);
                        }
                        updateVertex(aim, s, neighbour);
                    }
                }
            }*/
            throw new NoPathFound(start.getPosition(), aim.getPosition());
        } finally {
            graphe.readLock().unlock();
        }
    }

    private void updateVertex(Node aim, Node s, Node neighbour) {
        boolean canUseParent;
        do {
            Node parent = parents.get(s);
            Ridge lineOfSight = parent.getNeighbours().get(neighbour);
            canUseParent =  ! parent.equals(s) && lineOfSight != null && lineOfSight.isReachable(graphe);
            if(canUseParent) { // différence avec A* => on regarde s'il est possible d'y aller en ligne droite
                s = parent;
            }
            updateOpenListIfNecessary(aim, s, neighbour);
        } while(canUseParent);
    }

    private void updateOpenListIfNecessary(Node aim, Node s, Node neighbour) {
        Ridge ridge = s.getNeighbours().get(neighbour);
        double costToNeighbourFromS = gScore.get(s) + ridge.getCost();
        if(costToNeighbourFromS < gScore.get(neighbour)) {
            gScore.put(neighbour, costToNeighbourFromS);
            parents.put(neighbour, s);
            double heuristicNeighbour = neighbour.costTo(aim);
            heuristiques.put(neighbour, gScore.get(neighbour) + heuristicNeighbour);
            if( ! openList.contains(neighbour)) {
                openList.add(neighbour);
            }
        }
    }

    /**
     * Méthode permettant de reconstruire un chemin trouvé à partir des prédecesseurs de chaque noeud
     * @param start noeud de départ du chemin
     * @param aim   noeud d'arriver
     */
    private LinkedList<Vec2> reconstructPath(Node start, Node aim) {
        Node currentNode = aim;
        LinkedList<Vec2> path = new LinkedList<>();

        do {
            path.add(0, currentNode.getPosition());
            currentNode = parents.getOrDefault(currentNode, null);
        } while (currentNode != null && !(currentNode.equals(start)));

        graphe.cacheLocks.writeLock().lock();
        try {
            Map<Node, LinkedList<Vec2>> alreadyComputedPaths = graphe.cache.computeIfAbsent(start, k -> new HashMap<>());
            alreadyComputedPaths.put(aim, path);
        } finally {
            graphe.cacheLocks.writeLock().unlock();
        }
        return path;
    }

    public Graphe getGraphe() {
        return graphe;
    }

    public void setGraphe(Graphe graphe) {
        this.graphe = graphe;
    }

}
