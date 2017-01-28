import java.io.Serializable;
import java.util.ArrayList;

/**
 * Class holding information about graph vertex
 *
 * @author Łukasz Mielczarek
 * @version 16.12.2016
 */
public class Node implements Serializable{
    /**
     * Holds the id of the Node
     */
    private String id;
    /**
     * Holds the latitude of the point on map
     */
    private double latitude;
    /**
     * Holds the longitude of the point on map
     */
    private double longitude;
    /**
     * Holds information how many times the point is used
     */
    private int waysCounter;
    /**
     * Holds information whether the point will appear on graph
     */
    private boolean needed;
    /**
     * Holds the references to the other Nodes
     */
    private ArrayList<Node> edges;
    /**
     * Holds the distances to the Nodes from edges
     */
    private ArrayList<Double> distances;
    /**
     * Holds the distance from start Node on the graph
     */
    private double distance = Double.MAX_VALUE;
    /**
     * Used in A* algorithm
     */
    private double heuristic;
    /**
     * Used in A* algorithm
     */
    private double forecastedDistance;
    /**
     * Holds the reference to the previous Node on the shortestWay
     */
    private Node previousNode = null;

    /**
     * Node's constructor
     * @param id the id of the point
     * @param latitude the latitude of the point
     * @param longitude the logitude of the point
     */
    public Node(String id, double latitude, double longitude){
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        waysCounter = 0;
        needed =false;
        edges = new ArrayList<>();
    }

    /**
     * Node's constructor
     * @param id Node's id
     */
    public Node(String id){
        this.id = id;
        this.edges = new ArrayList<>();
        this.distances = new ArrayList<>();
    }

    /**
     * Node's constructor
     * @param latitude latitude of the Node
     * @param longitude longitude of the Node
     */
    public Node(double latitude, double longitude){
        this.latitude = latitude;
        this.longitude = longitude;
        this.id = null;
        this.edges = null;
        this.distances = null;
    }

    /**
     * Sets the coordinates of the Node
     * @param latitude latitude of the Node
     * @param longitude longitude of the Node
     */
    public void setCoordinates(double latitude, double longitude){
        this.longitude = longitude;
        this.latitude = latitude;
    }

    /**
     * Returns ID of the Node
     * @return ID of the Node
     */
    public String getId(){
        return id;
    }

    /**
     * Returns longitude of the Node
     * @return longitude of the Node
     */
    public double getLongitude(){
        return longitude;
    }

    /**
     * Returns latitude of the Node
     * @return latitude of the Node
     */
    public double getLatitude(){
        return latitude;
    }
    /**
     * Returns the number of Node's uses on the map
     * @return value of waysCounter
     */
    public int getWaysCounter(){
        return waysCounter;
    }

    /**
     * Increases waysCounter value by 1
     */
    public void increaseWaysCounter(){
        waysCounter++;
    }

    /**
     * Sets that Node will be used on the graph
     */
    public void setNeeded(){
        this.needed = true;
    }

    /**
     * Returns whether the Node will be used on the graph
     * @return needed value
     */
    public boolean isNeeded(){
        return needed;
    }

    /**
     * Adds reference to the connected Node
     * @param edge reference to the connected Node
     */
    public void addEdge(Node edge){
        this.edges.add(edge);
    }

    /**
     * Returns ArrayList with the connected Nodes
     * @return ArrayList with the connected Nodes
     */
    public ArrayList<Node> getEdges(){
        return edges;
    }

    /**
     * Returns ArrayList with the distances to the connected Nodes
     * @return ArrayList with the distances to the connected Nodes
     */
    public ArrayList<Double> getDistances(){
        return distances;
    }

    /**
     * Sets the distance from start Node
     * @param distance distance from start Node
     */
    public void setDistance(Double distance){
        this.distance = distance;
    }

    /**
     * Sets the previous Node from the shortest way
     * @param previousNode reference to the previous Node
     */
    public void setPreviousNode(Node previousNode){
        this.previousNode = previousNode;
    }

    /**
     * Returns distance to the start Node
     * @return distance to the start Node
     */
    public double getDistance(){
        return distance;
    }

    /**
     * Returns previous Node
     * @return previous Node
     */
    public Node getPreviousNode(){
        return previousNode;
    }

    /**
     * Override of the equals method
     * @param o another object
     * @return if the o is the same object (Node)
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Node node = (Node) o;

        if (Double.compare(node.getLatitude(), latitude) != 0) return false;
        return Double.compare(node.getLongitude(), longitude) == 0;

    }

    /**
     * Returns hash of the Node
     * @return hash of the Node
     */
    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(latitude);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(longitude);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    /**
     * Changes Node to the String representation
     * @return
     */
    @Override
    public String toString(){
        return "id:" + this.id + " lat: " + this.latitude + " lon: "+ this.longitude + " distance from start: " + this.distance;
    }

    /**
     * Return distance to another Node
     * @param node another Node
     * @return distance to another Node
     */
    public double distance(Node node){
        double R = 6371e3;
        double phi1 = Math.toRadians(this.latitude);
        double phi2 = Math.toRadians(node.getLatitude());
        double deltaPhi = Math.toRadians(node.getLatitude() - this.latitude);
        double deltaLambda = Math.toRadians(node.getLongitude() - this.longitude);
        double a = Math.sin(deltaPhi/2)*Math.sin(deltaPhi/2)+Math.cos(phi1)*Math.cos(phi2)*Math.sin(deltaLambda/2)*Math.sin(deltaLambda/2);
        double c = 2*Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return R*c;
    }

    public void calculateDistances(){
        for(int i=0; i<edges.size(); i++){
            distances.add(distance(edges.get(i)));
        }
    }

    public double getForecastedDistance() {
        return forecastedDistance;
    }

    public void setForecastedDistance(double forecastedDistance) {
        this.forecastedDistance = forecastedDistance;
    }

    public double getHeuristic() {
        return heuristic;

    }

    public void setHeuristic(double heuristic) {
        this.heuristic = heuristic;
    }
}
