package locomotion;

import data.graphe.Node;
import java.util.Comparator;
import java.util.Map;

/**
 * Utilisé par la PriorityQueue
 */
public class ComparatorNode implements Comparator<Node> {

    private final Map<Node, Double> heuristiques;

    public ComparatorNode(Map<Node, Double> heuristiques) {
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
        if(heuristiques.get(n1) == null)
            return -1;
        if(heuristiques.get(n2) == null)
            return -1;
        if (heuristiques.get(n1) > heuristiques.get(n2)){
            return 1;
        } else if (heuristiques.get(n1) < heuristiques.get(n2)) {
            return -1;
        } else {
            return 0;
        }
    }
}
