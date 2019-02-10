package goap;

import utils.math.Vec2;

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

        private Set<Node> children;

        /**
         * L'action associée à ce noeud
         */
        private final Action action;

        public Node(Action action) {
            this.action = action;
            this.children = new HashSet<>();
        }

        /**
         * Renvoie le coût de l'action.
         * /!\\ Le coût peut être dynamique!
         */
        public double getCost(EnvironmentInfo info) {
            return action.getCost(info);
        }

        public void addChild(Node child) {
            children.add(child);
        }

        public Set<Node> getChildren() {
            return children;
        }

        public void performAction(EnvironmentInfo info) {
            action.perform(info);
        }

        public boolean checkCompletion(EnvironmentInfo info) {
            return action.isComplete(info);
        }

        public boolean requiresMovement(EnvironmentInfo info) {
            return action.requiresMovement(info);
        }

        public void updateTargetPosition(EnvironmentInfo info, Vec2 targetPos) {
            action.updateTargetPosition(info, targetPos);
        }

        public Action getAction() {
            return action;
        }

        public void reset() {
            action.reset();
        }
    }

    private Set<Node> nodes;

    public ActionGraph() {
        nodes = new HashSet<>();
    }

    /**
     * Crée un nouveau noeud et l'ajoute au graphe
     */
    public Node node(Action action) {
        Node node = new Node(action);
        nodes.add(node);
        return node;
    }

    /**
     * Planifies la meilleure (théoriquement) trajectoire à travers le graphe pour réussir son but
     */
    public Stack<Node> plan(EnvironmentInfo info, Node goal) {
        Node startNode = new Node(new Action() {

            // TODO

            @Override
            public double getCost(EnvironmentInfo info) {
                return 0;
            }

            @Override
            public void perform(EnvironmentInfo info) {

            }

            @Override
            public boolean isComplete(EnvironmentInfo info) {
                return false;
            }

            @Override
            public boolean requiresMovement(EnvironmentInfo info) {
                return false;
            }

            @Override
            public void updateTargetPosition(EnvironmentInfo info, Vec2 targetPos) {

            }

            @Override
            public void reset() {

            }
        });

        nodes.add(startNode); // ajout temporaire

        for (Node node : nodes) {
            node.reset();
        }


        for(Node potentialAction : nodes) {
            boolean foundPathToGoal = false; // TODO
        }
        // TODO: parcours du graphe


        nodes.remove(startNode);

        Stack<Node> result = new Stack<>();
        // TODO: add to stack
        return result; // TODO
    }

}
