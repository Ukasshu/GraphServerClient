import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
/**
 * Class used to hold information about connected Nodes before converting them into a graph
 *
 * @author Łukasz Mielczarek
 * @version 21.10.2016
 */
public class Way implements Serializable{
    /**
     * Container used to hold Nodes included in the Way
     */
    private ArrayList<Node> nodes;
    /**
     * Information what type the way is
     */
    private String type;
    /**
     * The name of way (e.g. street name)
     */
    private String name;
    /**
     * List of types that will be counted in creating graph
     */
    private static final List<String> allowedTypes = Arrays.asList("motorway", "trunk", "primary", "secondary", "tertiary", "unclassified", "residential",  "living_street", "pedestrian",  "primary_link", "secondary_link", "tertiary_link", "trunk_link", "motorway_link");
    /**
     * Holds an information whether the way is a roundabout
     */
    private boolean roundabout;

    /**
     * Default Constructor
     */
    public Way(){
        nodes = new ArrayList<>();
        type = null;
        name = null;
        roundabout = false;
    }

     /**
     * Adds Node included in the Way
     * @param node Node included in the way
     */
    public void addNode(Node node){
        this.nodes.add(node);
    }

    /**
     * Checks whether the Way is correct (if it is correct or if its type contains in allowedTypes)
      * @return true if it is correct, false if not
     */
    public boolean isCorrect(){
        return ((!this.nodes.get(0).getId().equals(this.nodes.get(this.nodes.size() -1).getId())) || this.roundabout) && (allowedTypes.contains(this.type));
    }

    /**
     * Sets type of the Way
     * @param type String name of the type of Way
     */
    public void setType(String type){
        this.type =type;
    }

    /**
     * Returns the type of the Way
     * @return type of way
     */
    public String getType(){
        return this.type;
    }

    /**
     * Sets the name of Way
     * @param name String with name of way (e.g. street name)
     */
    public void setName(String name){
        this.name = name;
    }

    /**
     * Returns the name of Way
     * @return String name of the Way (e.g. street name)
     */
    public String getName(){
        return this.name;
    }

    /**
     * Returns ArrayList of Nodes included in the way
     * @return
     */
    public ArrayList<Node> getNodes(){
        return nodes;
    }

    /**
     * Sets the way as the roundabout
     */
    public void setRoundabout(){
        this.roundabout = true;
    }

    /**
     * Returns if the way is a roundabout
     * @return true if the way is a roundabout, false if not
     */
    public boolean isRoundabout(){
        return this.roundabout;
    }
}
