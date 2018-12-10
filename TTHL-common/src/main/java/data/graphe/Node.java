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

import utils.math.Vec2;

import java.util.HashMap;

/**
 * Classe implémentant un noeud afin de paramtérer la table
 *
 * @author rem
 */
public class Node {

    /**
     * Variables static pour l'initialistation des noeuds
     */
    private static final int DEFAULT_HEURISTIC      = 1000000;
    private static final int DEFAULT_COST           = 1000000;
    private static int FIX_COST                     = 0;

    /**
     * Position du noeud
     */
    private Vec2 position;

    /**
     * Heuristique : variable servant à évaluer la distance entre le ce noeud et le noeud visé lors d'un éxecution du Pathfinder
     */
    private int heuristique;

    /**
     * Coût du noeud : coût du point de départ jusqu'à ce noeud
     */
    private int cout;

    /**
     * Dis si le noeud est permanent (si c'est un noeud ajouté pour le Pathfinder)
     */
    private boolean permanent;

    /**
     * Noeuds voisins
     */
    private HashMap<Node, Ridge> neighbours;

    /**
     * Noeud prédecesseur (le meilleurs noeud voisin)
     */
    private Node pred;

    /**
     * Constructeur
     * @param position  la position du noeud sur la table
     */
    public Node(Vec2 position) {
        this.position = position;
        this.heuristique = DEFAULT_HEURISTIC;
        this.cout = DEFAULT_COST;
        this.permanent = true;
        this.neighbours = new HashMap<>();
    }

    /**
     * Constructeur
     * @param position  la position du noeud
     * @param permanent true si le noeud est un noeud permanent du graphe
     */
    public Node(Vec2 position, boolean permanent) {
        this.position = position;
        this.heuristique = DEFAULT_HEURISTIC;
        this.cout = DEFAULT_COST;
        this.permanent = permanent;
        this.neighbours = new HashMap<>();
    }

    /**
     * Ajoute un voisin au noeud, le cout de l'arete est calculée
     *
     * @param neighbour le noeud voisin à ajouter
     * @param ridge l'arrête qui les relien
     */
    public void addNeighbour(Node neighbour, Ridge ridge){
        if (!this.equals(neighbour)) {
            neighbours.put(neighbour, ridge);
        }
    }

    /**
     * @see Object#equals(Object)
     */
    @Override
    public boolean equals(Object object){
        if(object instanceof Node){
            return this.position.equals(((Node) object).position);
        }
        return false;
    }

    /**
     * @see Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + position.hashCode();
        return result;
    }

    /**
     * @see Object#toString()
     */
    @Override
    public String toString(){
        return "Node [" + this.position.toString() + ", Permanent : " + this.permanent + "]";
    }

    /** Getters & Setters */
    public int getHeuristique() {
        return heuristique;
    }
    public void setHeuristique(int heuristique) {
        this.heuristique = heuristique;
    }
    public int getCout() {
        return cout;
    }
    public void setCout(int cout) {
        this.cout = cout;
    }
    public boolean isPermanent() {
        return permanent;
    }
    public Node getPred() {
        return pred;
    }
    public void setPred(Node pred) {
        this.pred = pred;
    }
    public Vec2 getPosition() {
        return position;
    }
    public HashMap<Node, Ridge> getNeighbours() {
        return neighbours;
    }
    public static void setFixCost(int fixCost) {
        FIX_COST = fixCost;
    }
    public static int getDefaultHeuristic() {
        return DEFAULT_HEURISTIC;
    }
    public static int getDefaultCost() {
        return DEFAULT_COST;
    }
    public static int getFixCost() {
        return FIX_COST;
    }
}
