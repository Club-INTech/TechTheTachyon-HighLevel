package locomotion;

import data.graphe.Node;
import java.util.Comparator;
import java.util.Map;

/**
 * Utilisé par la PriorityQueue
 */
public class NodeComparator implements Comparator<Node> {

    private final Map<Node, Double> heuristiques;

    public NodeComparator(Map<Node, Double> heuristiques) {
        this.heuristiques = heuristiques;
    }

    /**
     * @see Comparator
     */
    @Override
    public int compare(Node n1, Node n2) {
        if(n1 == null)
            return 1;
        if(n2 == null)
            return -1;
        return Double.compare(heuristiques.getOrDefault(n1, Node.DEFAULT_HEURISTIC), heuristiques.getOrDefault(n2, Node.DEFAULT_HEURISTIC));
    }
}
