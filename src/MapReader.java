import java.io.File;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import MapReaderExceptions.*;

/**
 * Class created to read data from file
 *
 * @author Łukasz Mielczarek
 * @version 20.10.2016
 */
public class MapReader implements Serializable{
    /**
     * Represents opened file
     */
    private File file;
    /**
     * Used to read read file
     */
    private transient Scanner input;
    /**
     * Remembers if Nodes are already read
     */
    private boolean areNodesAlreadyRead;
    /**
     * Remembers if Ways are already read
     */
    private boolean areWaysAlreadyRead;
    /**
     * Remembers if bounds are already read
     */
    private boolean areBoundsAlreadyRead;
    /**
     * Holds currently processed line of the file
     */
    private String currentLine;
    /**
     * Holds all read Nodes
     */
    private HashMap<String, Node> nodes;
    /**
     * Holds all read Ways
     */
    private ArrayList<Way> ways;
    /**
     * Holds boundaries of map represented by file
     */
    private Double bounds[];

    /**
     * Default constructor
     */
    MapReader(){
        this.areNodesAlreadyRead = false;
        this.areWaysAlreadyRead = false;
        this.areBoundsAlreadyRead = false;
    }

    /**
     * Opens file that will be read
     * @param filename path to the file that will be read
     * @throws FileNotFoundException if file cannot be opened
     */
    public void openFile(String filename) throws FileNotFoundException{
        this.file = new File(filename);
        areNodesAlreadyRead = false;
        areWaysAlreadyRead = false;
        areBoundsAlreadyRead = false;
    }

    /**
     * Opens stream to the file
     * @throws FileNotFoundException if the file cannot be read
     */
    public void openStream() throws FileNotFoundException {
        this.input = new Scanner(file);
    }

    /**
     * Reads boundaries of the map
     * @throws NodesAlreadyReadException if is called after reading Nodes
     */
    private void readBounds() throws NodesAlreadyReadException, BoundsAlreadyReadException{
        if (areNodesAlreadyRead){
            throw new NodesAlreadyReadException("Too late to read bounds!");
        }
        if (areBoundsAlreadyRead){
            throw new BoundsAlreadyReadException("Bounds have been already read!");
        }
        bounds = new Double[4];
        this.currentLine = input.nextLine();
        while(!this.currentLine.matches("(.*)<bounds minlat(.*)")){
            this.currentLine = input.nextLine();
        }
        bounds[0] = Double.parseDouble(this.currentLine.substring(this.currentLine.indexOf("minlat")+8, this.currentLine.indexOf('\"', this.currentLine.indexOf("minlat")+8)));
        bounds[1] = Double.parseDouble(this.currentLine.substring(this.currentLine.indexOf("minlon")+8, this.currentLine.indexOf('\"', this.currentLine.indexOf("minlon")+8)));
        bounds[2] = Double.parseDouble(this.currentLine.substring(this.currentLine.indexOf("maxlat")+8, this.currentLine.indexOf('\"', this.currentLine.indexOf("maxlat")+8)));
        bounds[3] = Double.parseDouble(this.currentLine.substring(this.currentLine.indexOf("maxlon")+8, this.currentLine.indexOf('\"', this.currentLine.indexOf("maxlon")+8)));
        areBoundsAlreadyRead = true;
    }

    /**
     * Reads all Nodes from file
     * @throws NodesAlreadyReadException if Nodes have been already read
     */
    private void readNodes() throws NodesAlreadyReadException {
        if(areNodesAlreadyRead){
            throw new NodesAlreadyReadException("Nodes have been already read from this file! Reopen file to do this again.");
        }
        nodes = new HashMap<>();
        String tempId;
        double tempLat;
        double tempLon;
        this.currentLine = input.nextLine();
        while(!currentLine.matches("(.*)<way (.*)") && input.hasNextLine()){
            if(currentLine.matches("(.*)<node (.*)")){
                tempId = this.currentLine.substring(this.currentLine.indexOf("id")+4, this.currentLine.indexOf('\"',this.currentLine.indexOf("id")+4));
                tempLat = Double.parseDouble(this.currentLine.substring(this.currentLine.indexOf("lat")+5, this.currentLine.indexOf('\"',this.currentLine.indexOf("lat")+5)));
                tempLon = Double.parseDouble(this.currentLine.substring(this.currentLine.indexOf("lon")+5, this.currentLine.indexOf("\"", this.currentLine.indexOf("lon")+5)));
                nodes.put(tempId, new Node(tempId, tempLat, tempLon));
            }
            this.currentLine =input.nextLine();
        }
        areNodesAlreadyRead = true;
    }

    /**
     * Reads all Ways from file and throws off incorrect ones
     * @throws NodesNotReadYetException if it's called to early
     * @throws WaysAlreadyReadException if Ways have been already read
     */
    private void readWays() throws NodesNotReadYetException, WaysAlreadyReadException{
        if(!areNodesAlreadyRead){
            throw new NodesNotReadYetException("There was an attempt of reading ways without nodes!");
        }
        if(areWaysAlreadyRead){
            throw new WaysAlreadyReadException("Ways have been already read from this file! Reopen file to do this again.");
        }
        ways = new ArrayList<>();
        while(input.hasNextLine() ) {
            if (currentLine.matches("(.*)<way(.*)")) {
                Way newWay = new Way();
                String nodeId;
                currentLine = input.nextLine();
                while (currentLine.matches("(.*)<nd ref(.*)")) {
                    nodeId = currentLine.substring(currentLine.indexOf("ref") + 5, currentLine.indexOf("\"", currentLine.indexOf("ref") + 5));
                    newWay.addNode(nodes.get(nodeId));
                    currentLine = input.nextLine();
                }
                while(!currentLine.matches("(.*)</way>(.*)")){
                    if(currentLine.matches("(.*)tag k=\"highway\"(.*)")){
                        newWay.setType(currentLine.substring(currentLine.indexOf("v=\"")+3, currentLine.indexOf('\"', currentLine.indexOf("v=\"")+3)));
                    }
                    else if(currentLine.matches("(.*)tag k=\"name\"(.*)")){
                        newWay.setName(currentLine.substring(currentLine.indexOf("v=\"")+3, currentLine.indexOf('\"', currentLine.indexOf("v=\"")+3)));
                    }
                    else if(currentLine.matches("(.*)tag k=\"junction\" v=\"roundabout\"(.*)")){
                        newWay.setRoundabout();
                        newWay.getNodes().remove(newWay.getNodes().size()-1);
                    }
                    currentLine = input.nextLine();
                }
                if(newWay.isCorrect()){
                    for(Node n: newWay.getNodes()){
                        n.increaseWaysCounter();
                    }
                    ways.add(newWay);
                }
            }
            currentLine = input.nextLine();
        }
        areWaysAlreadyRead=true;
    }

    /**
     * Calls all method needed to read the file
     * @throws BoundsAlreadyReadException if readBounds was called again
     * @throws NodesAlreadyReadException if readBounds was called after Nodes reading or readNodes was called again
     * @throws NodesNotReadYetException if readWay was called before reading Node
     * @throws WaysAlreadyReadException if readWay was called again
     * @throws FileNotFoundException if occurred a problem with file
     */
    public void runReader() throws BoundsAlreadyReadException, NodesAlreadyReadException, NodesNotReadYetException, WaysAlreadyReadException, FileNotFoundException{
        this.openStream();
        this.readBounds();
        this.readNodes();
        this.readWays();
        this.input.close();
    }

    /**
     * Returns container with read Nodes
     * @return HashMap with Nodes
     * @throws NodesNotReadYetException if Nodes have not been read yet
     */
    public HashMap<String, Node> getNodes() throws NodesNotReadYetException{
        if(!areNodesAlreadyRead){
            throw new NodesNotReadYetException("Try to get nodes after reading them!");
        }
        return nodes;
    }

    /**
     * Returns boundaries of the map
     * @return array with boundaries
     * @throws BoundsNotReadYetException if boundaries have not been read yet
     */
    public Double[] getBounds() throws BoundsNotReadYetException {
        if(!areBoundsAlreadyRead){
            throw new BoundsNotReadYetException("Bounds have not been read yet!");
        }
        return bounds;
    }

    /**
     * Returns container with Ways
     * @return ArrayList with Ways
     * @throws WaysNotReadYetException if Ways have not been read yet
     */
    public ArrayList<Way> getWays() throws WaysNotReadYetException{
        return ways;
    }
}
