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
import data.XYO;
import data.graphe.Node;
import data.graphe.Ridge;
import pfg.config.Config;
import utils.Log;
import utils.container.Service;
import utils.math.Vec2;

import java.util.*;

/**
 * Service déstiner à calculer un chemin entre deux points de la table
 * TODO : 1As ?
 *
 * @author rem
 */
public class Pathfinder implements Service {

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
    private ArrayList<Node> closedList;

    private Map<Node, Integer> costs;
    private Map<Node, Node> parents;
    private Map<Node, Double> heuristiques;

    private Node lastAim;

    /**
     * Construit un pathfinder
     * @param graphe    graphe paramétrant la table
     */
    public Pathfinder(Graphe graphe) {
        heuristiques = new HashMap<>();
        costs = new HashMap<>();
        parents = new HashMap<>();

        this.graphe = graphe;
        this.openList = new PriorityQueue<>(new ComparatorNode(heuristiques));
        this.closedList = new ArrayList<>();
    }

    /**
     * Algorithme permettant de calculer le chemin entre deux noeuds du graphe
     * @param start noeud de départ
     * @param aim   noeud d'arrivé
     * @return      le plus court chemin du noeud de départ au noeud d'arrivé
     * @throws NoPathFound
     *              s'il n'existe pas de chemin entre les deux noeuds
     */
    public ArrayList<Vec2> findPath(Node start, Node aim) throws NoPathFound {
        Node currentNode;
        Set<Node> neighbours;
        int currentCost;

        // On clean la liste des noeuds à visiter et celles des noeuds visités et on ajoute le noeud de départ
        closedList.clear();
        openList.clear();
        openList.add(start);

        try {
            graphe.readLock().lock();
            graphe.updateHeuristique(aim, lastAim, heuristiques);
            lastAim = aim;
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
                    if (ridge.isReachable()) {
                        currentCost = currentNode.getCout() + ridge.getCost();
                        if(neighbour.equals(aim)) {
                            costs.put(neighbour, currentCost);
                            parents.put(neighbour, currentNode);
                            return reconstructPath(start, neighbour);
                        }
                        // Si l'on a déjà visiter ce noeud et que l'on a trouvé un meilleur chemin, on met à jour le noeud
                        if ((openList.contains(neighbour) || closedList.contains(neighbour)) && currentCost < neighbour.getCout()) {
                            costs.put(neighbour, currentCost);
                            parents.put(neighbour, currentNode);
                            if (closedList.contains(neighbour)) {
                                closedList.remove(neighbour);
                                openList.add(neighbour);
                            }
                        } else if (!(openList.contains(neighbour) || closedList.contains(neighbour))) {
                            // Sinon, si le noeud n'as jamais été visité, lui assigne le coût courant et le noeud courant comme prédecesseur
                            costs.put(neighbour, currentCost);
                            parents.put(neighbour, currentNode);
                            openList.add(neighbour);
                        }
                    }
                }
                closedList.add(currentNode);
            }
        } finally {
            graphe.readLock().unlock();
        }

        throw new NoPathFound(start.getPosition(), aim.getPosition());
    }

    /**
     * Méthode permettant de reconstruire un chemin trouvé à partir des prédecesseurs de chaque noeud
     * @param start noeud de départ du chemin
     * @param aim   noeud d'arriver
     */
    private ArrayList<Vec2> reconstructPath(Node start, Node aim) {
        Node currentNode = aim;
        ArrayList<Vec2> path = new ArrayList<>();

        do {
            path.add(0, currentNode.getPosition());
            currentNode = currentNode.getPred();
        } while (currentNode != null && !(currentNode.equals(start)));
        return path;
    }

    @Override
    public void updateConfig(Config config) {

    }

    public Graphe getGraphe() {
        return graphe;
    }

    public void setGraphe(Graphe graphe) {
        this.graphe = graphe;
    }
}
