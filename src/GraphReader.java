import java.io.File;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ListIterator;
import java.util.Scanner;

import WayFinderExceptions.*;

/**
 * Class which provides reading graph from file
 *
 * @author Łukasz Mielczarek
 * @version 04.11.2016
 */
public class GraphReader implements Serializable{
    /**
     * Represents opened file
     */
    private File file;
    /**
     * Stream that reads the file
     */
    private transient Scanner input;
    /**
     * Holds all read Nodes
     */
    private HashMap<String, Node> graph;
    /**
     * Holds the line of file that is currently analysed
     */
    private String currentLine;
    /**
     * Remembers is the opened file has been already read
     */
    private boolean graphRead;

    /**
     * Default constructor
     */
    public GraphReader(){
        this.file = null;
        this.input = null;
        this.graph = null;
    }

    /**
     * Opens stream to the file that will be read
     * @param filepath path to the file that will be read
     * @throws FileNotFoundException if the file cannot be opened
     */
    public void openFile(String filepath) throws FileNotFoundException{
        if(input!=null){
            input.close();
        }
        file = new File(filepath);
        graphRead = false;
        input = new Scanner(file);
        graph = new HashMap<>();
    }

    /**
     * Reads the ways from file and converts them into edges
     */
    private void readEdges(){
        ArrayList<String> ids = null;
        ArrayList<Double> distances = null;
        currentLine = input.nextLine();
        while(!currentLine.matches("(.*)\"nodes\"(.*)") && input.hasNextLine()){
            if(currentLine.matches("(.*)\"nodesRef\":(.*)")){
                ids = new ArrayList<>();
                distances = new ArrayList<>();
                currentLine = input.nextLine();
                while(currentLine.matches("(.*)[0-9]+(.*)")){
                    currentLine = currentLine.replaceAll("\\D+", "");
                    ids.add(currentLine);
                    currentLine = input.nextLine();
                }
                input.nextLine();
                currentLine = input.nextLine();
                while(currentLine.matches("(.*)[0-9]+[.]?[0-9]*(.*)")){
                    currentLine = currentLine.replaceAll("[^\\d.]", "");
                    distances.add(Double.parseDouble(currentLine));
                    currentLine = input.nextLine();
                }
                ListIterator<String> idIterator = ids.listIterator(0);
                String current = idIterator.next();
                Node previousNode = null;
                Node currentNode = null;
                //Double dist = null;
                if(!graph.containsKey(current)){
                    currentNode = new Node(current);
                    graph.put(current, currentNode);
                }
                else{
                    currentNode = graph.get(current);
                }
                while(idIterator.hasNext()){
                    previousNode = currentNode;
                    current = idIterator.next();
                    if(!graph.containsKey(current)) {
                        currentNode = new Node(current);
                        graph.put(current, currentNode);
                    }
                    else{
                        currentNode = graph.get(current);
                    }
                    currentNode.addEdge(previousNode);
                    previousNode.addEdge(currentNode);
                }
            }
            currentLine = input.nextLine();
        }
    }

    /**
     * Reads the coordinates of the Nodes
     */
    private void readNodes(){
        String id;
        double longitude;
        double latitude;
        while(input.hasNextLine()){
            if(currentLine.matches("(.*)[{](.*)")){
                currentLine = input.nextLine();
                id = currentLine.replaceAll("[^\\d.]","");
                currentLine = input.nextLine();
                latitude = Double.parseDouble(currentLine.replaceAll("[^\\d.]",""));
                currentLine = input.nextLine();
                longitude = Double.parseDouble(currentLine.replaceAll("[^\\d.]",""));
                if(graph.containsKey(id)) {
                    graph.get(id).setCoordinates(longitude, latitude);
                }
            }
            currentLine = input.nextLine();
        }
    }

    /**
     * Returns HashMap with all Nodes
     * @return HashMap with all Nodes
     * @throws GraphNotReadYetException if method is called before reading the whole file
     */
    public HashMap<String, Node> returnGraph() throws GraphNotReadYetException {
        if(!graphRead){
            throw new GraphNotReadYetException("Graph has not been already read!");
        }
        return graph;
    }

    /**
     * Method that calls methods providing reading the file
     */
    public void readGraph(){
        this.readEdges();
        this.readNodes();
        this.graphRead = true;
        for(Node n: graph.values()){
            n.calculateDistances();
        }
    }

}
