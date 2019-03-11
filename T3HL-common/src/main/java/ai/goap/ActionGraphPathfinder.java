package ai.goap;

import utils.math.Vec2;
import utils.math.VectCartesian;

import java.util.*;

public class ActionGraphPathfinder implements Comparator<ActionGraphPathfinder.PathfinderNode> {

    public static class PathfinderNode {

        private final ActionGraph.Node node;
        private final EnvironmentInfo info;

        public PathfinderNode(ActionGraph.Node node, EnvironmentInfo info) {
            this.node = node;
            this.info = info;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            PathfinderNode that = (PathfinderNode) o;
            return Objects.equals(node, that.node) &&
                    Objects.equals(info, that.info);
        }

        @Override
        public int hashCode() {
            return Objects.hash(node, info);
        }

        @Override
        public String toString() {
            return "PathfinderNode{" +
                    "node=" + node +
                    ", info=" + info +
                    '}';
        }

    }

    private static final Object LOCK = new Object();
    private final ActionGraph graph;
    private PathfinderNode currentNode;

    /**
     * Liste des noeuds à visité
     */
    private PriorityQueue<PathfinderNode> openList;
    /**
     * Liste des noeuds déjà visités
     */
    private LinkedList<PathfinderNode> closedList;

    public ActionGraphPathfinder(ActionGraph graph) {
        this.graph = graph;
        openList = new PriorityQueue<>(this);
        closedList = new LinkedList<>();
    }

    public boolean findPath(ActionGraph.Node startNode, ActionGraph.Node startingParent, List<ActionGraph.Node> leaves, List<ActionGraph.Node> usableNodes, EnvironmentInfo startingInfo, EnvironmentInfo goal, int depth) {
        if(goal.isMetByState(startingInfo)) // on est déjà arrivé au but!
            return true;

     /*   boolean foundAtLeastOnePath = false;
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
                    path.add(clone);
                }
                foundAtLeastOnePath = true;
            } else {
                // on retire cette action de la liste des actions possibles
                List<ActionGraph.Node> newUsableNodes = usableNodes.stream().filter(n -> n != actionNode).collect(Collectors.toList());
                boolean foundSubpath = buildPathToGoal(actionNode.cloneWithParent(parent, runningCost), path, newUsableNodes, newState, goal, depth+1); // on continue à parcourir l'arbre

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
        return foundAtLeastOnePath;*/

       openList.clear();
       closedList.clear();
       startNode.setParentForCurrentThread(startingParent);
       openList.add(new PathfinderNode(startNode, startingInfo));
       while(!openList.isEmpty()) {
           PathfinderNode moreInfo = openList.poll();
           ActionGraph.Node node = moreInfo.node;
           ActionGraph.Node parent = moreInfo.node.getParentForCurrentThread();
           EnvironmentInfo info = moreInfo.info;
           this.currentNode = moreInfo;
        //   System.out.println(">> "+moreInfo);
           EnvironmentInfo newState = info.copyWithEffects(node.getAction());
           if(node.requiresMovement(newState)) {
               node.updateTargetPosition(newState, newState.getXYO().getPosition()); // mise à jour de la position de l'IA
               // TODO: angle
           }

           double runningCost = parent.getRunningCostForCurrentThread() + node.getCost(info, depth);
           if(goal.isMetByState(newState)) {
               synchronized (LOCK) {
                   rebuildGraph(leaves, node, parent, runningCost);
               }
               return true;
           }

           usableNodes.stream().filter(n -> n != node).forEach(actionNode -> {
               PathfinderNode newPathNode = new PathfinderNode(actionNode, newState);
               if(!closedList.contains(newPathNode)) {
                   if(actionNode.getAction().arePreconditionsMet(newState)) {
                       actionNode.setParentForCurrentThread(node);
                       actionNode.setRunningCostForCurrentThread(runningCost);
                       openList.add(newPathNode);
                   }
               }
           });

           closedList.add(moreInfo);
       }


       return false;
    }

    private ActionGraph.Node deepClone(ActionGraph.Node node) {
        ActionGraph.Node parent = node.getParentForCurrentThread();
      //  System.out.println(">> "+parent);
        if(parent != null) {
            return node.cloneWithParent(deepClone(parent), 0.0);
        }
        return node.cloneWithParent(null, 0.0);
    }

    private void rebuildGraph(List<ActionGraph.Node> leaves, ActionGraph.Node node, ActionGraph.Node parent, double runningCost) {
        ActionGraph.Node clone = node.cloneWithParent(deepClone(parent), runningCost);
        leaves.add(clone);
    }

    @Override
    public int compare(ActionGraphPathfinder.PathfinderNode a, ActionGraphPathfinder.PathfinderNode b) {
        double aDist, bDist;
        Vec2 tmpVec = new VectCartesian(0,0);
        EnvironmentInfo currentInfo = currentNode.info;
        if(a.node.requiresMovement(currentInfo)) {
            a.node.updateTargetPosition(currentInfo, tmpVec);
            aDist = tmpVec.squaredDistanceTo(currentNode.info.getCurrentPosition());
        } else {
            aDist = 0.0;
        }
        if(b.node.requiresMovement(currentInfo)) {
            b.node.updateTargetPosition(currentInfo, tmpVec);
            bDist = tmpVec.squaredDistanceTo(currentNode.info.getCurrentPosition());
        } else {
            bDist = 0.0;
        }
        int distCompare = Double.compare(aDist, bDist);

        aDist = a.info.distanceTo(currentInfo);
        bDist = b.info.distanceTo(currentInfo);
        int stateCompare = Double.compare(aDist, bDist);

        return -distCompare/* plus c'est proche, mieux c'est */ + -stateCompare;
    }
}
