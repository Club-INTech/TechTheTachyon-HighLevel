package goap;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

/**
 * Représente le graphe des actions possibles pour l'agent et leurs dépendances
 */
public class ActionGraph {

    public static class Node {

        /**
         * Coût courant d'un chemin, utilisé lors de la planification
         */
        double runningCost;

        /**
         * Coût d'une action (en temps, en ressources, selon comment on le voit)
         */
        private double cost;

        private Set<Node> children;

        /**
         * Nom du noeud, utile pour comprendre à quoi il sert
         */
        private final String name;


        // TODO: preconditions
        // TODO: effects on environment
        public Node(String name, double baseCost) {
            this.name = name;
            this.cost = baseCost;
            this.children = new HashSet<>();
        }

        /**
         * Renvoie le coût de l'action.
         * /!\\ Le coût peut être dynamique!
         */
        public double getCost() {
            return cost;
        }

        public void addChild(Node child) {
            children.add(child);
        }

        public Set<Node> getChildren() {
            return children;
        }
    }

    private Set<Node> nodes;

    public ActionGraph() {
        nodes = new HashSet<>();
    }

    /**
     * Crée un nouveau noeud et l'ajoute au graphe
     */
    public Node node(String name, double baseCost) {
        Node node = new Node(name, baseCost);
        nodes.add(node);
        return node;
    }

    /**
     * Planifies la meilleure (théoriquement) trajectoire à travers le graphe pour réussir son but
     */
    public Stack<Node> plan(EnvironmentInfo info) {
        return null; // TODO
    }

}
