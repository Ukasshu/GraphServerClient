import DataConverterExceptions.DataNotConvertedYetException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ListIterator;

/**
 * Class providing data converting
 *
 * @author Łukasz Mielczarek
 * @version 21.10.2016
 */
public class DataConverter implements Serializable{
    /**
     * Holds Nodes provided by MapReader that are changed then
     */
    private HashMap<String, Node> nodes;
    /**
     * Holds Ways provided by MapReader
     */
    private ArrayList<Way> ways;
    /**
     * Remembers if the data have been converted
     */
    private boolean areDataConverted;
    /**
     * Holds Nodes that will be from HashMap
     */
    private ArrayList<Node> unnecessaryNodes;

    /**
     * DataConverter's constructor
     * @param nodes HashMap with Nodes read from file
     * @param ways ArrayList with Ways read from file
     */
    public DataConverter(HashMap<String, Node> nodes, ArrayList<Way> ways){
        this.nodes = nodes;
        this.ways = ways;
        this.unnecessaryNodes = new ArrayList<>();
        this.areDataConverted = false;
    }

    /**
     * Marks Nodes that will be removed
     */
    private void markUnnecessaryNodes(){
        Node currentNode;
        ListIterator<Node> iterator;
        for(Way way: ways){
            iterator = way.getNodes().listIterator(0);
            if(!way.isRoundabout()){
                currentNode = iterator.next();
                currentNode.setNeeded();
            }
            currentNode = iterator.next();
            while(iterator.hasNext()){
                if(currentNode.getWaysCounter() <=1){
                    iterator.remove();
                    unnecessaryNodes.add(currentNode);
                }
                else{
                    currentNode.setNeeded();
                }
                currentNode = iterator.next();
            }
            if(!way.isRoundabout()){
                currentNode.setNeeded();
            }
            else if(currentNode.getWaysCounter()<=1){
                iterator.remove();
            }
        }
    }

    /**
     * Removes unnecessary Nodes
     */
    private void removeUnnecessaryNodes(){
        for(Node node: nodes.values()){
            if(!node.isNeeded()){
                unnecessaryNodes.add(node);
            }
        }
        for(Node node: unnecessaryNodes){
            nodes.remove(node.getId());
        }
    }

    /**
     * Adds connections to the Nodes that are connected on map
     */
    private void convertWaysIntoEdges(){
        ListIterator<Node> iterator;
        Node previousNode, currentNode;
        for(Way way: ways){
            iterator = way.getNodes().listIterator(0);
            if(iterator.hasNext()) {
                currentNode = iterator.next();
                while (iterator.hasNext()) {
                    previousNode = currentNode;
                    currentNode = iterator.next();
                    previousNode.addEdge(currentNode);
                    currentNode.addEdge(previousNode);
                }
            }
        }
    }

    /**
     * Calls all methods needed to convert data
     */
    public void runConverter(){
        this.markUnnecessaryNodes();
        this.removeUnnecessaryNodes();
        this.convertWaysIntoEdges();
        this.areDataConverted = true;
    }

    /**
     * Returns container with converted data
     * @return
     * @throws DataNotConvertedYetException
     */
    public HashMap<String, Node> returnConvertedNodes() throws DataNotConvertedYetException{
        if(!areDataConverted){
            throw new DataNotConvertedYetException("There was an attempt to return data without converting them!");
        }
        return nodes;
    }
}