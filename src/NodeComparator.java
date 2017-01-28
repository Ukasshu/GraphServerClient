import java.io.Serializable;
import java.util.Comparator;

/**
 * Comparator providing sorting Nodes in PriorityQueue
 * @author Łukasz Mielczarek
 * @version 05.11.16
 */
public class NodeComparator implements Comparator<Node>, Serializable{

    /**
     * Compares distance from the start Node
     * @param node1 first Node
     * @param node2 second Node
     * @return -1 if first Node has lower distance, 1 if second has, 0 if they both have the same one
     */
    public int compare(Node node1, Node node2){
        double dst1 = node1.getDistance();
        double dst2 = node2.getDistance();
        if(dst1 < dst2){
            return -1;
        }
        if(dst1 > dst2){
            return 1;
        }
        return 0;
    }
}
