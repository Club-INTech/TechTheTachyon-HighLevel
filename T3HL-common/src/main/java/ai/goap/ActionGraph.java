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
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Représente le graphe des actions possibles pour l'agent et leurs dépendances
 */
public class ActionGraph {

    private static final Object LOCK = new Object();
    private final Comparator<EnvironmentInfo> stateComparator;

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
        public double getCost(EnvironmentInfo info, int depth) {
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
            parent = null;
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

    /**
     * Une action juste pour forcer la copie du graphe et de la table lors de la planification
     */
    private Action copyAction = new Action() {
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

        @Override
        public boolean modifiesTable() {
            return true; // force une copie du graphe et de la table
        }
    };

    private Set<Node> nodes;

    public static AtomicLong checkPrecondsTimeProfiler = new AtomicLong(0);
    public static AtomicLong pathfinderProfiler = new AtomicLong(0);
    public static AtomicLong precondMet = new AtomicLong(0);

    public ActionGraph() {
        this((o1, o2) -> 0); // aucun tri par défaut
    }

    public ActionGraph(Comparator<EnvironmentInfo> stateComparator) {
        nodes = new HashSet<>();
        this.stateComparator = stateComparator;
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

        List<Node> usableNodes = new LinkedList<>(nodes);
        List<Node> path = new LinkedList<>();

        EnvironmentInfo.copyWithEffectsProfiler.set(0);
        checkPrecondsTimeProfiler.set(0);
        pathfinderProfiler.set(0);
        precondMet.set(0);
        boolean foundPath = buildPathToGoal(startNode, path, usableNodes, info, goal);
        long time = EnvironmentInfo.copyWithEffectsProfiler.getAndSet(0);
        Log.AI.debug("copyWithEffects: "+time+" ns ("+time/1_000_000+"ms)");

        time = checkPrecondsTimeProfiler.getAndSet(0);
        Log.AI.debug("checkPrecondsTimeProfiler: "+time+" ns ("+time/1_000_000+"ms)");

        time = pathfinderProfiler.getAndSet(0);
        Log.AI.debug("pathfinderProfiler: "+time+" ns ("+time/1_000_000+"ms)");

        time = precondMet.getAndSet(0);
        Log.AI.debug("precondMet: "+time+" ns ("+time/1_000_000+"ms)");
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

      //  long startStackTime = System.nanoTime();
      //  Log.AI.debug("Création du stack:");
        while(n != null) {
            if(n.action != null) { // on évite d'ajouter le noeud de départ au chemin
             //   Log.AI.debug(">> "+n.getAction());
                //result.insertElementAt(n, 0);
                // TODO: check order
                // TODO: insertElementAt a l'air d'être un poil plus lent, faudrait voir pour remplacer par une Queue
                result.push(n);
            }
            n = n.parent;
        }
       /* long elapsed = (System.nanoTime()-startStackTime);
        Log.AI.debug("la création du stack a pris "+elapsed+"ns ("+elapsed/1000000+"ms)");*/
        return result;
    }

    private boolean buildPathToGoal(Node startNode, List<Node> path, List<Node> usableNodes, EnvironmentInfo info, EnvironmentInfo goal) {
        ExecutorService executor = Executors.newWorkStealingPool();
        Stream<Boolean> futures = usableNodes.parallelStream()
                .map(node -> {
                    try {
                        return executor.submit(() -> {
                            boolean result = GoapPathfinder.findPath(node, startNode, path, usableNodes, info.copyWithEffects(copyAction), goal, 0);
                            Log.AI.debug("> Finished with "+node.getAction()+" result is "+result);
                            Log.AI.debug("> Set result "+result);
                            return result;
                        }).get();
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }
                    return false;
                });

        List<Boolean> futureList = futures.collect(Collectors.toList());
        return futureList.stream().findAny().get();
    }



    public Action getCopyAction() {
        return copyAction;
    }
}
