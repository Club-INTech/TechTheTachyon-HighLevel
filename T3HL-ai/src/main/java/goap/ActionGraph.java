package goap;

import utils.Log;
import utils.math.Vec2;

import java.util.*;
import java.util.stream.Collectors;

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
         * L'action associée à ce noeud
         */
        private final Action action;
        private Node parent;

        public Node(Action action) {
            this.action = action;
        }

        /**
         * Renvoie le coût de l'action.
         * /!\\ Le coût peut être dynamique!
         */
        public double getCost(EnvironmentInfo info) {
            return action.getCost(info);
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
            runningCost = 0.0;
            action.reset();
        }

        public Node cloneWithParent(Node parent, double runningCost) {
            Node clone = new Node(action);
            clone.parent = parent;
            clone.runningCost = runningCost;
            return clone;
        }

        public Node getParent() {
            return parent;
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
    public Stack<Node> plan(EnvironmentInfo info, EnvironmentInfo goal) {
        Node startNode = new Node(null /* TODO */);

        for (Node node : nodes) {
            node.reset();
        }

        Set<Node> usableNodes = new HashSet<>(nodes);
        List<Node> path = new LinkedList<>();
        boolean foundPath = buildPathToGoal(startNode, path, usableNodes, info, goal);
        if(!foundPath) {
            Log.AI.critical("Impossible de planifier des actions! Aucun chemin n'a été trouvé dans le graphe.");
            return null;
        }

        Optional<Node> cheapest = path.stream()
                .sorted(Comparator.comparingDouble(a -> a.runningCost))
                .map(n -> {
                    System.out.println(">>"+n.action);
                    return n;
                })
                .findFirst()
        ;
        Stack<Node> result = new Stack<>();
        Node n = cheapest.get();
        while(n != null) {
            if(n.action != null) { // on évite d'ajouter le noeud de départ au chemin
                result.insertElementAt(n, 0);
            }
            n = n.parent;
        }
        return result;
    }

    private boolean buildPathToGoal(Node parent, List<Node> path, Set<Node> usableNodes, EnvironmentInfo info, EnvironmentInfo goal) {
        boolean foundAtLeastOnePath = false;
        for(Node actionNode : usableNodes) {
            System.out.println(">>> "+actionNode.action);
            if(actionNode.getAction().arePreconditionsMet(info)) { // noeud utilisable
                System.out.println(">>>!! "+actionNode.action);
                EnvironmentInfo newState = info.copyWithEffects(actionNode.getAction().effects);

                if(goal.isMetByState(newState)) {
                    double runningCost = parent.runningCost + actionNode.getCost(info);
                    path.add(actionNode.cloneWithParent(parent, runningCost));
                    foundAtLeastOnePath = true;
                    System.out.println("goal!!");
                } else {
                    // on retire cette action de la liste des actions possibles
                    Set<Node> newUsableNodes = usableNodes.stream().filter(n -> n != actionNode).collect(Collectors.toSet());
                    boolean foundSubpath = buildPathToGoal(actionNode, path, newUsableNodes, newState, goal); // on continue à parcourir l'arbre

                    if(foundSubpath) {
                        foundAtLeastOnePath = true;
                    }
                }
            }
        }
        return foundAtLeastOnePath;
    }

}