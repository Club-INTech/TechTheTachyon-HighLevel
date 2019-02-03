package locomotion;

import data.graphe.Node;
import java.util.Comparator;

/**
 * Utilisé par la PriorityQueue
 */
public class ComparatorNode implements Comparator<Node> {

    /**
     * @see Comparator
     */
    @Override
    public int compare(Node n1, Node n2) {
        if (n1.getHeuristique() > n2.getHeuristique()){
            return 1;
        } else if (n1.getHeuristique() < n2.getHeuristique()) {
            return -1;
        } else {
            return 0;
        }
    }
}
