import java.io.Serializable;
import java.util.*;

/**
 * Class which provides finding the shortest way on graph
 *
 * @author Łukasz Mielczarek
 * @version 04.11.2016
 */
public class WayFinder implements Serializable{
    /**
     * HashMap holding all Nodes
     */
    private HashMap<String, Node> graph;
    /**
     * PriorityQueue used in Dijksrta's algorithm
     */
    private PriorityQueue<Node> queue;
    /**
     * ArrayList that holds the shortest way found on graph
     */
    private ArrayList<Node> foundWay;
    /**
     * Comparator used to determine the Nodes' priority in the PriorityQueue
     */
    private Comparator comparator;

    /**
     * WayFinder's construcyor
     * @param graph HashMap with all read Nodes
     */
    public WayFinder(HashMap<String, Node> graph){
        this.graph = graph;
        this.foundWay = new ArrayList<>();
    }

    /**
     * Starts the work of the WayFinder with Dijkstra's algorithm
     * @param start ID of the start Node
     * @param goal ID of the goal Node
     */
    public void runFinderDijkstra(String start, String goal){
        this.comparator = new NodeComparator();
        this.queue = new PriorityQueue<>(comparator);
        for(Node n: graph.values()){
            n.setDistance(Double.MAX_VALUE);
            n.setPreviousNode(null);
        }
        graph.get(start).setDistance((double) 0);
        this.queue.add(graph.get(start));
        dijkstra();
        extractShortestWay(start, goal);
    }

    /**
     * Implements working of the Djikstra's algorithm
     */
    private void dijkstra(){
        Node node, tmp;
        while(!queue.isEmpty()){
            node = queue.poll();
            for(int i = 0; i< node.getEdges().size(); i++){
                tmp = node.getEdges().get(i);
                if(tmp.getDistance() > node.getDistance() + node.distance(tmp)){
                    tmp.setDistance(node.getDistance() + node.distance(tmp));
                    tmp.setPreviousNode(node);
                    queue.add(tmp);
                }
            }
        }
    }

    /**
     * Extracts the path which is the shortest way from start to goal
     * @param start ID of the start Node
     * @param goal ID of the goal Node
     */
    private void extractShortestWay(String start, String goal){
        Node current = graph.get(goal);
        foundWay = new ArrayList<>();
        while(current != null){
            foundWay.add(current);
            current = current.getPreviousNode();
        }
        Collections.reverse(foundWay);
        if(!foundWay.get(0).equals(graph.get(start))){
            foundWay = null;
        }
    }

    /**
     * Implements working of A* algorithm
     * @param start ID of the start Node
     * @param goal ID of the goal Node
     */
    private void aStar(String start, String goal){
        Node tmp, node;
        double dst, tmpDst;
        ArrayList<Node> visitedNodes = new ArrayList<>();
        this.queue.add(graph.get(start));
        while(!this.queue.isEmpty()){
            node = queue.poll();
            if(node.getId() == goal) {
                return;
            }
            visitedNodes.add(node);
            for(int i =0; i<node.getEdges().size(); i++){
                tmp = node.getEdges().get(i);
                dst = node.distance(tmp);
                if(visitedNodes.contains(tmp)){
                    continue;
                }
                tmpDst = node.getDistance() + dst;
                if(!queue.contains(tmp)){
                    tmp.setHeuristic(tmp.distance(graph.get(goal)));
                    tmp.setPreviousNode(node);
                    tmp.setDistance(tmpDst);
                    tmp.setForecastedDistance(tmp.getDistance()+tmp.getHeuristic());
                    queue.add(tmp);
                }
                else if(tmpDst < tmp.getDistance()){
                    tmp.setPreviousNode(node);
                    tmp.setDistance(tmpDst);
                    tmp.setForecastedDistance(tmp.getDistance()+tmp.getHeuristic());
                }

            }

        }
    }

    /**
     * Starts the work of the WayFinder with Dijkstra's algorithm
     * @param start ID of the start Node
     * @param goal ID of the goal Node
     */
    public void runFinderAStar(String start, String goal){
        for(Node n: graph.values()){
            n.setDistance(Double.MAX_VALUE);
            n.setPreviousNode(null);
            n.setForecastedDistance(Double.MAX_VALUE);
        }
        this.comparator = new NodeComparatorAStar();
        this.queue = new PriorityQueue<>(comparator);
        this.graph.get(start).setDistance(0.0);
        this.queue.add(this.graph.get(start));
        aStar(start, goal);
        extractShortestWay(start, goal);
    }

    /**
     * Returns ArrayList with the shortest way from start to goal
     * @return
     */
    public ArrayList<Node> getFoundWay(){
        return foundWay;
    }
}
