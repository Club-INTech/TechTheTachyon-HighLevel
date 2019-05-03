package ai.goap;

import utils.Log;

import java.util.List;
import java.util.stream.Collectors;

public class GoapPathfinder {

    public static final Object LOCK = new Object();

    private GoapPathfinder() {
    }

    private static boolean buildPathToGoal(ActionGraph.Node parent, List<ActionGraph.Node> leaves, List<ActionGraph.Node> usableNodes, EnvironmentInfo info, EnvironmentInfo goal, int depth) {
        if(goal.isMetByState(info)) {
            // on est déjà arrivé au but!
            return true;
        }
        boolean foundAtLeastOnePath = false;
        for (int i = 0; i < usableNodes.size(); i++) {
            boolean foundSubPath = findPath(usableNodes.get(i), parent, leaves, usableNodes, info, goal, depth);
            if(foundSubPath)
                foundAtLeastOnePath = true;
        }

        return foundAtLeastOnePath;
    }

    public static boolean findPath(ActionGraph.Node actionNode, ActionGraph.Node parent, List<ActionGraph.Node> leaves, List<ActionGraph.Node> usableNodes, EnvironmentInfo info, EnvironmentInfo goal, int depth) {
        if(goal.isMetByState(info)) {
            // on est déjà arrivé au but!
            return true;
        }
        if(depth >= 3) {
            if(actionNode.getAction().arePreconditionsMet(info)) {
                synchronized (LOCK) {
                    leaves.add(actionNode.cloneWithParent(parent, parent.runningCost + actionNode.getCost(info, depth)));
                }
                return true; // TODO: test only ? (ou pas :D)
            }
            return false;
        }
        boolean foundAtLeastOnePath = false;
        long startTime = System.currentTimeMillis();
        StringBuilder depthStr = new StringBuilder();
        for (int i = 0; i < depth; i++) {
            depthStr.append(">");
        }
        if(depth == 0)
            Log.AI.debug(">"+depthStr+" Testing "+actionNode.getAction());
        long startPrecondTime = System.nanoTime();
        boolean checkPreconds = actionNode.getAction().arePreconditionsMet(info);
        ActionGraph.checkPrecondsTimeProfiler.addAndGet(System.nanoTime()-startPrecondTime);
        if(checkPreconds) { // noeud utilisable
            //     Log.AI.debug("Can be executed: "+actionNode.getAction());
            EnvironmentInfo newState = info.copyWithEffects(actionNode.getAction());
            if(actionNode.requiresMovement(newState)) {
                actionNode.updateTargetPosition(newState, newState.getXYO().getPosition()); // mise à jour de la position de l'IA
                // TODO: angle
            }
            double runningCost = parent.runningCost + actionNode.getCost(info, depth);
            if(goal.isMetByState(newState)) {
                ActionGraph.Node clone = actionNode.cloneWithParent(parent, runningCost);
                synchronized (LOCK) {
                    leaves.add(clone);
                }
                foundAtLeastOnePath = true;
            } else {
                // on retire cette action de la liste des actions possibles
                List<ActionGraph.Node> newUsableNodes = usableNodes.stream().filter(n -> n != actionNode).collect(Collectors.toList());
                boolean foundSubpath = buildPathToGoal(actionNode.cloneWithParent(parent, runningCost), leaves, newUsableNodes, newState, goal, depth+1); // on continue à parcourir l'arbre

                if(foundSubpath) {
                    foundAtLeastOnePath = true;
                }
            }
            //newState.getSpectre().destroy();
        }
        long elapsed = (System.currentTimeMillis()-startTime);
        if(depth == 0)
            Log.AI.debug(">"+depthStr+" "+elapsed+" for "+actionNode.getAction()+" usableNodes = "+
                    usableNodes.stream().map(n -> n.getAction().toString()).collect(Collectors.joining(", ")));

        if(!foundAtLeastOnePath) {
            leaves.add(actionNode.cloneWithParent(parent, 1000000000));
            return true;
        }
        return foundAtLeastOnePath;
    }
}
