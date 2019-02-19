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

package ai.goap;

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
        Log.AI.debug("Début de la planification");
        Node startNode = new Node(null);

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
        if(path.isEmpty()) {
            Log.AI.debug("But déjà atteint!");
            return new Stack<>();
        }

        Optional<Node> cheapest = path.stream()
                .min(Comparator.comparingDouble(a -> a.runningCost));
        Stack<Node> result = new Stack<>();
        Node n = cheapest.get();

        long startStackTime = System.nanoTime();
        Log.AI.debug("Création du stack:");
        while(n != null) {
            if(n.action != null) { // on évite d'ajouter le noeud de départ au chemin
                Log.AI.debug(">> "+n.getAction());
                //result.insertElementAt(n, 0);
                // TODO: check order
                // TODO: insertElementAt a l'air d'être un poil plus lent, faudrait voir pour remplacer par une Queue
                result.push(n);
            }
            n = n.parent;
        }
        long elapsed = (System.nanoTime()-startStackTime);
        Log.AI.debug("la création du stack a pris "+elapsed+"ns ("+elapsed/1000000+"ms)");
        return result;
    }

    private boolean buildPathToGoal(Node startNode, List<Node> path, Set<Node> usableNodes, EnvironmentInfo info, EnvironmentInfo goal) {
        boolean[] subPaths = new boolean[usableNodes.size()];
        Thread[] subThreads = new Thread[usableNodes.size()];
        int index = 0;
        for(Node node : usableNodes) {
            final int i = index;
            Thread thread = new Thread(() -> {
                subPaths[i] = findSubPath(node, startNode, path, usableNodes, info, goal, 0);
            });
            thread.setPriority(Thread.MAX_PRIORITY);
            subThreads[i] = thread;
           // thread.run();
            thread.start();

            index++;
        }

        while(true) {
            try {
                boolean allFinished = true;
                for(Thread t : subThreads) {
                    if(t.isAlive()) {
                        allFinished = false;
                        break;
                    }
                }
                if(allFinished)
                    break;
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        for (boolean b : subPaths) {
            if(b)
                return true;
        }
        return false;
    }

    private boolean buildPathToGoal(Node parent, List<Node> path, Set<Node> usableNodes, EnvironmentInfo info, EnvironmentInfo goal, int depth) {
        if(goal.isMetByState(info)) // on est déjà arrivé au but!
            return true;
        boolean foundAtLeastOnePath = false;
        for(Node actionNode : usableNodes) {
            boolean foundSubPath = findSubPath(actionNode, parent, path, usableNodes, info, goal, depth);
            if(foundSubPath)
                foundAtLeastOnePath = true;
        }

        return foundAtLeastOnePath;
    }

    private boolean findSubPath(Node actionNode, Node parent, List<Node> path, Set<Node> usableNodes, EnvironmentInfo info, EnvironmentInfo goal, int depth) {
        boolean foundAtLeastOnePath = false;
        long startTime = System.currentTimeMillis();
        StringBuilder depthStr = new StringBuilder();
        for (int i = 0; i < depth; i++) {
            depthStr.append(">");
        }
        Log.AI.debug(">"+depthStr+" Testing "+actionNode.getAction());
        if(actionNode.getAction().arePreconditionsMet(info)) { // noeud utilisable
            Log.AI.debug("Can be executed: "+actionNode.getAction());
            EnvironmentInfo newState = info.copyWithEffects(actionNode.getAction());
            if(actionNode.requiresMovement(newState)) {
                actionNode.updateTargetPosition(newState, newState.getXYO().getPosition()); // mise à jour de la position de l'IA
                // TODO: angle
            }
            if(goal.isMetByState(newState)) {
                double runningCost = parent.runningCost + actionNode.getCost(info);
                synchronized (path) {
                    path.add(actionNode.cloneWithParent(parent, runningCost));
                }
                foundAtLeastOnePath = true;
            } else {
                // on retire cette action de la liste des actions possibles
                Set<Node> newUsableNodes = usableNodes.stream().filter(n -> n != actionNode).collect(Collectors.toSet());
                boolean foundSubpath = buildPathToGoal(actionNode, path, newUsableNodes, newState, goal, depth+1); // on continue à parcourir l'arbre

                if(foundSubpath) {
                    foundAtLeastOnePath = true;
                }
            }
        }
        long elapsed = (System.currentTimeMillis()-startTime);
        Log.AI.debug(">"+depthStr+" "+elapsed+" for "+actionNode.getAction()+" usableNodes = "+
                usableNodes.stream().map(n -> n.getAction().toString()).collect(Collectors.joining(", ")));
        return foundAtLeastOnePath;
    }

}